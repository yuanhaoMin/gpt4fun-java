package com.rua.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChamberController {

    // This is used for Azure health check when "Always On" is enabled
    // Unfortunately we can not customize the endpoint in azure
    @GetMapping(path = "/")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Server is up!");
    }

}