package com.eazybytes.springsecsection4.controller;

import com.eazybytes.springsecsection4.model.Customer;
import com.eazybytes.springsecsection4.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody Customer customer) {
        try {
            String hashedPwd = passwordEncoder.encode(customer.getPwd());
            customer.setPwd(hashedPwd);
            Customer savedCustomer = customerRepository.save(customer);
            if(savedCustomer.getId()>0)
                return ResponseEntity.status(HttpStatus.CREATED).body("Created");
            else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User registration failed");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occored while creating user");
        }
    }

}
