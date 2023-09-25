package com.alibaba.cloud.examples.feignclient;

import com.alibaba.cloud.examples.config.FeignClientConfiguration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

//@FeignClient(value = "mtls-mvc-example")
@FeignClient(value = "https://mtls-mvc-example", configuration = FeignClientConfiguration.class)
public interface MvcClient {
	@GetMapping("/b/get")
	String getB();
}
