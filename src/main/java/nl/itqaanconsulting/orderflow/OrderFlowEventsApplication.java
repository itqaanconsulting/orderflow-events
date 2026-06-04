package nl.itqaanconsulting.orderflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class OrderFlowEventsApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderFlowEventsApplication.class, args);
    }
}
