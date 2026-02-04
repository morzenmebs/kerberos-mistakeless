package com.mistakeless.kerberos.auth;

import com.mistakeless.kerberos.config.KerberosClientConfig;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public final class KerberosAuthenticator {
    private final KerberosClientConfig clientConfig;

    public KerberosAuthenticator(KerberosClientConfig clientConfig) {
        this.clientConfig = Objects.requireNonNull(clientConfig, "clientConfig");
    }

    public KerberosLoginResult authenticateWithPassword(String principal, String password)
        throws LoginException, IOException {
        Objects.requireNonNull(principal, "principal");
        Objects.requireNonNull(password, "password");
        Path krb5Conf = writeKrb5Conf();
        System.setProperty("java.security.krb5.conf", krb5Conf.toString());

        Configuration configuration = buildJaasConfig(principal, null, false);
        CallbackHandler handler = callbacks -> handlePasswordCallbacks(callbacks, principal, password);
        LoginContext loginContext = new LoginContext("Krb5Login", null, handler, configuration);
        loginContext.login();

        Subject subject = loginContext.getSubject();
        KerberosTicket tgt = extractTgt(subject);
        return new KerberosLoginResult(principal, tgt, subject, Instant.now());
    }

    public KerberosLoginResult authenticateWithKeytab(String principal, File keytabFile)
        throws LoginException, IOException {
        Objects.requireNonNull(principal, "principal");
        Objects.requireNonNull(keytabFile, "keytabFile");
        Path krb5Conf = writeKrb5Conf();
        System.setProperty("java.security.krb5.conf", krb5Conf.toString());

        Configuration configuration = buildJaasConfig(principal, keytabFile, true);
        LoginContext loginContext = new LoginContext("Krb5Login", null, null, configuration);
        loginContext.login();

        Subject subject = loginContext.getSubject();
        KerberosTicket tgt = extractTgt(subject);
        return new KerberosLoginResult(principal, tgt, subject, Instant.now());
    }

    private KerberosTicket extractTgt(Subject subject) throws LoginException {
        Optional<KerberosTicket> ticket = subject.getPrivateCredentials(KerberosTicket.class)
            .stream()
            .findFirst();
        if (ticket.isEmpty()) {
            throw new LoginException("Kerberos TGT not found in subject credentials.");
        }
        return ticket.get();
    }

    private void handlePasswordCallbacks(Callback[] callbacks, String principal, String password)
        throws UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                ((NameCallback) callback).setName(principal);
            } else if (callback instanceof PasswordCallback) {
                ((PasswordCallback) callback).setPassword(password.toCharArray());
            } else {
                throw new UnsupportedCallbackException(callback, "Unsupported callback type.");
            }
        }
    }

    private Configuration buildJaasConfig(String principal, File keytabFile, boolean useKeytab) {
        Map<String, String> options = new HashMap<>();
        options.put("principal", principal);
        options.put("refreshKrb5Config", "true");
        options.put("doNotPrompt", Boolean.toString(useKeytab));
        options.put("useTicketCache", "false");
        options.put("isInitiator", "true");

        if (useKeytab) {
            options.put("useKeyTab", "true");
            options.put("keyTab", keytabFile.getAbsolutePath());
            options.put("storeKey", "true");
        }

        AppConfigurationEntry entry = new AppConfigurationEntry(
            "com.sun.security.auth.module.Krb5LoginModule",
            AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
            options
        );

        return new Configuration() {
            @Override
            public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
                return new AppConfigurationEntry[] { entry };
            }
        };
    }

    private Path writeKrb5Conf() throws IOException {
        StringBuilder config = new StringBuilder();
        config.append("[libdefaults]\n");
        config.append("  default_realm = ").append(clientConfig.getRealm()).append('\n');
        config.append("  dns_lookup_kdc = false\n");
        config.append("  dns_lookup_realm = false\n");
        if (!clientConfig.isAllowTcp()) {
            config.append("  udp_preference_limit = 2147483647\n");
        }
        config.append("\n[realms]\n");
        config.append("  ").append(clientConfig.getRealm()).append(" = {\n");
        config.append("    kdc = ").append(clientConfig.getKdcHost())
            .append(':').append(clientConfig.getKdcPort()).append('\n');
        config.append("  }\n");

        if (clientConfig.getDomain() != null) {
            config.append("\n[domain_realm]\n");
            config.append("  .").append(clientConfig.getDomain()).append(" = ")
                .append(clientConfig.getRealm()).append('\n');
            config.append("  ").append(clientConfig.getDomain()).append(" = ")
                .append(clientConfig.getRealm()).append('\n');
        }

        Path tempFile = Files.createTempFile("krb5-", ".conf");
        Files.writeString(tempFile, config.toString(), StandardCharsets.UTF_8);
        tempFile.toFile().deleteOnExit();
        return tempFile;
    }
}
