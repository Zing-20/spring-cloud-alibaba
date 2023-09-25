package com.alibaba.cloud.examples;

import java.security.cert.X509Certificate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.cloud.examples.feignclient.MvcClient;
import com.alibaba.cloud.examples.feignclient.WebfluxClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MtlsOpenfeignApplication {

	public static void main(String[] args) {
		SpringApplication.run(MtlsOpenfeignApplication.class, args);
	}

}
