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

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Greeting controller.
 *
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
@RestController
@RibbonClient(name = "name-service", configuration = RibbonConfiguration.class)// TODO move to the application class
public class GreetingController {

	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping("/greeting")
	public String getGreeting(@RequestParam(value = "delay", defaultValue = "0") int delay) {
		String name = new NameCall(this.restTemplate, delay).execute();
		return String.format("Hello from %s!", name);
	}

	/**
	 * Operation to get name or fallback.
	 */
	static class NameCall extends HystrixCommand<String> {

		private final RestTemplate restTemplate;

		private final int delay;

		NameCall(RestTemplate restTemplate, int delay) {
			super(HystrixCommandGroupKey.Factory.asKey("NameCall"),
					HystrixThreadPoolKey.Factory.asKey("NameCallTreadPool"),
					2000);
			this.restTemplate = restTemplate;
			this.delay = delay;
		}

		@Override
		protected String run() throws Exception {
			return this.restTemplate.getForObject(String.format("http://name-service/name?delay=%d", this.delay), String.class);
		}

		@Override
		protected String getFallback() {
			return "Fallback";
		}
	}

}
