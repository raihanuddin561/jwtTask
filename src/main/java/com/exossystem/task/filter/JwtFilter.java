package com.exossystem.task.filter;

import com.exossystem.task.constants.SecurityConstants;
import com.exossystem.task.model.response.PayLoadData;
import com.exossystem.task.service.UserService;
import com.exossystem.task.utility.JwtUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Component

@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtility jwtUtility;
    @Autowired
    private UserService userService;
    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        String token = null;
        String username = null;
        Set<Map.Entry<String, Object>> data = null;
        if(null!=authorization && authorization.startsWith(SecurityConstants.TOKEN_PREFIX)){
            token = authorization.replace(SecurityConstants.TOKEN_PREFIX,"");
            data = jwtUtility.getPayLoadData(token);
            PayLoadData payLoadData = new PayLoadData();
            for(Map.Entry<String, Object> map:data){
                if(map.getKey().equals("name")) payLoadData.setName(map.getValue().toString());
                if(map.getKey().equals("id")) payLoadData.setId(map.getValue().toString());
                if(map.getKey().equals("validated")) payLoadData.setValidated(map.getValue().toString());
            }
            ObjectMapper objectMapper = new ObjectMapper();
            String js=objectMapper.writeValueAsString(payLoadData);
            log.info("Payload Data: "+js);
            username = jwtUtility.getUsernameFromToken(token);
        }
        if(null!=username && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails = userService.loadUserByUsername(username);
            if(jwtUtility.validateToken(token,userDetails)){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }

        }
        filterChain.doFilter(request,response);
    }
}
