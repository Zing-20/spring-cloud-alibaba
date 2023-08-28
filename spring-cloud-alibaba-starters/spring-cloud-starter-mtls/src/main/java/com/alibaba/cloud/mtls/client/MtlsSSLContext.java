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

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import com.alibaba.cloud.mtls.MtlsSslStoreProvider;

public class MtlsSSLContext {

	private MtlsSslStoreProvider mtlsSslStoreProvider;

	private SSLContext sslContext;

	public MtlsSSLContext(MtlsSslStoreProvider mtlsSslStoreProvider) {
		if (sslContext == null) {
			synchronized (MtlsSSLContext.class) {
				if (sslContext == null) {
					this.mtlsSslStoreProvider = mtlsSslStoreProvider;
					initContext();
				}
			}
		}
	}

	private void initContext() {
		try {
			this.sslContext = SSLContext.getInstance("TLS");
			// init key store
			KeyStore keyStore = mtlsSslStoreProvider.getKeyStore();
			KeyManagerFactory keyManagerFactory = KeyManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, "".toCharArray());
			KeyManager[] x509KeyManagers = keyManagerFactory.getKeyManagers();
			initKeyManagers(x509KeyManagers);
			// init trust store
			KeyStore trustStore = mtlsSslStoreProvider.getTrustStore();
			TrustManagerFactory trustStoreFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustStoreFactory.init(trustStore);
			TrustManager[] x509TrustManagers = trustStoreFactory.getTrustManagers();
			initTrustManagers(x509TrustManagers);
		}
		catch (Throwable t) {
			throw new RuntimeException("Failed to create the initial context", t);
		}
	}

	private void initTrustManagers(TrustManager[] x509TrustManagers) {
		final int x509Len = x509TrustManagers.length;
		for (int i = 0; i < x509Len; ++i) {
			if (x509TrustManagers[i] instanceof X509TrustManager) {
				X509TrustManager x509TrustManager = (X509TrustManager) x509TrustManagers[i];
				x509TrustManagers[i] = new ReloadableTrustManager(mtlsSslStoreProvider,
						x509TrustManager);
			}
		}
	}

	private void initKeyManagers(KeyManager[] x509KeyManagers) {
		final int x509Len = x509KeyManagers.length;
		for (int i = 0; i < x509Len; ++i) {
			if (x509KeyManagers[i] instanceof X509KeyManager) {
				X509KeyManager x509KeyManager = (X509KeyManager) x509KeyManagers[i];
				x509KeyManagers[i] = new ReloadableKeyManager(mtlsSslStoreProvider,
						x509KeyManager);
			}
		}
	}

}
