package com.huddlespace.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HuddleSpaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuddleSpaceApplication.class, args);
        System.out.println("✅ HuddleSpace backend is running at http://localhost:8080");
    }
}
