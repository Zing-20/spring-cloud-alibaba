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

import io.grpc.ChannelCredentials;

final class ServerInfoImpl extends Bootstrapper.ServerInfo {

	private final String target;

	private final ChannelCredentials channelCredentials;

	private final boolean useProtocolV3;

	private final boolean ignoreResourceDeletion;

	ServerInfoImpl(String target, ChannelCredentials channelCredentials,
			boolean useProtocolV3, boolean ignoreResourceDeletion) {
		this.target = target;
		this.channelCredentials = channelCredentials;
		this.useProtocolV3 = useProtocolV3;
		this.ignoreResourceDeletion = ignoreResourceDeletion;
	}

	@Override
	public String target() {
		return target;
	}

	@Override
	ChannelCredentials channelCredentials() {
		return channelCredentials;
	}

	@Override
	boolean useProtocolV3() {
		return useProtocolV3;
	}

	@Override
	boolean ignoreResourceDeletion() {
		return ignoreResourceDeletion;
	}

	@Override
	public String toString() {
		return "ServerInfo{" + "target=" + target + ", " + "channelCredentials="
				+ channelCredentials + ", " + "useProtocolV3=" + useProtocolV3 + ", "
				+ "ignoreResourceDeletion=" + ignoreResourceDeletion + "}";
	}

}
