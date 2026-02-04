package com.mistakeless.kerberos.config;

import java.net.InetSocketAddress;
import java.util.Objects;

public final class KerberosClientConfig {
    private final String realm;
    private final InetSocketAddress kdcAddress;
    private final boolean allowTcp;

    private KerberosClientConfig(Builder builder) {
        this.realm = builder.realm;
        this.kdcAddress = builder.kdcAddress;
        this.allowTcp = builder.allowTcp;
    }

    public String getRealm() {
        return realm;
    }

    public InetSocketAddress getKdcAddress() {
        return kdcAddress;
    }

    public boolean isAllowTcp() {
        return allowTcp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String realm;
        private InetSocketAddress kdcAddress;
        private boolean allowTcp = true;

        private Builder() {
        }

        public Builder realm(String realm) {
            this.realm = Objects.requireNonNull(realm, "realm");
            return this;
        }

        public Builder kdcAddress(String host, int port) {
            this.kdcAddress = new InetSocketAddress(Objects.requireNonNull(host, "host"), port);
            return this;
        }

        public Builder allowTcp(boolean allowTcp) {
            this.allowTcp = allowTcp;
            return this;
        }

        public KerberosClientConfig build() {
            Objects.requireNonNull(realm, "realm");
            Objects.requireNonNull(kdcAddress, "kdcAddress");
            return new KerberosClientConfig(this);
        }
    }
}
