package ai.supporter.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public CommandLineRunner run(BusinessOrchestrator orchestrator) {
        return args -> {
            // TODO: Trigger the business orchestration here
            System.out.println("Worker started. Business logic will be triggered here.");
        };
    }
}
