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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Greeting Service.
 *
 * @author Obsidian Quickstarts
 */
@RestController
public class Greeting {

	/**
	 * String Variable HOSTNAME.
	 */
	public static final String HOSTNAME = "HOSTNAME";

	private static Logger log = LoggerFactory.getLogger(Greeting.class);

	@RequestMapping("/")
	public String health() {
		//For Ribbon pings
		log.info("Access /");
		return "Hi!" + ", from " + this.hostName + ", pod !";
	}

	private final String hostName;

	public Greeting() {
		this.hostName = System.getenv(HOSTNAME);
	}

	@RequestMapping("/greeting")
	public String test(@RequestParam(value = "indent", defaultValue = "--") String indent) {
		StringBuilder sb = new StringBuilder();
		sb.append(indent)
				.append(" backend service called at ")
				.append(LocalTime.now())
				.append("<br/>");
		delay();
		sb.append("Say Hello from " + this.hostName + "!").append("<br/>");
		sb.append(indent)
				.append(" backend service finished at ")
				.append(LocalTime.now())
				.append("<br/>");
		return sb.toString();
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
