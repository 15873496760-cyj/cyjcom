package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WF on 2019/8/22 15:50
 */
@RestController
@RequestMapping
public class LoginController {


    @RequestMapping("login")
    //获取当前登录的用户名
    public Map<String,String> login(){
        //获取当前的登录用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //定义Map用于存放用户名
        Map<String,String> map = new HashMap<>();
        //放当前用户名，并返回
        map.put("name",name);
        //返回
        return map;
    }
}
