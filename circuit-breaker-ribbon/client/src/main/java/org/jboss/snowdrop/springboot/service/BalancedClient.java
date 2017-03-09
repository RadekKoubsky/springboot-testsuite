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

package org.jboss.snowdrop.springboot.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Client controller.
 *
 * @author Obsidian Quickstarts
 */
@RestController
@RibbonClient(name = "backend", configuration = ClientHelloConfiguration.class)
public class BalancedClient {

	private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm:ss");

	@LoadBalanced
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Autowired
	RestTemplate restTemplate;

	@RequestMapping("/hi")
	public String test(@RequestParam(value = "count", defaultValue = "1") int count) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder
				.append("-- client called at ")
				.append(TIME_FORMATTER.format(new Date()))
				.append("<br/>");

		/*
		List<Observable<String>> observables = new ArrayList<>();
		for (int index = 0; index < count; index++) {
			observables.add(new BackendCall().toObservable());
		}
		Observable<String> zipped = Observable.zip(observables, objects -> {
			StringBuilder responses = new StringBuilder();
			for (Object object : objects) {
				responses.append(object);
			}
			return responses.toString();
		});
		stringBuilder.append(zipped.toBlocking().first());
		*/

		String response = BalancedClient.this.restTemplate.getForObject("http://backend/greeting?indent={indent}", String.class,
						"----");
		stringBuilder.append(response);

		stringBuilder
				.append("-- client finished at ")
				.append(TIME_FORMATTER.format(new Date()))
				.append("<br/>");
		return stringBuilder.toString();
	}

	/**
	 * Hystrix Command.
	 */
	public class BackendCall extends HystrixCommand<String> {
		BackendCall() {
			super(HystrixCommandGroupKey.Factory.asKey("BackendCall"),
					HystrixThreadPoolKey.Factory.asKey("BackendCallThread"));
		}

		@Override
		protected String run() throws Exception {
			return BalancedClient.this.restTemplate
					.getForObject("http://backend/greeting?indent={indent}", String.class,
							"----");
		}

		@Override
		protected String getFallback() {
			return "FAILED <br/>";
		}
	}
}
