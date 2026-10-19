package ro.frit.ticketing.config;

import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.event.ValidatingEntityCallback;

// Checks the Bean Validation constraints of a document before it is saved; an invalid one throws ConstraintViolationException.
@Configuration(proxyBeanMethods = false)
public class MongoValidationConfig {

    @Bean
    ValidatingEntityCallback validatingEntityCallback(Validator validator) {
        return new ValidatingEntityCallback(validator);
    }
}
