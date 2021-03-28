package com.lcydream.open.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: FallbackController
 * @author: LuoChunYun
 * @Date: 2019/4/29 21:49
 * @Description: TODO
 */
@RestController
@RequestMapping("/fallbackRest")
public class FallbackController {

    @RequestMapping("")
    public String fallback(){
        return "error";
    }
}