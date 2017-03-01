/*
 * Copyright 2016-2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.snowdrop.springboot.health;

import java.util.concurrent.TimeUnit;

import com.jayway.awaitility.Awaitility;
import io.fabric8.kubernetes.assertions.Assertions;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.openshift.api.model.Route;
import io.obsidian.testsuite.common.Ensure;
import io.obsidian.testsuite.common.Kube;
import io.restassured.RestAssured;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;

/**
 * Health check application integration test with a healthy indicator.
 *
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
@RunAsClient
@RunWith(Arquillian.class)
public class HealthCheckApplicationHealthyIT {

	private static final String ROUTE_NAME = System.getProperty("route.name", "health-check");

	@ArquillianResource
	private KubernetesClient client;

	private Route route;

	@Before
	public void before() {
		this.route = Kube.createRouteForService(this.client, ROUTE_NAME, true);
		Assertions.assertThat(this.route)
				.isNotNull();

		Ensure.ensureThat("the pod is ready and stay ready for a bit",
				() -> Assertions.assertThat(this.client)
						.deployments()
						.pods()
						.isPodReadyForPeriod());

		Ensure.ensureThat("the route is served correctly", () ->
				Awaitility.await()
						.atMost(1, TimeUnit.MINUTES)
						.until(() -> Kube.isRouteServed(this.route)));
	}

	@Test
	public void shouldGetHealthyResponse() {
		RestAssured.when()
				.get(getHealthUrl())
				.then()
				.statusCode(is(200))
				.body("status", is("UP"))
				.body("custom.status", is("UP"))
				.body("diskSpace.status", is("UP"));
	}

	private String getHealthUrl() {
		return Kube.urlForRoute(this.route)
				.toString() + "/health";
	}

}
