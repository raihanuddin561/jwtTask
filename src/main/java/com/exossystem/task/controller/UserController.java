package com.exossystem.task.controller;

import com.exossystem.task.model.request.JwtRequest;
import com.exossystem.task.model.response.JwtResponse;
import com.exossystem.task.service.UserService;
import com.exossystem.task.utility.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {
    @GetMapping("/get")
    public String hello(){
        return "hello testing";
    }
     @Autowired
    private JwtUtility jwtUtility;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
   @PostMapping("/authenticate")
    public JwtResponse authenticate(@RequestBody JwtRequest jwtRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            jwtRequest.getUsername(),
                            jwtRequest.getPassword()
                    )
            );
        }catch (BadCredentialsException e){
            throw new Exception("Invalid credentials",e);
        }
        final UserDetails userDetails= userService.loadUserByUsername(jwtRequest.getUsername());
        final String token = jwtUtility.genrateToken(userDetails);
        return new JwtResponse(token);
    }
}
