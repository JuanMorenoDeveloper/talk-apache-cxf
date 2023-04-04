package ve.com.proitcsolution.cxf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.temp.calculator.CalculatorSoap;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CxfCourseClientApplication implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(CxfCourseClientApplication.class);
  @Autowired private CalculatorSoap calculatorClient;

  public static void main(String[] args) {
    SpringApplication.run(CxfCourseClientApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info("The result is {}", calculatorClient.add(1, 2));
  }
}
