package ve.com.proitcsolution.cxf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CxfCourseApplication {

    public static void main(String[] args) {
        SpringApplication.run(CxfCourseApplication.class, args);
    }

}
