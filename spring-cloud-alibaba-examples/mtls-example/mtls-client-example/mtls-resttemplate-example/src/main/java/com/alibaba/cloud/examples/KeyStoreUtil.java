package com.alibaba.cloud.examples;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509Key;

import org.springframework.util.StringUtils;

public class KeyStoreUtil {
	private static final Logger log = LoggerFactory.getLogger(KeyStoreUtil.class);
	public static Certificate[] loadCertificates(List<String> certificateBytes) {
		List<Certificate> certificates = new ArrayList<>();
		final int n = certificateBytes.size();
		for (int i = 0; i < n; ++i) {
			try {
				certificates.add(loadCertificate(certificateBytes.get(i)));
			}
			catch (Exception e) {
				log.error("Unable to load certificate", e);
			}
		}
		return certificates.toArray(new Certificate[0]);
	}

	private static Certificate loadCertificate(String certificatePem) throws GeneralSecurityException {
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
		certificatePem = certificatePem.replaceAll(HttpsConstants.PEM_CERTIFICATE_START, "");
		certificatePem = certificatePem.replaceAll(HttpsConstants.PEM_CERTIFICATE_END, "");
		certificatePem = certificatePem.replaceAll("\\s*", "");
		return certificateFactory.generateCertificate(new ByteArrayInputStream(Base64.getDecoder()
				.decode(certificatePem)));
	}

