package ar.edu.unnoba.poo2025.torneos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    
	@GetMapping("/api/hello")
	public String sayHello() {
        System.out.println("Hello endpoint was called");
		return "Hello, World!";
	}

    @GetMapping("/api/goodbye")
    public String sayGoodbye() {
        System.out.println("Goodbye endpoint was called");
        return "Goodbye, World!";
    }

}
