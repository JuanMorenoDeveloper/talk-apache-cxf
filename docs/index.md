---
author: Juan Moreno
title: Apache CXF
date: March 03, 2023
---
# Apache CXF

## Agenda
* Cryptography concepts
* Apache CXF
* Apache CXF - Integration with Spring Boot
* Developing a client/endpoint
* WS Security
* Testing

## Cryptography

Cryptography is **a method of protecting data and communications using codes and digital keys**  to ensure that the information is delivered untampered to the intended sender for further processing.

![](images/cryptographic-key.png)

## Cryptography - Digital Signatures

A digital signature is a cryptographic means through which one can **verify** a document's **origin**, the **sender's identity**, the time, and date a document was signed or sent, etc.

![](images/wax-stamp.jpg)

## Cryptography - Plaintext/Ciphertext

As cryptography operates with data, this can be either in **plaintext** (cleartext) or **ciphertext** (cryptogram). 

Plaintext data means that the message is in natural format, readable to an attacker. Ciphertext data means that the message is in an unreadable format to the attacker but readable to the intended recipient.

## Cryptography - Encryption/Decryption

You can convert the message from plaintext to ciphertext using the process of **encryption**. 
Similarly, you can convert ciphertext into plaintext via **decryption** by using a cryptographic algorithm and key used to make the original message.

## Cryptography - Symmetric Encryption/Decryption

Symmetric or shared key encryption is a method where both parties share a key, kept secret by both parties. 
For example, sender A can encrypt a message with a shared key, then receiver B can decrypt the encrypted message only with that key.

## Cryptography - Symmetric Encryption/Decryption

![](images/symmetric_encryption.png)

## Cryptography - Asymmetric Encryption/Decryption

Asymmetrical encryption uses a pair of mathematical related keys, one for encryption and the other for decryption.
With public key cryptography, a user has a pair of public and private keys.

## Cryptography - Asymmetric Encryption/Decryption - Keys
These are generated using a large prime number and a key function. The keys are related mathematically, but cannot be derived from one another.

![](images/250px-Public_key_making.png)

## Cryptography - Asymmetric Encryption/Decryption 

With these keys we can encrypt messages. For example, if Bob wants to send a message to Alice, he can encrypt a message using her public key. Alice can then decrypt this message using her private key. Only Alice can decrypt this message as she is the only one with the private key.

## Cryptography - Asymmetric Encryption/Decryption

![](images/Public_key_encryption.png)

## Cryptography - Asymmetric Signing

Messages can also be signed. This allows you to ensure the authenticity of the message. If Alice wants to send a message to Bob, and Bob wants to be sure that it is from Alice, Alice can sign the message using her private key. Bob can then verify that the message is from Alice by using her public key.

## Cryptography - Asymmetric Signing

![](images/280px-Public_key_signing.png)

## Key and Certificate Management Tool - Keytool

* Java includes the keytool utility in its releases.
* We can use `keytool` to manage keys and certificates and store them in a keystore. 
* The `keytool` command allows us to create self-signed certificates and show information about the keystore

## Apache CXF - Introduction

* Apache CXF is an open source services framework.
* It supports multiple APIs like JAX-WS, JAX-RS.
* Compatible with protocols like SOAP, XML/HTTP, RESTful HTTP, and others

## Apache CXF - Spring Boot Integration
**Spring Boot** makes it easy to create stand-alone, production-grade Spring based Applications that you can "just run".

To integrate CXF with Spring boot we need to add the following dependencies: _spring-boot-starter-web_,_cxf-spring-boot-starter-jaxws_, _cxf-rt-transports-http_, _cxf-rt-ws-security_ and _cxf-rt-features-logging_.

## Developing a SOAP Client - Model generation (WSDL-first)

1. Download the WSDL file and place it in the resources' folder.
2. Use the maven _cxf-codegen-plugin_
 * Configure the _bindingFile_ for model generation. 
 * Configure the wsdlLocation.
 * Set the output path with _extraargs_
3. Use the _build-helper-maven-plugin_ to add the generated source to the project.

## Developing a SOAP Client - Spring Boot Configuration

1. Define in the `@Configuration` class a `@Bean` with the client implementation. It's the class with the annotation `@WebServiceClient`.
2. Create a `JaxWsProxyFactoryBean` for the client class.
3. Assign Endpoint URL, and assign the Interceptors as needed. 

## Developing an Endpoint - Spring Boot Configuration

If the WSDL already exists, follow the _WSDL-first_ steps plus:

1. Define in the `@Configuration` class an `javax.xml.ws.Endpoint` implementation. It's the interface with the annotation `@WebService`.
2. Assign Endpoint URL, and Interceptors. 

## How to use WS-Security?

## Testing

## References

* [Introduction to Java Encryption/Decryption - Dev.java](https://dev.java/learn/security/intro/)
* [Apache CXF -- WS-Security](https://cxf.apache.org/docs/ws-security.html)