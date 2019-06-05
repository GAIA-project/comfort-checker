package gr.cti.gaia.comfort.checker;

import net.sparkworks.cargo.client.config.CargoClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"gr.cti.gaia.comfort.checker", CargoClientConfig.CARGO_CLIENT_BASE_PACKAGE_NAME})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
