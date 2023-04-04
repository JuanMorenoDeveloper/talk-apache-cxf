package ve.com.proitcsolution.cxf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CxfCourseClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(CxfCourseClientApplication.class, args);
  }
}
