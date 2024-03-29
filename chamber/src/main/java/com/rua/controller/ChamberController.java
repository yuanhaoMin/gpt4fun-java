package com.rua.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChamberController {

    // This is used for Azure health check when "Always On" is enabled
    @GetMapping(path = "/")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Server is up!");
    }

    @GetMapping("/favicon.ico")
    public void disableFavicon() {
        //Method is void to avoid browser 404 issue by returning nothing in the response.
    }

}