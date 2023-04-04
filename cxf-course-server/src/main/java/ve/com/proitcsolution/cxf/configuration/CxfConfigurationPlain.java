package ve.com.proitcsolution.cxf.configuration;

import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ve.com.proitcsolution.cxf.controller.CalculatorController;

@Configuration
@ConditionalOnProperty(prefix = "calculator", name = "mode", havingValue = "plain")
public class CxfConfigurationPlain {

    private final CalculatorConfig config;
    private final Bus bus;

    public CxfConfigurationPlain(CalculatorConfig config, Bus bus) {
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
