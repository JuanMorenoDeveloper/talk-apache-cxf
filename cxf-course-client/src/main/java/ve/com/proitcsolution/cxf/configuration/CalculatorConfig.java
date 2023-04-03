package ve.com.proitcsolution.cxf.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "calculator")
public record CalculatorConfig(String url) {

}
