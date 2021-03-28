package com.lcydream.open.springmvcrest.controller;

import com.lcydream.open.springmvcrest.annotation.OptionsMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CachedRestController {

    @RequestMapping
    @ResponseBody //没有设置缓存
    //服务端和客户端没有形成默契（状态码）
    //Http协议，Rest继承
    public String helloWord(){ //200 / 500 / 400
        return "Hello,World"; //Body="Hello,World" String
    }

    @RequestMapping("/cache")
    public ResponseEntity<String> cacheHelloWord(
            @RequestParam(required = false,defaultValue = "false") boolean cached){
        if(cached){
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }else {
            return new ResponseEntity<>("Hello,Cache",HttpStatus.OK);
        }
        /*ResponseEntity<String> responseEntity =
                new ResponseEntity<>("Hello,Cache",HttpStatus.OK);
        return responseEntity;*/
    }

    @OptionsMapping("/optionsMapping")
    public String optionsMapping(){
        return "optionsMapping";
    }

}
