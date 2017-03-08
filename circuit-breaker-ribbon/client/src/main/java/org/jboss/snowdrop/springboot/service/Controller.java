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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import rx.Observable;

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
public class Controller {

	@LoadBalanced
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Autowired
	RestTemplate restTemplate;

	@RequestMapping("/test")
	public String test(@RequestParam(value = "count", defaultValue = "1") int count) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("-- presentation service called at ").append(LocalTime.now())
				.append("<br/>");
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
		stringBuilder.append("-- presentation service finished at ")
				.append(LocalTime.now()).append("<br/>");
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
			return Controller.this.restTemplate
					.getForObject("http://backend/greeting?indent={indent}", String.class,
							"----");
		}

		@Override
		protected String getFallback() {
			return "FAILED <br/>";
		}
	}
}