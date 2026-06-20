package com.example.naijaWallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class NaijaWalletApplication {

	public static void main(String[] args) {
		SpringApplication.run(NaijaWalletApplication.class, args);
	}

}
