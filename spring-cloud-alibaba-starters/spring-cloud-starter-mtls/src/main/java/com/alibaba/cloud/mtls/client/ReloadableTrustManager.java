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

package com.alibaba.cloud.mtls.client;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import com.alibaba.cloud.governance.istio.sds.CertPair;
import com.alibaba.cloud.governance.istio.sds.CertUpdateCallback;
import com.alibaba.cloud.mtls.MtlsSslStoreProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReloadableTrustManager implements X509TrustManager, CertUpdateCallback {

	private static final Logger log = LoggerFactory
			.getLogger(ReloadableTrustManager.class);

	private final MtlsSslStoreProvider mtlsSslStoreProvider;

	private X509TrustManager x509TrustManager;

	public ReloadableTrustManager(MtlsSslStoreProvider mtlsSslStoreProvider,
			X509TrustManager x509TrustManager) {
		this.mtlsSslStoreProvider = mtlsSslStoreProvider;
		this.x509TrustManager = x509TrustManager;
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		x509TrustManager.checkClientTrusted(chain, authType);
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		x509TrustManager.checkServerTrusted(chain, authType);
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return x509TrustManager.getAcceptedIssuers();
	}

	@Override
	public synchronized void onUpdateCert(CertPair certPair) {
		try {
			KeyStore trustStore = mtlsSslStoreProvider.getTrustStore();
			TrustManagerFactory trustStoreFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustStoreFactory.init(trustStore);
			TrustManager[] x509TrustManagers = trustStoreFactory.getTrustManagers();
			for (TrustManager tm : x509TrustManagers) {
				if (tm instanceof X509KeyManager) {
					this.x509TrustManager = (X509TrustManager) tm;
				}
			}
		}
		catch (Throwable t) {
			log.error("Failed to refresh x509TrustManager", t);
		}
	}

}
