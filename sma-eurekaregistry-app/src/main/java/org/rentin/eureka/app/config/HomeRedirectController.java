package org.sma.eureka.app.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeRedirectController {

    @GetMapping("/home")
    public String home() {
        return "redirect:/home/index.html";
    }

    @GetMapping("/home/")
    public String homeSlash() {
        return "redirect:/home/index.html";
    }
}

