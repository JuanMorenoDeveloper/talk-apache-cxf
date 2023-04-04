package ve.com.proitcsolution.cxf.configuration;

import jakarta.xml.ws.Endpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.security.auth.callback.CallbackHandler;
import org.apache.cxf.Bus;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.common.ConfigurationConstants;
import org.apache.wss4j.common.WSS4JConstants;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.apache.wss4j.stax.ext.WSSConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ve.com.proitcsolution.cxf.controller.CalculatorController;

@Configuration
@ConditionalOnProperty(prefix = "calculator", name = "mode", havingValue = "wss")
public class CxfConfigurationWSS {

  private final CalculatorConfig config;
  private final Bus bus;

  public CxfConfigurationWSS(CalculatorConfig config, Bus bus) {
    this.config = config;
    this.bus = bus;
  }

  @Bean
  public Endpoint calculatorSoap() {
    var endpoint = new EndpointImpl(bus, new CalculatorController());
    endpoint.getInInterceptors().add(wss4jIn());
    endpoint.getInInterceptors().add(loggingInInterceptor());
    endpoint.getOutInterceptors().add(wss4jOut());
    endpoint.getOutInterceptors().add(loggingOutInterceptor());
    endpoint.publish(config.path());
    endpoint.setWsdlLocation(config.wsdlLocation());
    return endpoint;
  }

  public WSS4JOutInterceptor wss4jOut() {
    return new WSS4JOutInterceptor(wss4jProperties());
  }

  public WSS4JInInterceptor wss4jIn() {
    return new WSS4JInInterceptor(wss4jProperties());
  }

  private HashMap<String, Object> wss4jProperties() {
    var properties = new HashMap<String, Object>();
    properties.put(
        ConfigurationConstants.ACTION,
        ConfigurationConstants.SIGNATURE + " " + ConfigurationConstants.ENCRYPTION);
    properties.put(ConfigurationConstants.PW_CALLBACK_REF, clientKeystorePasswordCallback());
    properties.putAll(signingProperties(keystoreProperties(), truststoreProperties()));
    properties.putAll(encryptionProperties(truststoreProperties(), keystoreProperties()));
    return properties;
  }

  public CallbackHandler clientKeystorePasswordCallback() {
    var passwords = new HashMap<String, String>();
    passwords.put(config.keystoreAlias(), config.keystorePassword());
    passwords.put(config.truststoreAlias(), config.truststorePassword());
    return callbacks -> {
      for (var callback : callbacks) {
        if (callback instanceof WSPasswordCallback passCallback
            && passwords.containsKey(passCallback.getIdentifier())) {
          passCallback.setPassword(passwords.get(passCallback.getIdentifier()));
          break;
        }
      }
    };
  }

  public Map<String, Object> signingProperties(
      final Properties signProp, final Properties verSignProp) {
    var properties = new HashMap<String, Object>();
    properties.put("signingProperties", signProp);
    properties.put("signatureVerificationProperties", verSignProp);
    properties.put(ConfigurationConstants.SIG_PROP_REF_ID, "signingProperties");
    properties.put(ConfigurationConstants.SIG_VER_PROP_REF_ID, "signatureVerificationProperties");
    properties.put(ConfigurationConstants.SIG_KEY_ID, "DirectReference");
    properties.put(ConfigurationConstants.SIGNATURE_USER, config.keystoreAlias());
    properties.put(
        ConfigurationConstants.SIGNATURE_PARTS,
        "{Element}{" + WSSConstants.NS_SOAP11 + "}" + WSS4JConstants.ELEM_BODY);
    /*properties.put(ConfigurationConstants.SIG_ALGO,
    "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");*/
    return properties;
  }

  public Map<String, Object> encryptionProperties(
      final Properties encProp, final Properties decProp) {
    final Map<String, Object> properties = new HashMap<>();
    properties.put("encryptionProperties", encProp);
    properties.put("decryptionProperties", decProp);
    properties.put(ConfigurationConstants.ENC_PROP_REF_ID, "encryptionProperties");
    properties.put(ConfigurationConstants.DEC_PROP_REF_ID, "decryptionProperties");
    properties.put(ConfigurationConstants.ENC_KEY_ID, "DirectReference");
    properties.put(ConfigurationConstants.ENCRYPTION_USER, config.truststoreAlias());
    properties.put(
        ConfigurationConstants.ENCRYPTION_PARTS,
        "{Content}{" + WSSConstants.NS_SOAP11 + "}" + WSS4JConstants.ELEM_BODY);
    /*properties.put(ConfigurationConstants.ENC_SYM_ALGO, WSS4JConstants.AES_128);
    properties.put(ConfigurationConstants.ENC_KEY_TRANSPORT, WSS4JConstants.KEYTRANSPORT_RSAOAEP)*/
    ;
    return properties;
  }

  public Properties keystoreProperties() {
    var properties = new Properties();
    properties.put(
        "org.apache.wss4j.crypto.merlin.provider", "org.apache.wss4j.common.crypto.Merlin");
    properties.put("org.apache.wss4j.crypto.merlin.keystore.type", config.keystoreType());
    properties.put("org.apache.wss4j.crypto.merlin.keystore.password", config.keystorePassword());
    properties.put("org.apache.wss4j.crypto.merlin.keystore.alias", config.keystoreAlias());
    properties.put("org.apache.wss4j.crypto.merlin.keystore.file", config.keystoreFile());
    return properties;
  }

  public Properties truststoreProperties() {
    var properties = new Properties();
    properties.put(
        "org.apache.wss4j.crypto.merlin.provider", "org.apache.wss4j.common.crypto.Merlin");
    properties.put("org.apache.wss4j.crypto.merlin.keystore.type", config.truststoreType());
    properties.put("org.apache.wss4j.crypto.merlin.keystore.password", config.truststorePassword());
    properties.put("org.apache.wss4j.crypto.merlin.keystore.alias", config.truststoreAlias());
    properties.put("org.apache.wss4j.crypto.merlin.keystore.file", config.truststoreFile());
    return properties;
  }

  public LoggingOutInterceptor loggingOutInterceptor() {
    var loggingOutInterceptor = new LoggingOutInterceptor();
    loggingOutInterceptor.setPrettyLogging(true);
    return loggingOutInterceptor;
  }

  public LoggingInInterceptor loggingInInterceptor() {
    var loggingInInterceptor = new LoggingInInterceptor();
    loggingInInterceptor.setPrettyLogging(true);
    return loggingInInterceptor;
  }
}
