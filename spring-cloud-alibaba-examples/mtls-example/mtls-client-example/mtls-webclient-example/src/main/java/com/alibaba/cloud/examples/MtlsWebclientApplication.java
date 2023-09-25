package com.alibaba.cloud.examples;

import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

import com.alibaba.cloud.mtls.client.MtlsClientSSLContext;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

@SpringBootApplication
public class MtlsWebclientApplication {

	public static void main(String[] args) {
		SpringApplication.run(MtlsWebclientApplication.class, args);
	}

	@RestController
	public class Controller {

		@Autowired
		MtlsClientSSLContext mtlsClientSSLContext;

		@Autowired
		private WebClient.Builder builder;

		@GetMapping("/e/getB")
		public Mono<String> getB(ServerWebExchange serverWebExchange) {
			SslContext nettySslContext = null;
			try {
				SslContextBuilder builder1;

				nettySslContext = SslContextBuilder.forClient()
						.trustManager(InsecureTrustManagerFactory.INSTANCE)
						.keyManager(mtlsClientSSLContext.getKeyManagerFactory().orElse(null))
						.build();
			}
			catch (SSLException e) {
				throw new RuntimeException("Error setting SSL context for WebClient", e);
			}

			SslContext finalNettySslContext = nettySslContext;

			HttpClient httpClient = HttpClient.create()
					.secure(spec -> spec.sslContext(finalNettySslContext));

			ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
			return builder.clientConnector(connector).build().get()
					.uri("https://mtls-mvc-example/b/get")
					.retrieve()
					.bodyToMono(String.class);
		}

//		@GetMapping("/e/getC")
//		public Mono<String> getC(ServerWebExchange serverWebExchange) {
//			return webClient.get()
//					.uri("https://mtls-webflux-example/c/get")
//					.retrieve()
//					.bodyToMono(String.class);
//		}

	}
}
