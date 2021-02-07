package com.example.resilience.springresilience4jtimelimit.controller;


import com.example.resilience.springresilience4jtimelimit.service.SlowService;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@RestController
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private static final String ORDER_SERVICE ="orderService" ;

    public OrderController(SlowService slowService) {
        this.slowService = slowService;
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private final SlowService slowService;

    @GetMapping("/order")
    @TimeLimiter(name=ORDER_SERVICE)
    public CompletableFuture<String> createOrder()
    {
        return CompletableFuture.supplyAsync(slowService::slowMethod);
    }
    public ResponseEntity<String> timeLimitFallback(RequestNotPermitted t) throws InterruptedException {
        return new ResponseEntity<String>(" orderService is full and does not permit further calls", HttpStatus.TOO_MANY_REQUESTS);
    }



}
