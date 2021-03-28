package com.lcydream.open.mvc.comtroller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class HomeController {

    @GetMapping({"/",""})
    public String home(Model model){
        //model.addAttribute("message","罗春云");
        model.addAttribute("string",new StringUtil());
        return "home";
    }

    @ModelAttribute(name = "message")
    public String message(){
        return "Hello,world";
    }


    public static class StringUtil{
        public StringUtil(){}

        public boolean isNotBlank(String value){
            return StringUtils.hasText(value);
        }
    }
}
