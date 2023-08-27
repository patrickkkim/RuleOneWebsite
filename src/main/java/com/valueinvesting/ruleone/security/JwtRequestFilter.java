//package com.valueinvesting.ruleone.security;
//
//import com.valueinvesting.ruleone.services.CustomUserDetailsService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@Order(1)
//public class JwtRequestFilter extends OncePerRequestFilter {
//
//    private JwtUtil jwtUtil;
//    private CustomUserDetailsService userDetailsService;
//
//    @Autowired
//    public JwtRequestFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
//        this.jwtUtil = jwtUtil;
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String username, jwtStr;
//        final String authorizationHeader = request.getHeader("Authorization");
//
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            jwtStr = authorizationHeader.substring(7);
//            username = jwtUtil.extractUsername(jwtStr);
//        } else throw new ServletException("Authorization header is missing");
//
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//            if (jwtUtil.validateToken(jwtStr, userDetails)) {
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                        userDetails, null, userDetails.getAuthorities());
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            } else throw new ServletException("JWT is not valid");
//        } else throw new ServletException("Username does not exist or authentication already exists");
//        filterChain.doFilter(request, response);
//    }
//}
