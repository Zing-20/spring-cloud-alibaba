/*
 * Copyright 2022-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.governance.istio.bootstrap;

import java.util.Map;

final class CertificateProviderInfoImpl extends Bootstrapper.CertificateProviderInfo {

	private final String pluginName;

	private final Map<String, ?> config;

	CertificateProviderInfoImpl(String pluginName, Map<String, ?> config) {
		this.pluginName = pluginName;
		this.config = config;
	}

	@Override
	public String pluginName() {
		return pluginName;
	}

	@Override
	public Map<String, ?> config() {
		return config;
	}

	@Override
	public String toString() {
		return "CertificateProviderInfo{" + "pluginName=" + pluginName + ", " + "config="
				+ config + "}";
	}

}
