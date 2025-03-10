package com.example.Articles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ArticlesApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArticlesApplication.class, args);
        System.out.println("Приложение Articles успешно запущено!");
    }
}
