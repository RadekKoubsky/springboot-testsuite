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

package org.jboss.snowdrop.springboot.rest.cxf;

import org.jboss.snowdrop.springboot.rest.cxf.service.Greeting;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

/**
 * Simple REST test.
 *
 * @author Obsidian Quickstarts
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)  // Use a random port
public class RestApplicationTest {

	// This will hold the port number the server was started on
	@Value("${local.server.port}")
	int port;

	final RestTemplate template = new RestTemplate();

	@Test
	public void callServiceTest() {
		Greeting message = this.template.getForObject("http://localhost:" + this.port + "/rest/api/greeting", Greeting.class);
		Assert.assertEquals("Hello, World!", message.getContent());
		Assert.assertEquals(1, message.getId());
	}

}
