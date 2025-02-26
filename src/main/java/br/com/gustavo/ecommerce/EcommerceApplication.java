package br.com.gustavo.ecommerce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class EcommerceApplication implements CommandLineRunner {

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("ENCODE " + passwordEncoder.encode("123456") );

		// como o spring security verifica as credenciais quando o usuario for logar no sistema
		boolean result = passwordEncoder.matches("123456", "$2a$10$K1odOK387dhqXRx3kOyDDeKu4CZ1y5KYPcSF1vkHf26mjQixR7hpG");
		System.out.println("Resultado = " + result);
	}
}
