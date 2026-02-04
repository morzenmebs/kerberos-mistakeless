package com.mistakeless.kerberos.auth;

import java.time.Instant;
import java.util.Objects;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosTicket;

public final class KerberosLoginResult {
    private final String clientPrincipal;
    private final KerberosTicket tgt;
    private final Subject subject;
    private final Instant authenticatedAt;

    public KerberosLoginResult(String clientPrincipal, KerberosTicket tgt, Subject subject, Instant authenticatedAt) {
        this.clientPrincipal = Objects.requireNonNull(clientPrincipal, "clientPrincipal");
        this.tgt = Objects.requireNonNull(tgt, "tgt");
        this.subject = Objects.requireNonNull(subject, "subject");
        this.authenticatedAt = Objects.requireNonNull(authenticatedAt, "authenticatedAt");
    }

    public String getClientPrincipal() {
        return clientPrincipal;
    }

    public KerberosTicket getTgt() {
        return tgt;
    }

    public Subject getSubject() {
        return subject;
    }

    public Instant getAuthenticatedAt() {
        return authenticatedAt;
    }

    @Override
    public String toString() {
        return "KerberosLoginResult{" +
            "clientPrincipal='" + clientPrincipal + '\'' +
            ", authenticatedAt=" + authenticatedAt +
            ", realm='" + tgt.getClient().getRealm() + '\'' +
            '}';
    }
}
