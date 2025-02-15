package ru.akvine.istochnik;

import org.springframework.boot.SpringApplication;

public class TestIstochnikApplication {

	public static void main(String[] args) {
		SpringApplication.from(IstochnikApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
