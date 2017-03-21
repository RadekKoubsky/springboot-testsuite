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

package org.jboss.snowdrop.springboot.rest.cxf.service;

import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Greeting controller.
 *
 * @author <a href="mailto:cmoulliard@redhat.com">Charles Moulliard</a>
 */
@Component
@Path("/")
public class GreetingEndpoint {

	@Autowired
	private GreetingProperties properties;

	private final AtomicLong counter = new AtomicLong();

	@GET
	@Path("/greeting")
	@Produces("application/json")
	public Greeting greeting(@DefaultValue("World") @QueryParam("name") String name) {
		return new Greeting(this.counter.incrementAndGet(), String.format(this.properties.getMessage(), name));
	}
}
