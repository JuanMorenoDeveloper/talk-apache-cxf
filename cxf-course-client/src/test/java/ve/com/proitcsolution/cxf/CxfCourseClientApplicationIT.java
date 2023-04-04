package ve.com.proitcsolution.cxf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.temp.calculator.CalculatorSoap;

@ActiveProfiles("test")
@SpringBootTest(
    properties = {
      "calculator.url=https://localhost:1443",
      "calculator.sslFile=certs/CertificateAuthorityCertificate.pem" // MockServer SSL certificate
    })
@ExtendWith(MockServerExtension.class)
@MockServerSettings(ports = {CxfCourseClientApplicationIT.MOCKSERVER_PORT})
class CxfCourseClientApplicationIT {

  public static final int MOCKSERVER_PORT = 1443;
  @Autowired private CalculatorSoap calculatorClient;

  private final ClientAndServer client;

  CxfCourseClientApplicationIT(ClientAndServer client) {
    this.client = client;
  }

  @BeforeEach
  public void resetClient() {
    client.reset();
  }

  @Test
  void whenAdd_thenUseSignatureAndEncryption() throws IOException {
    client
        .when(
            request()
                .withMethod("POST")
                .withBody(contains("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
                .withBody(contains("http://www.w3.org/2001/04/xmlenc#")))
        .respond(
            response()
                .withStatusCode(200)
                .withBody(Files.readString(Path.of("src/test/resources/addResponse.xml"))));

    int result = calculatorClient.add(1, 2);

    assertThat(result).isEqualTo(3);
    client.verify(
        request()
            .withMethod("POST")
            .withBody(contains("http://www.w3.org/2000/09/xmldsig#rsa-sha1")),
        VerificationTimes.exactly(1));
  }
}
