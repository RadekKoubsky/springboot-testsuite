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

package org.jboss.snowdrop.springboot.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.jayway.awaitility.Awaitility;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.dsl.NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.client.OpenShiftClient;
import io.restassured.RestAssured;

/**
 * Class to help deploy and undeploy applications to OpenShift.
 *
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
public class OpenShiftTestAssistant {

	private final OpenShiftClient client;

	private final String applicationName;

	private final String configurationPath;

	private final Map<String, NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata, Boolean>> created
			= new LinkedHashMap<>();

	private String baseUrl;

	public OpenShiftTestAssistant(String applicationName) {
		this(applicationName, "target/classes/META-INF/fabric8/openshift.yml");
	}

	public OpenShiftTestAssistant(String applicationName, String configurationPath) {
		this.applicationName = applicationName;
		this.configurationPath = configurationPath;
		this.client = new DefaultKubernetesClient().adapt(OpenShiftClient.class);
	}

	public String deployApplication() throws IOException {
		deploy(this.applicationName, new File(this.configurationPath));
		Route route = this.client.adapt(OpenShiftClient.class)
				.routes()
				.inNamespace(this.client.getNamespace())
				.withName(this.applicationName)
				.get();

		this.baseUrl = "http://" + Objects.requireNonNull(route)
				.getSpec()
				.getHost();

		System.out.println("Route url: " + this.baseUrl);

		return this.baseUrl;
	}

	public void cleanup() {
		List<String> keys = new ArrayList<>(this.created.keySet());
		Collections.reverse(keys);
		for (String key : keys) {
			System.out.println("Deleting " + key);
			this.created.remove(key)
					.delete();
		}
	}

	public void awaitApplicationReadinessOrFail() {
		waitUntilPodIsReady();
		waitUntilRouteIsServed();
	}

	public String getBaseUrl() {
		return this.baseUrl;
	}

	private List<? extends HasMetadata> deploy(String name, File template) throws IOException {
		try (FileInputStream fis = new FileInputStream(template)) {
			NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata, Boolean> declarations
					= this.client.load(fis);
			List<HasMetadata> entities = declarations.createOrReplace();
			this.created.put(name, declarations);

			System.out.println(name + " deployed, " + entities.size() + " object(s) created.");

			return entities;
		}
	}

	private void waitUntilPodIsReady() {
		Awaitility.await()
				.atMost(5, TimeUnit.MINUTES)
				.until(() -> !this.client.pods()
						.inNamespace(this.client.getNamespace())
						.list()
						.getItems()
						.stream()
						.filter(this::isThisApplicationPod)
						.filter(this::isRunning)
						.collect(Collectors.toList())
						.isEmpty());
		System.out.println("Pod is running");
	}

	private void waitUntilRouteIsServed() {
		Awaitility.await()
				.atMost(5, TimeUnit.MINUTES)
				.until(() -> isUrlServed(this.baseUrl));
		System.out.println("Route is served");
	}

	private boolean isRunning(Pod pod) {
		return "running".equalsIgnoreCase(pod.getStatus()
				.getPhase());
	}

	private boolean isThisApplicationPod(Pod pod) {
		return pod.getMetadata()
				.getName()
				.startsWith(this.applicationName);
	}

	private boolean isUrlServed(String url) {
		try {
			return RestAssured.get(url)
					.getStatusCode() < 500;
		}
		catch (Exception e) {
			return false;
		}
	}

}
