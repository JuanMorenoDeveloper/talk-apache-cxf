package ve.com.proitcsolution.cxf.configuration;

import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ve.com.proitcsolution.cxf.controller.CalculatorController;

@Configuration
public class CxfConfiguration {

  private final CalculatorConfig config;
  private final Bus bus;

  public CxfConfiguration(CalculatorConfig config, Bus bus) {
    this.config = config;
    this.bus = bus;
  }

  @Bean
  public Endpoint calculatorSoap() {
    var endpoint = new EndpointImpl(bus, new CalculatorController());
    String endpointUrl = config.path();
    String wsdlLocation = config.wsdlLocation();
    endpoint.publish(endpointUrl);
    endpoint.setWsdlLocation(wsdlLocation);
    return endpoint;
  }
}
