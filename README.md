# Kerberos Client Library (Pure Java)

This project provides a **pure Java Kerberos client** built on [Apache Kerby](https://directory.apache.org/kerby/).
It is designed to authenticate machines or users against a Kerberos KDC without relying on
native OS Kerberos libraries, making it portable across Windows, Linux, and macOS.

## Why Apache Kerby?

Apache Kerby is a Java-only Kerberos implementation that does not depend on system
`krb5` libraries. This library wraps Kerby client APIs to provide a small, explicit
Java API for Kerberos authentication.

## Usage

```java
import com.mistakeless.kerberos.auth.KerberosAuthenticator;
import com.mistakeless.kerberos.auth.KerberosLoginResult;
import com.mistakeless.kerberos.config.KerberosClientConfig;
import java.io.File;

KerberosClientConfig config = KerberosClientConfig.builder()
    .realm("EXAMPLE.COM")
    .kdcAddress("kdc.example.com", 88)
    .build();

KerberosAuthenticator authenticator = new KerberosAuthenticator(config);
KerberosLoginResult result = authenticator.authenticateWithPassword(
    "user@EXAMPLE.COM",
    "correct-horse-battery-staple"
);

System.out.println("Authenticated principal: " + result.getClientPrincipal());
```

For keytab authentication:

```java
KerberosLoginResult result = authenticator.authenticateWithKeytab(
    "service/host@EXAMPLE.COM",
    new File("/path/to/service.keytab")
);
```

## Build

```bash
mvn -DskipTests package
```

## Notes

- This library only performs Kerberos client authentication; it does not manage KDC setup.
- You must point the client at your KDC host and realm explicitly through the config builder.
- To use with an LDAP or other Kerberos-aware service, use the returned TGT to request
  service tickets with Kerby APIs.
