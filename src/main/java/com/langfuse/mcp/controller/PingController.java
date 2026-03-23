package com.langfuse.mcp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
class PingController {
    @GetMapping("/ping")
    Map<String, String> ping() {
        return Map.of("status", "ok");
    }
}