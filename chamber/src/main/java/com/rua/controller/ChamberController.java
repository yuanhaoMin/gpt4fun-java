package com.rua.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping
@RestController
public class ChamberController {

    @GetMapping(value = "/chamber")
    public ModelAndView home() {
        final var modelAndView = new ModelAndView();
        modelAndView.setViewName("chamber.html");
        return modelAndView;
    }

}