#!/usr/bin/env bash
set -euo pipefail

: "${KRB_REALM:?Set KRB_REALM}"
: "${KRB_DOMAIN:?Set KRB_DOMAIN}"
: "${KRB_KDC_HOST:?Set KRB_KDC_HOST}"
: "${KRB_ADMIN_HOST:?Set KRB_ADMIN_HOST}"

if ! command -v envsubst >/dev/null 2>&1; then
  echo "envsubst is required (package: gettext-base)." >&2
  exit 1
fi

install -d /etc/krb5kdc

envsubst < "$(dirname "$0")/../config/krb5.conf" > /etc/krb5.conf
envsubst < "$(dirname "$0")/../config/kdc.conf" > /etc/krb5kdc/kdc.conf
envsubst < "$(dirname "$0")/../config/kadm5.acl" > /etc/krb5kdc/kadm5.acl

chmod 644 /etc/krb5.conf /etc/krb5kdc/kdc.conf /etc/krb5kdc/kadm5.acl

echo "Installed Kerberos configuration for realm ${KRB_REALM}."
