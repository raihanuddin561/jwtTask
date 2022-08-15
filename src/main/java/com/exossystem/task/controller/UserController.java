package com.exossystem.task.controller;

import com.exossystem.task.model.request.JwtRequest;
import com.exossystem.task.model.response.JwtResponse;
import com.exossystem.task.service.UserService;
import com.exossystem.task.utility.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {
     @Autowired
    private JwtUtility jwtUtility;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @GetMapping("/get")
    public ResponseEntity<Integer> get(){
        return new ResponseEntity<>(1, HttpStatus.OK);
    }
    @GetMapping("/getAll")
    public ResponseEntity<List<Integer>> getAll(){
        return new ResponseEntity<>(List.of(1,2),HttpStatus.OK);
    }
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
