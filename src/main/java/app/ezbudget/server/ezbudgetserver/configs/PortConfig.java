package app.ezbudget.server.ezbudgetserver.configs;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PortConfig {

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> portCustomizer() {
        return factory -> factory.setPort(80);
    }
}