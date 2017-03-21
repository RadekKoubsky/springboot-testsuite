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

package org.jboss.snowdrop.springboot.rest;

import io.obsidian.testsuite.common.OpenShiftTestAssistant;
import io.restassured.RestAssured;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Simple Boot REST Test case.
 *
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
public class OpenShiftIT {

	private static final String APPLICATION_NAME = System.getProperty("app.name");

	private static final OpenShiftTestAssistant ASSISTANT = new OpenShiftTestAssistant(APPLICATION_NAME);

	@BeforeClass
	public static void prepare() throws Exception {
		ASSISTANT.deployApplication();
		ASSISTANT.awaitApplicationReadinessOrFail();
	}

	@AfterClass
	public static void cleanup() {
		ASSISTANT.cleanup();
	}

	@Test
	public void shouldGetHelloWorld() throws Exception {
		RestAssured.given()
				.baseUri(ASSISTANT.getBaseUrl())
				.when()
				.get("/greeting")
				.then()
				.assertThat()
				.body(Is.is(IsEqual.equalTo("{\"id\":1,\"content\":\"Hello, World!\"}")));

	}

}
