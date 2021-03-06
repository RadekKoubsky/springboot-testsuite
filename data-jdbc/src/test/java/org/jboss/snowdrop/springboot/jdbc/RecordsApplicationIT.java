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

import io.obsidian.testsuite.common.OpenShiftTestAssistant;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * OpenShift integration test for records application.
 *
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
public class RecordsApplicationIT extends RecordsApplicationTestBase {

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

	@Override
	protected String getBaseUrl() {
		return ASSISTANT.getBaseUrl();
	}

}
