package com.turkcell.blogging_platform.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    //test etmek için
    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "Bu endpoint'e sadece ADMIN rolü erişebilir. Hoşgeldin Admin!";
    }

}
