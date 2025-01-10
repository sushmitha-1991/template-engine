package com.dsp.templateEngine.config;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class RestTemplateConfig {

	private static final Logger log = LoggerFactory.getLogger(RestTemplateConfig.class);

	@Bean
	public RestTemplate restTemplate(HttpServletRequest request) {

		System.out.println("##### Inside restTemplate Bean Initialization #####");
		log.debug("Creating RestTemplate with custom clientHttpRequestFactory");

		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());

		// Add interceptor to attach JWT token
		System.out.println("##### Adding Interceptor to RestTemplate #####");
		ClientHttpRequestInterceptor interceptor = (req, body, execution) -> {
			System.out.println("##### Inside Interceptor #####");
			System.out.println("Request URI: " + req.getURI());
			System.out.println("Request Headers: " + req.getHeaders()); // Log the request headers

			// Check and print the Authorization header directly from the request object
			String jwtToken = request.getHeader("Authorization");
			System.out.println("Extracted JWT Token from HttpServletRequest: " + jwtToken);

			// Now let's check the header in the request object passed to the interceptor
			String headerInRequest = req.getHeaders().getFirst("Authorization");
			System.out.println("Authorization Header in the Request Object: " + headerInRequest);

			if (jwtToken != null && !jwtToken.isEmpty()) {
				System.out.println("JWT Token is present. Adding Authorization header to the request.");
				req.getHeaders().add("Authorization", jwtToken);
			} else {
				System.out.println("No JWT Token found in the request headers.");
			}

			System.out.println("##### Proceeding with Request Execution #####");
			return execution.execute(req, body);
		};

		// Set the interceptor to add the Authorization header with JWT token
		restTemplate.setInterceptors(Collections.singletonList(interceptor));
		System.out.println("##### Interceptor Added Successfully #####");

		return restTemplate;
	}

	// Factory to configure timeouts for RestTemplate requests
	private ClientHttpRequestFactory clientHttpRequestFactory() {
		System.out.println("##### Configuring Timeouts for RestTemplate #####");
		log.debug("Configuring timeouts for RestTemplate");

		// Configure timeout settings
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3, TimeUnit.SECONDS) // Connection
																									// timeout (3
																									// seconds)
				.setResponseTimeout(3, TimeUnit.SECONDS) // Response timeout (3 seconds)
				.build();

		// Create the HttpClient with the request configuration
		System.out.println("##### Creating HttpClient with Timeout Settings #####");
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
		log.debug("HttpClient created with connection and response timeout of 3 seconds");
		System.out.println("##### HttpClient Created Successfully #####");

		// Create and return the request factory with the custom HttpClient
		System.out.println("##### Returning Custom ClientHttpRequestFactory #####");
		return new HttpComponentsClientHttpRequestFactory(httpClient);
	}
}
