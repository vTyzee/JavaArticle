package com.example.articles;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ArticlesApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ArticlesApplication.class, args);
		System.out.println("Work in progress");
	}

	@Override
	public void run(String... args) throws Exception {

	}
}

