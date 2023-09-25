package com.alibaba.cloud.examples;

public class HttpsConstants {
	public static final String PEM_PRIVATE_START = "-----BEGIN PRIVATE KEY-----";
	public static final String PEM_PRIVATE_EC_START = "-----BEGIN EC PRIVATE KEY-----";
	public static final String PEM_PRIVATE_RSA_START = "-----BEGIN RSA PRIVATE KEY-----";
	public static final String PEM_PRIVATE_END = "-----END PRIVATE KEY-----";
	public static final String PEM_PRIVATE_EC_END = "-----END EC PRIVATE KEY-----";
	public static final String PEM_PRIVATE_RSA_END = "-----END RSA PRIVATE KEY-----";
	public static final String PEM_CERTIFICATE_START = "-----BEGIN CERTIFICATE-----";
	public static final String PEM_CERTIFICATE_END = "-----END CERTIFICATE-----";
	public static final String DEFAULT_ALIAS = "default-alias";
	public static final String ALG_RSA = "RSA";
	public static final String ALG_EC = "EC";

	public static final String DEFAULT_PASSWORD = "changeit";
	public static final String UNKNOWN_SNI = "Unknown";
	public static final String SUBJECT_DN_HEADER = "subjectDnHeader";
	public static final String ISSUER_DN_HEADER = "issuerDnHeader";
}
