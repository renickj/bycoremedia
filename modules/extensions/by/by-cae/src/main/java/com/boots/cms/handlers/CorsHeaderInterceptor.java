package com.boots.cms.handlers;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ssmyka on 29.02.2016.
 */
public class CorsHeaderInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setHeader("Access-Control-Allow-Headers","origin, x-requested-with, accept,content-type");
        response.setHeader("Access-Control-Allow-Methods","GET, PUT, POST, DELETE");
        response.setHeader("Access-Control-Allow-Origin","*");
        return true;
    }
}
