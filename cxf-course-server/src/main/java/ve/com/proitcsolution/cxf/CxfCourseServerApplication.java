package ve.com.proitcsolution.cxf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CxfCourseServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(CxfCourseServerApplication.class, args);
  }
}
