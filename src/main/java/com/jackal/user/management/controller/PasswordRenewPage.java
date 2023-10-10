package com.jackal.user.management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PasswordRenewPage {

    @RequestMapping("/PasswordRenewPage")
    public String greeting(){
        return "ResetPasswordPage.html";
    }

}
