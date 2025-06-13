package com.fmd.order_service.controller;

import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shailesh Halor
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @GetMapping
    public String getOrder() {
        log.info("getOrder method called");
        return "Order details";
    }

}