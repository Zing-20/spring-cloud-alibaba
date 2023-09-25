package com.alibaba.cloud.examples;

import java.security.cert.X509Certificate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.cloud.examples.feignclient.MvcClient;
import com.alibaba.cloud.examples.feignclient.WebfluxClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

	@Resource
	MvcClient mvcClient;

	@Autowired
	WebfluxClient webfluxClient;

	@GetMapping("/d/getB")
	public String getB(HttpServletRequest httpServletRequest) {
		X509Certificate[] certs = (X509Certificate[]) httpServletRequest.getAttribute("javax.servlet.request.X509Certificate");
		return mvcClient.getB();
	}

	@GetMapping("/d/getC")
	public String getC(HttpServletRequest httpServletRequest) {
		X509Certificate[] certs = (X509Certificate[]) httpServletRequest.getAttribute("javax.servlet.request.X509Certificate");
		return webfluxClient.getC();
	}

}
