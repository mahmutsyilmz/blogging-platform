package com.turkcell.blogging_platform.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    //test etmek için
    @GetMapping("/profile")
    public String userProfile() {
        return "Bu endpoint'e sadece USER rolü erişebilir. Kullanıcı profil sayfası!";
    }
}
