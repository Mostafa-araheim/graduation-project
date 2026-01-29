package com.example.pharma;

import com.example.pharma.repository.Core.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class PharmaApplication {

        public static void main(String[] args) {
        SpringApplication.run(PharmaApplication.class, args);
    }

}
