# Kerberos Mistakeless

This repository provides a minimal, repeatable setup for a local MIT Kerberos realm.
It includes configuration templates and a small set of scripts to initialize a KDC
and validate a client login.

## Prerequisites

The commands below assume a Debian/Ubuntu host with systemd.

```bash
sudo apt-get update
sudo apt-get install -y krb5-kdc krb5-admin-server krb5-user
```

## Quickstart (single host KDC + client)

1. **Set your realm details** (replace values as needed):

   ```bash
   export KRB_REALM=EXAMPLE.COM
   export KRB_DOMAIN=example.com
   export KRB_KDC_HOST=kerberos.example.com
   export KRB_ADMIN_HOST=kerberos.example.com
   ```

2. **Install the configuration templates**:

   ```bash
   sudo ./scripts/install-configs.sh
   ```

3. **Initialize the KDC database and admin principal**:

   ```bash
   sudo ./scripts/init-kdc.sh
   ```

4. **Start the KDC services**:

   ```bash
   sudo systemctl restart krb5-kdc krb5-admin-server
   ```

5. **Verify the realm**:

   ```bash
   kinit admin/admin
   klist
   ```

## Notes

- The scripts do not overwrite existing databases unless you explicitly remove them.
- If you are setting up multiple hosts, update `KRB_KDC_HOST` and
  `KRB_ADMIN_HOST` to point to the KDC machine and deploy only `krb5.conf`
  on clients.
- For production, replace the example realm, hostnames, and ACLs with your
  environment-specific values.

## Files

- `config/krb5.conf`: Client + server realm configuration.
- `config/kdc.conf`: KDC configuration for the realm.
- `config/kadm5.acl`: Admin ACLs for `kadmind`.
- `scripts/install-configs.sh`: Installs configs to `/etc` locations.
- `scripts/init-kdc.sh`: Initializes the KDC database and admin principal.
