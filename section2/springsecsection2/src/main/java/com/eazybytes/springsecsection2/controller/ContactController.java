package com.eazybytes.springsecsection2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactController {

    @GetMapping("/contact")
    public  String saveContactEnquiryDetails () {
        return "Save Contact Enquiry Details.";
    }

}
