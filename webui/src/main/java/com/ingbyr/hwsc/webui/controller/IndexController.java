package com.ingbyr.hwsc.webui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {
    @GetMapping("/")
    public String hello(Model model,
                        @RequestParam(value = "name", required = false, defaultValue = "world") String name) {
        model.addAttribute("name", name);
        return "index";
    }
}
