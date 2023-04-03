package ve.com.proitcsolution.cxf.configuration;

import org.springframework.context.annotation.Configuration;

@Configuration
public class CxfConfiguration {

  private final CalculatorConfig config;

  public CxfConfiguration(CalculatorConfig config) {
    this.config = config;
  }

  /*@Bean
  public Cl calculatorSoap() {
    var proxy = new JaxWsProxyFactoryBean();
    proxy.setAddress(stmConfiguration.configuracionUrl());
    LoggingFeature loggingFeature = new LoggingFeature();
    loggingFeature.setLimit(100 * 1024);
    loggingFeature.setPrettyLogging(true);
    proxy.getFeatures().add(loggingFeature);
    proxy.getOutInterceptors().add(wss4jOut());
    var configuracionPostpagoSTM = proxy.create(ConfiguracionPostpagoSTM.class);
    return configuracionPostpagoSTM;
  }*/
}
