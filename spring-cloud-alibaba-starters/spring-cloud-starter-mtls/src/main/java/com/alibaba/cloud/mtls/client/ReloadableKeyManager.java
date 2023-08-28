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

import java.net.Socket;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509KeyManager;

import com.alibaba.cloud.governance.istio.sds.CertPair;
import com.alibaba.cloud.governance.istio.sds.CertUpdateCallback;
import com.alibaba.cloud.mtls.MtlsSslStoreProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReloadableKeyManager implements X509KeyManager, CertUpdateCallback {

	private static final Logger log = LoggerFactory.getLogger(ReloadableKeyManager.class);

	private final MtlsSslStoreProvider mtlsSslStoreProvider;

	private X509KeyManager x509KeyManager;

	public ReloadableKeyManager(MtlsSslStoreProvider mtlsSslStoreProvider,
			X509KeyManager x509KeyManager) {
		this.mtlsSslStoreProvider = mtlsSslStoreProvider;
		this.x509KeyManager = x509KeyManager;
	}

	@Override
	public String[] getClientAliases(String keyType, Principal[] issuers) {
		return x509KeyManager.getClientAliases(keyType, issuers);
	}

	@Override
	public String chooseClientAlias(String[] keyType, Principal[] issuers,
			Socket socket) {
		return x509KeyManager.chooseClientAlias(keyType, issuers, socket);
	}

	@Override
	public String[] getServerAliases(String keyType, Principal[] issuers) {
		return x509KeyManager.getServerAliases(keyType, issuers);
	}

	@Override
	public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
		return x509KeyManager.chooseServerAlias(keyType, issuers, socket);
	}

	@Override
	public X509Certificate[] getCertificateChain(String alias) {
		return x509KeyManager.getCertificateChain(alias);
	}

	@Override
	public PrivateKey getPrivateKey(String alias) {
		return x509KeyManager.getPrivateKey(alias);
	}

	@Override
	public synchronized void onUpdateCert(CertPair certPair) {
		try {
			KeyStore keyStore = mtlsSslStoreProvider.getKeyStore();
			KeyManagerFactory keyManagerFactory = KeyManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, "".toCharArray());
			KeyManager[] x509KeyManagers = keyManagerFactory.getKeyManagers();
			for (KeyManager km : x509KeyManagers) {
				if (km instanceof X509KeyManager) {
					this.x509KeyManager = (X509KeyManager) km;
				}
			}
		}
		catch (Throwable t) {
			log.error("Failed to refresh x509KeyManager", t);
		}
	}

}
