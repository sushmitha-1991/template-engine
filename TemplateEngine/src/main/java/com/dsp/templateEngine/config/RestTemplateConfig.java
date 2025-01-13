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

		log.debug("Creating RestTemplate with custom clientHttpRequestFactory");

		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());

		// Add interceptor to attach JWT token
		ClientHttpRequestInterceptor interceptor = (req, body, execution) -> {
			// Check and print the Authorization header directly from the request object
			String jwtToken = request.getHeader("Authorization");

			// Now let's check the header in the request object passed to the interceptor
			String headerInRequest = req.getHeaders().getFirst("Authorization");

			if (jwtToken != null && !jwtToken.isEmpty()) {
				req.getHeaders().add("Authorization", jwtToken);
			} else {
			}

			return execution.execute(req, body);
		};

		// Set the interceptor to add the Authorization header with JWT token
		restTemplate.setInterceptors(Collections.singletonList(interceptor));

		return restTemplate;
	}

	// Factory to configure timeouts for RestTemplate requests
	private ClientHttpRequestFactory clientHttpRequestFactory() {
		log.debug("Configuring timeouts for RestTemplate");

		// Configure timeout settings
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3, TimeUnit.SECONDS) // Connection
																									// timeout (3
																									// seconds)
				.setResponseTimeout(3, TimeUnit.SECONDS) // Response timeout (3 seconds)
				.build();

		// Create the HttpClient with the request configuration
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
		log.debug("HttpClient created with connection and response timeout of 3 seconds");

		// Create and return the request factory with the custom HttpClient
		return new HttpComponentsClientHttpRequestFactory(httpClient);
	}
}
