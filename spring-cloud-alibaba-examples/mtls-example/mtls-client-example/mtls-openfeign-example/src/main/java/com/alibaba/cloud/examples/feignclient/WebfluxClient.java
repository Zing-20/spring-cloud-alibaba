package com.alibaba.cloud.examples.feignclient;

import com.alibaba.cloud.examples.config.FeignClientConfiguration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "mtls-webflux-example", configuration = FeignClientConfiguration.class)
public interface WebfluxClient {
	@GetMapping("/c/get")
	String getC();
}
