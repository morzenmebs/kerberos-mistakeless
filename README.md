# Kerberos Client Library (Pure Java)

This project provides a **pure Java Kerberos client** built on the JDK's JAAS
`Krb5LoginModule`. It is designed to authenticate machines or users against a Kerberos
KDC without relying on native OS Kerberos libraries, making it portable across Windows,
Linux, and macOS.

## Why the JDK JAAS module?

The JDK ships with a Java-only Kerberos implementation (`Krb5LoginModule`) that can
authenticate without external native libraries. This library wraps that module with a
small, explicit Java API for Kerberos authentication.

## Usage

```java
import com.mistakeless.kerberos.auth.KerberosAuthenticator;
import com.mistakeless.kerberos.auth.KerberosLoginResult;
import com.mistakeless.kerberos.config.KerberosClientConfig;
import java.io.File;

KerberosClientConfig config = KerberosClientConfig.builder()
    .realm("EXAMPLE.COM")
    .kdcAddress("kdc.example.com", 88)
    .domain("example.com")
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
- The authenticator generates a temporary `krb5.conf` with your realm and KDC settings
  and instructs the JDK to use it for the login session.
