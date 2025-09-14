package vn.iotstar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "vn.iotstar.repository")
@EntityScan(basePackages = "vn.iotstar.entity")
public class BtwebApplication {
    public static void main(String[] args) {
        SpringApplication.run(BtwebApplication.class, args);
    }
}