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

package org.jboss.snowdrop.springboot.jdbc;

import java.util.HashMap;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

/**
 * Class containing common test cases for unit and integration tests.
 *
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
public abstract class RecordsApplicationTestBase {

	/**
	 * Test executes GET request on /bands/1 and expects a response as presented below.
	 * <pre>
	 *     {
	 *         "name": "AC/DC",
	 *         "records": [
	 *             {
	 *                 "name": "High Voltage",
	 *                 "bandId": 1
	 *             },
	 *             {
	 *                 "name": "T.N.T.",
	 *                 "bandId": 1
	 *             }
	 *         ]
	 *     }
	 * </pre>
	 */
	@Test
	public void shouldGetBand() {
		RestAssured.when()
				.get(getBaseUrl() + "/bands/1")
				.then()
				.body("name", is("AC/DC"))
				.body("records.name", hasItems("High Voltage", "T.N.T."));
	}

	/**
	 * Test executes POST request on /bands with a request body presented below and expects status code 201.
	 * <pre>
	 *     {
	 *         "name": "Iron Maiden"
	 *     }
	 * </pre>
	 */
	@Test
	public void shouldCreateBand() {
		Map<String, String> jsonAsMap = new HashMap<>();
		jsonAsMap.put("name", "Iron Maiden");

		RestAssured.given()
				.contentType(ContentType.JSON)
				.body(jsonAsMap)
				.when()
				.post(getBaseUrl() + "/bands")
				.then()
				.statusCode(201);

		RestAssured.when()
				.get(getBaseUrl() + "/bands/2")
				.then()
				.body("name", is("Iron Maiden"));
	}

	protected abstract String getBaseUrl();

}
