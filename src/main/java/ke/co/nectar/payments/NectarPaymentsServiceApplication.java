package ke.co.nectar.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "ke.co.nectar.payments")
@ConfigurationPropertiesScan("ke.co.nectar.payments.configurations")
@EnableJpaRepositories(basePackages="ke.co.nectar.payments.repository")
public class NectarPaymentsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NectarPaymentsServiceApplication.class, args);
    }
}
