package ve.com.proitcsolution.cxf.configuration;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.security.auth.callback.CallbackHandler;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.common.ConfigurationConstants;
import org.apache.wss4j.common.WSS4JConstants;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.apache.wss4j.stax.ext.WSSConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.temp.calculator.CalculatorSoap;

@Configuration
public class CxfConfiguration {

  private final CalculatorConfig config;

  public CxfConfiguration(CalculatorConfig config) {
    this.config = config;
  }

  @Bean
  public CalculatorSoap client()
      throws CertificateException,
          KeyStoreException,
          NoSuchAlgorithmException,
          IOException,
          KeyManagementException {
    var proxy = new JaxWsProxyFactoryBean();
    proxy.setAddress(config.url());
    var loggingFeature = new LoggingFeature();
    loggingFeature.setPrettyLogging(true);
    proxy.getFeatures().add(loggingFeature);
    proxy.getOutInterceptors().add(wss4jOut());
    proxy.getInInterceptors().add(wss4jIn());
    var client = proxy.create(CalculatorSoap.class);
    setupSsl(client);
    return client;
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
    properties.put(ConfigurationConstants.ENC_KEY_TRANSPORT, WSS4JConstants.KEYTRANSPORT_RSAOAEP);*/
    return properties;
  }

  public void setupSsl(final Object cliente)
      throws KeyStoreException,
          NoSuchAlgorithmException,
          CertificateException,
          IOException,
          KeyManagementException {
    var client = ClientProxy.getClient(cliente);
    var httpConduit = (HTTPConduit) client.getConduit();
    var tlsParams = new TLSClientParameters();
    var certificate =
        CertificateFactory.getInstance("X.509")
            .generateCertificate(getClass().getClassLoader().getResourceAsStream(config.sslFile()));
    var keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    keyStore.load(null, null);
    keyStore.setCertificateEntry("calculator", certificate);
    var trustManagerFactory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(keyStore);
    var sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
    tlsParams.setSslContext(sslContext);
    tlsParams.setDisableCNCheck(true);
    httpConduit.setTlsClientParameters(tlsParams);
  }

  public Properties keystoreProperties() {
    var properties = new Properties();
    properties.put(
        "org.apache.wss4j.crypto.merlin.provider", "org.apache.wss4j.common.crypto.Merlin");
    properties.put("org.apache.wss4j.crypto.merlin.keystore.type", config.keystoreType());
    properties.put("org.apache.wss4j.crypto.merlin.keystore.password", config.keystorePassword());
    properties.put("org.apache.wss4j.crypto.merlin.keystore.alias", config.keystoreAlias());
    properties.put(
        "org.apache.wss4j.crypto.merlin.keystore.private.password", config.keystorePassword());
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
}