	public static PrivateKey loadPrivateKey(String privateKeyPem, String algType) {
		if (StringUtils.isEmpty(algType)) {
			algType = HttpsConstants.ALG_RSA;
		}
		try {
			KeyFactory factory = KeyFactory.getInstance(algType);
			// 换行和空字符去掉
			if (privateKeyPem.startsWith(HttpsConstants.PEM_PRIVATE_START)) {
				privateKeyPem = privateKeyPem.replace(HttpsConstants.PEM_PRIVATE_START, "")
						.replace(HttpsConstants.PEM_PRIVATE_END, "");
				privateKeyPem = privateKeyPem.replaceAll("\n", "");
				privateKeyPem = privateKeyPem.replaceAll("\\s*", "");
				return factory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyPem)));
			}
			else {
				String pkcs1Prefix = "-----BEGIN " + algType + " PRIVATE KEY-----";
				String pkcs1Suffix = "-----END " + algType + " PRIVATE KEY-----";
				privateKeyPem = privateKeyPem.replace(pkcs1Prefix, "")
						.replace(pkcs1Suffix, "");
				privateKeyPem = privateKeyPem.replaceAll("\n", "");
				privateKeyPem = privateKeyPem.replaceAll("\\s*", "");
				DerInputStream derReader = new DerInputStream(Base64.getDecoder().decode(privateKeyPem));
				DerValue[] seq = derReader.getSequence(0);
				if (seq.length < 9) {
					throw new GeneralSecurityException("Could not parse a PKCS1 private key.");
				}
				BigInteger modulus = seq[1].getBigInteger();
				BigInteger publicExp = seq[2].getBigInteger();
				BigInteger privateExp = seq[3].getBigInteger();
				BigInteger prime1 = seq[4].getBigInteger();
				BigInteger prime2 = seq[5].getBigInteger();
				BigInteger exp1 = seq[6].getBigInteger();
				BigInteger exp2 = seq[7].getBigInteger();
				BigInteger crtCoef = seq[8].getBigInteger();
				RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(modulus, publicExp, privateExp, prime1, prime2, exp1, exp2, crtCoef);
				return factory.generatePrivate(keySpec);
			}
		} catch (Exception e) {
			throw new RuntimeException("加载证书私钥失败");
		}
	}

	public static LocalDateTime getSignTimeFromCertificate(Certificate certificate) {
		try {
			X509CertImpl x509Cert = (X509CertImpl) certificate;
			return x509Cert.getNotBefore().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		} catch (Exception e) {
			log.error("Unable to get sign time from certificate");
		}
		return LocalDateTime.now();
	}

	public static LocalDateTime getExpireTimeFromCertificate(Certificate certificate) {
		try {
			X509CertImpl x509Cert = (X509CertImpl) certificate;
			return x509Cert.getNotAfter().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		} catch (Exception e) {
			log.error("Unable to get expire time from certificate");
		}
		return LocalDateTime.now();
	}


	public static String getSignerCNFromCertificate(Certificate certificate) {
		try {
			X509CertImpl x509Cert = (X509CertImpl) certificate;
			return ((X500Name)x509Cert.getIssuerDN()).findMostSpecificAttribute(X500Name.commonName_oid).getAsString();
		} catch (Exception e) {
			log.error("Unable to get signer cn from certificate");
		}
		return "";
	}

	public static String getSignerOUFromCertificate(Certificate certificate) {
		try {
			X509CertImpl x509Cert = (X509CertImpl) certificate;
			return ((X500Name)x509Cert.getIssuerDN()).findMostSpecificAttribute(X500Name.orgName_oid).getAsString();
		} catch (Exception e) {
			log.error("Unable to get signer dn from certificate");
		}
		return "";
	}

	public static String getSNFromCertificate(Certificate certificate) {
		try {
			X509CertImpl x509Cert = (X509CertImpl) certificate;
			return x509Cert.getSerialNumber().toString(16);
		} catch (Exception e) {
			log.error("Unable to get sn from certificate");
		}
		return "";
	}

	public static int getKeyLenFromCertificate(Certificate certificate) {
		try {
			X509CertImpl x509Cert = (X509CertImpl) certificate;
			PublicKey publicKey = x509Cert.getPublicKey();
			if (publicKey instanceof RSAKey) {
				return ((RSAKey) publicKey).getModulus().toString(2).length();
			}
		} catch (Exception e) {
			log.error("Unable to get ");
		}
		return 0;
	}



	public static Set<String> getHostsFromCertificate(Certificate certificate) {
		Set<String> hosts = new HashSet<>();
		try {
			hosts.add(((X500Name) ((X509CertImpl) certificate).getSubjectDN()).findMostSpecificAttribute(X500Name.commonName_oid)
					.getAsString());
		}
		catch (Exception e) {
			log.error("Unable to extract CN from certificate");
		}
		try {
			Collection<List<?>> subjectAlternativeNames = ((X509Certificate) certificate).getSubjectAlternativeNames();
			if (subjectAlternativeNames == null) {
				return hosts;
			}
			for (List<?> next : subjectAlternativeNames) {
				Object host = next.get(1);
				if (host instanceof String) {
					hosts.add((String) host);
				}
			}
		}
		catch (Exception e) {
			log.error("Unable to extract alternative names from certificate", e);
		}

		return hosts;
	}

	public static String getAlgFromCertificate(Certificate certificate) {
		try {
			return ((X509Key) certificate.getPublicKey()).getAlgorithmId().getName();
		}
		catch (Exception e) {
			log.error("Unable to get alg from certificate", e);
		}
		return HttpsConstants.ALG_RSA;
	}

	public static Key getKey(String gwInstanceId, String certificateId) {
		byte[] bytes =  Base64.getEncoder().encode((gwInstanceId + certificateId).getBytes());
		byte[] keyBytes = new byte[8];
		final int n = bytes.length;
		for (int i = 0; i < 4; ++i) {
			int index = Math.min(i, n - 1);
			keyBytes[i] = bytes[index];
		}
		for (int i = 7; i >= 4; --i) {
			int index = Math.max(0, n - i);
			keyBytes[i] = bytes[index];
		}
		return new SecretKeySpec(keyBytes, "DES");
	}

	public static byte[] hexStringToBytes(String hexString) {
		if (hexString.length() % 2 != 0) {
			throw new IllegalArgumentException("hexString length not valid");
		}
		int length = hexString.length() / 2;
		byte[] resultBytes = new byte[length];
		for (int index = 0; index < length; index++) {
			String result = hexString.substring(index * 2, index * 2 + 2);
			resultBytes[index] = Integer.valueOf(Integer.parseInt(result, 16)).byteValue();
		}
		return resultBytes;
	}

	public static String bytesToHexString(byte[] sources) {
		if (sources == null) return null;
		StringBuilder stringBuffer = new StringBuilder();
		for (byte source : sources) {
			String result = Integer.toHexString(source& 0xff);
			if (result.length() < 2) {
				result = "0" + result;
			}
			stringBuffer.append(result);
		}
		return stringBuffer.toString();
	}

	public static List<String> splitCertificates(String certificate) {
		int index = 0;
		List<String> certificates = new ArrayList<>();
		while (index < certificate.length()) {
			int nextIndex = certificate.indexOf(HttpsConstants.PEM_CERTIFICATE_START, index + 1);
			if (nextIndex == -1) {
				certificates.add(certificate.substring(index));
				break;
			} else {
				certificates.add(certificate.substring(index, nextIndex));
				index = nextIndex;
			}
		}
		return certificates;
	}

}
