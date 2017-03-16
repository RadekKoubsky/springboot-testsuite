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

package org.jboss.snowdrop.springboot;

import java.util.concurrent.TimeUnit;

import com.jayway.awaitility.Awaitility;
import com.jayway.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

/**
 * Hystrix with Ribbon integration test on OpenShift.
 *
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OpenShiftIT {

	private static final OpenShiftTestAssistant nameServiceAssistant = new OpenShiftTestAssistant("name-service",
			"../name-service/target/classes/META-INF/fabric8/openshift.yml");

	private static final OpenShiftTestAssistant greetingServiceAssistant = new OpenShiftTestAssistant("greeting-service",
			"target/classes/META-INF/fabric8/openshift.yml");

	@BeforeClass
	public static void prepare() throws Exception {
		nameServiceAssistant.deployApplication();
		greetingServiceAssistant.deployApplication();
	}

	@AfterClass
	public static void cleanup() {
		nameServiceAssistant.cleanup();
		greetingServiceAssistant.cleanup();
	}

	@Test
	public void testThatWeAreReady() throws Exception {
		nameServiceAssistant.awaitApplicationReadinessOrFail();
		greetingServiceAssistant.awaitApplicationReadinessOrFail();

		// Check that the routes are served.
		Awaitility.await()
				.atMost(5, TimeUnit.MINUTES)
				.catchUncaughtExceptions()
				.until(() -> RestAssured.get(nameServiceAssistant.getBaseUrl())
						.getStatusCode() < 500);
		Awaitility.await()
				.atMost(5, TimeUnit.MINUTES)
				.catchUncaughtExceptions()
				.until(() -> RestAssured.get(greetingServiceAssistant.getBaseUrl())
						.getStatusCode() < 500);
	}

	@Test
	public void testThatWeServeAsExpected() {
		RestAssured.get(greetingServiceAssistant.getBaseUrl() + "/greeting")
				.then()
				.body(containsString("Hello from"))
				.body(not(containsString("Fallback")));
	}

	@Test
	public void testThatWeFallbackOnDelay() {
		RestAssured.get(greetingServiceAssistant.getBaseUrl() + "/greeting?delay=3000")
				.then()
				.body(equalTo("Hello from Fallback!"));
	}

}
