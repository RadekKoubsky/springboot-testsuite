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

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Server controller.
 *
 * @author Obsidian Quickstarts
 */
@RestController
public class Controller {

	@RequestMapping("/")
	public String health() {
		//For Ribbon pings
		return "";
	}

	@RequestMapping("/test")
	public String test(@RequestParam(value = "indent", defaultValue = "--") String indent) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(indent).append(" backend service called at ")
				.append(LocalTime.now()).append("<br/>");
		delay();
		stringBuilder.append(indent).append(" backend service finished at ")
				.append(LocalTime.now()).append("<br/>");
		return stringBuilder.toString();
	}

	private static synchronized void delay() {
		try {
			Thread.sleep(1000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
