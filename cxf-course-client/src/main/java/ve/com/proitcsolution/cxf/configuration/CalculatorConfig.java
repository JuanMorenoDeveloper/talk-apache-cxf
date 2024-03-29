package ve.com.proitcsolution.cxf.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "calculator")
public record CalculatorConfig(
    String url,
    String truststoreAlias,
    String truststorePassword,
    String truststoreFile,
    String truststoreType,
    String keystoreAlias,
    String keystorePassword,
    String keystoreFile,
    String keystoreType,
    String sslFile) {}
