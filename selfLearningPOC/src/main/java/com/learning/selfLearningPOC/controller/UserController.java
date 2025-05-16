package com.learning.selfLearningPOC.controller;

import com.learning.selfLearningPOC.dto.LoginRequestDTO;
import com.learning.selfLearningPOC.dto.LoginResponseDTO;
import com.learning.selfLearningPOC.model.Customer;
import com.learning.selfLearningPOC.repository.CustomerRepository;
import com.learning.selfLearningPOC.util.ProjectConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.stream.Collectors;

import static org.yaml.snakeyaml.tokens.Token.ID.Key;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @PutMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Customer customer){
        try{
            String hashedPassword = passwordEncoder.encode(customer.getPwd());
            customer.setPwd(hashedPassword);
            customer.setCreatedDt(LocalDateTime.now());
            Customer savedUser = customerRepository.save(customer);
            if(savedUser.getId()>0) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("User Created");
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Unable to create User");
            }
        } catch(Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occored while creating user: " + ex.getMessage());
        }
    }

    @PostMapping("/apiLogin")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        log.info("login controller");
        String jwt = "";
        Authentication authentication = UsernamePasswordAuthenticationToken
                .unauthenticated(loginRequestDTO.username(), loginRequestDTO.password());
        Authentication authenticationResponse = authenticationManager.authenticate(authentication);

        if(null != authenticationResponse && authenticationResponse.isAuthenticated()){
            log.info("Secret: " + ProjectConstants.JWT_SECURITY_SECRET);
            SecretKey secretKey = Keys.hmacShaKeyFor(ProjectConstants.JWT_SECURITY_SECRET.getBytes(StandardCharsets.UTF_8));
            jwt = Jwts.builder().issuer("Test Issuer")
                    .subject("Test Subject")
                    .claim("username", authenticationResponse.getName())
                    .claim("authority", authenticationResponse.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                    .issuedAt(new Date())
                    .expiration(new Date((new Date()).getTime() + 30000000))
                    .signWith(secretKey).compact();
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new LoginResponseDTO(HttpStatus.OK.getReasonPhrase(), jwt));
    }

}
