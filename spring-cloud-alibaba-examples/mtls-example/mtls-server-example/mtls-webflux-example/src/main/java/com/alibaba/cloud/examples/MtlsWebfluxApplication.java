package com.alibaba.cloud.examples;

import java.security.cert.X509Certificate;

import reactor.core.publisher.Mono;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

@SpringBootApplication
public class MtlsWebfluxApplication {

	public static void main(String[] args) {
		SpringApplication.run(MtlsWebfluxApplication.class, args);
	}

	@RestController
	public class WebFluxController {

		@GetMapping("/c/get")
		public Mono<String> get(ServerWebExchange exchange) {
			// 获取X509证书
			X509Certificate[] certs = exchange.getRequest().getSslInfo().getPeerCertificates();
			System.out.println(certs[0].toString());
			// 构建响应消息并返回
			return Mono.just("C received request from A");
		}
	}
}
