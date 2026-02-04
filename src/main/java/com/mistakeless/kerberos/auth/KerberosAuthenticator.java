package com.mistakeless.kerberos.auth;

import com.mistakeless.kerberos.config.KerberosClientConfig;
import java.io.File;
import java.time.Instant;
import java.util.Objects;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.client.KrbClient;
import org.apache.kerby.kerberos.kerb.client.KrbConfig;
import org.apache.kerby.kerberos.kerb.client.KrbConfigKey;
import org.apache.kerby.kerberos.kerb.type.ticket.TgtTicket;

public final class KerberosAuthenticator {
    private final KerberosClientConfig clientConfig;

    public KerberosAuthenticator(KerberosClientConfig clientConfig) {
        this.clientConfig = Objects.requireNonNull(clientConfig, "clientConfig");
    }

    public KerberosLoginResult authenticateWithPassword(String principal, String password) throws KrbException {
        Objects.requireNonNull(principal, "principal");
        Objects.requireNonNull(password, "password");
        KrbClient krbClient = buildClient();
        TgtTicket tgt = krbClient.requestTgt(principal, password);
        return new KerberosLoginResult(principal, tgt, Instant.now());
    }

    public KerberosLoginResult authenticateWithKeytab(String principal, File keytabFile) throws KrbException {
        Objects.requireNonNull(principal, "principal");
        Objects.requireNonNull(keytabFile, "keytabFile");
        KrbClient krbClient = buildClient();
        TgtTicket tgt = krbClient.requestTgt(principal, keytabFile);
        return new KerberosLoginResult(principal, tgt, Instant.now());
    }

    private KrbClient buildClient() throws KrbException {
        KrbConfig config = new KrbConfig();
        config.setString(KrbConfigKey.DEFAULT_REALM, clientConfig.getRealm());
        config.setString(KrbConfigKey.KDC_HOST, clientConfig.getKdcAddress().getHostString());
        config.setInt(KrbConfigKey.KDC_PORT, clientConfig.getKdcAddress().getPort());
        config.setBoolean(KrbConfigKey.KDC_ALLOW_TCP, clientConfig.isAllowTcp());
        config.setBoolean(KrbConfigKey.KDC_ALLOW_UDP, true);

        KrbClient krbClient = new KrbClient(config);
        krbClient.init();
        return krbClient;
    }
}
