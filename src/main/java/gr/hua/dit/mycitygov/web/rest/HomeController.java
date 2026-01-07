package gr.hua.dit.mycitygov.web.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//TODO REMOVE TEST
@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Hello from MyCityGov! The server is running perfectly.";
    }
}
