package org.octopus.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/hello")
public class HelloWorldController {
    @GetMapping
    public String helloWorld() {
        return "Hello world";
    }

    @PostMapping
    public String unCovered() {
        return "Uncovered method";
    }
}
