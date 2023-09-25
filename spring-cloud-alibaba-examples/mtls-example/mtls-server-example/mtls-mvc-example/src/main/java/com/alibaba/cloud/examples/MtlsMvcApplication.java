package com.alibaba.cloud.examples;

import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class MtlsMvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(MtlsMvcApplication.class, args);
	}

	@RestController
	public class Controller {
		@GetMapping("/b/get")
		public String get(HttpServletRequest httpServletRequest) {
			X509Certificate[] certs = (X509Certificate[]) httpServletRequest.getAttribute("javax.servlet.request.X509Certificate");
			System.out.println(certs[0].toString());
			return "B received request from A";
		}
	}
}
