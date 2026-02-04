#!/usr/bin/env bash
set -euo pipefail

: "${KRB_REALM:?Set KRB_REALM}"

if [ -f /var/lib/krb5kdc/principal ]; then
  echo "KDC database already exists at /var/lib/krb5kdc/principal." >&2
  echo "Remove it if you intend to recreate the realm." >&2
  exit 1
fi

kdb5_util create -s -r "${KRB_REALM}"

if ! kadmin.local -q "get_principal admin/admin" >/dev/null 2>&1; then
  kadmin.local -q "add_principal -requires_preauth admin/admin"
fi

echo "KDC initialized for realm ${KRB_REALM}."
