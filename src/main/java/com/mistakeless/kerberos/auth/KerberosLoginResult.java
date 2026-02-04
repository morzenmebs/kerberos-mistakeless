package com.mistakeless.kerberos.auth;

import java.time.Instant;
import java.util.Objects;
import org.apache.kerby.kerberos.kerb.type.ticket.TgtTicket;

public final class KerberosLoginResult {
    private final String clientPrincipal;
    private final TgtTicket tgt;
    private final Instant authenticatedAt;

    public KerberosLoginResult(String clientPrincipal, TgtTicket tgt, Instant authenticatedAt) {
        this.clientPrincipal = Objects.requireNonNull(clientPrincipal, "clientPrincipal");
        this.tgt = Objects.requireNonNull(tgt, "tgt");
        this.authenticatedAt = Objects.requireNonNull(authenticatedAt, "authenticatedAt");
    }

    public String getClientPrincipal() {
        return clientPrincipal;
    }

    public TgtTicket getTgt() {
        return tgt;
    }

    public Instant getAuthenticatedAt() {
        return authenticatedAt;
    }

    @Override
    public String toString() {
        return "KerberosLoginResult{" +
            "clientPrincipal='" + clientPrincipal + '\'' +
            ", authenticatedAt=" + authenticatedAt +
            ", realm='" + tgt.getRealm() + '\'' +
            '}';
    }
}
