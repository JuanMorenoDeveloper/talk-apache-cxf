package ve.com.proitcsolution.cxf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.temp.calculator.CalculatorSoap;

@Profile("!test")
@Component
public class CommandLineTaskExecutor implements CommandLineRunner {
  private static final Logger log = LoggerFactory.getLogger(CommandLineTaskExecutor.class);
  @Autowired private CalculatorSoap calculatorClient;

  @Override
  public void run(String... args) throws Exception {
    log.info("The result is {}", calculatorClient.add(1, 2));
  }
}
