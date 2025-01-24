package co.istad.streamsavro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/test")
    public String test() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String viewStatistic() {
        return "statistic";
    }

}
