package com.learning.selfLearningPOC.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Customer {

    @Id
    @Column(name = "customer_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String mobileNumber;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String pwd;
    private String role;

    @Column(name="create_dt")
    @JsonIgnore
    private LocalDateTime createdDt;

}
