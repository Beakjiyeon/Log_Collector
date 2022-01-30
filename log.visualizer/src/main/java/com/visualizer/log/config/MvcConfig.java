//package com.visualizer.log.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///*
//https://velog.io/@ydh6226/Spring-404-NotFound-Response-Custom
// */
//@EnableWebMvc
//@Configuration
//class MvcConfig implements WebMvcConfigurer {
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/resources/static/**")
//                .addResourceLocations("/resources/static/");
//    }
//}