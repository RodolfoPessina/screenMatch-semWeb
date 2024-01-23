package br.com.rodolfopessina.screenmatch;

import br.com.rodolfopessina.screenmatch.principal.Principal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {

		SpringApplication.run(ScreenmatchApplication.class, args);

	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal();
		principal.exibirMenu();



	}
}
