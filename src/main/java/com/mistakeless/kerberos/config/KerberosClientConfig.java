package com.mistakeless.kerberos.config;

import java.util.Objects;

public final class KerberosClientConfig {
    private final String realm;
    private final String kdcHost;
    private final int kdcPort;
    private final String domain;
    private final boolean allowTcp;

    private KerberosClientConfig(Builder builder) {
        this.realm = builder.realm;
        this.kdcHost = builder.kdcHost;
        this.kdcPort = builder.kdcPort;
        this.domain = builder.domain;
        this.allowTcp = builder.allowTcp;
    }

    public String getRealm() {
        return realm;
    }

    public String getKdcHost() {
        return kdcHost;
    }

    public int getKdcPort() {
        return kdcPort;
    }

    public String getDomain() {
        return domain;
    }

    public boolean isAllowTcp() {
        return allowTcp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String realm;
        private String kdcHost;
        private int kdcPort = 88;
        private String domain;
        private boolean allowTcp = true;

        private Builder() {
        }

        public Builder realm(String realm) {
            this.realm = Objects.requireNonNull(realm, "realm");
            return this;
        }

        public Builder kdcAddress(String host, int port) {
            this.kdcHost = Objects.requireNonNull(host, "host");
            this.kdcPort = port;
            return this;
        }

        public Builder domain(String domain) {
            this.domain = Objects.requireNonNull(domain, "domain");
            return this;
        }

        public Builder allowTcp(boolean allowTcp) {
            this.allowTcp = allowTcp;
            return this;
        }

        public KerberosClientConfig build() {
            Objects.requireNonNull(realm, "realm");
            Objects.requireNonNull(kdcHost, "kdcHost");
            return new KerberosClientConfig(this);
        }
    }
}
