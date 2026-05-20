#!/bin/bash
# Axiom AI Coach - Release Keystore Setup Script
# Run this once to generate your release signing key

set -euo pipefail

KEYSTORE_NAME="axiom-release.jks"
KEY_ALIAS="axiom"

echo "============================================"
echo "  Axiom AI Coach — Release Keystore Setup"
echo "============================================"
echo ""

if [ -f "$KEYSTORE_NAME" ]; then
  echo "Keystore already exists: $KEYSTORE_NAME"
  echo "Delete it manually if you need to regenerate."
  exit 0
fi

echo "Generating release keystore..."
echo "You will be prompted for keystore and key passwords."
echo ""

keytool -genkey -v \
  -keystore "$KEYSTORE_NAME" \
  -alias "$KEY_ALIAS" \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -dname "CN=Axiom AI Coach, OU=Mobile, O=Axiom Inc, L=San Francisco, ST=CA, C=US"

echo ""
echo "✅ Keystore created: $KEYSTORE_NAME"
echo ""
echo "📋 Next steps:"
echo "  1. Add to .gitignore (NEVER commit the keystore file)"
echo "  2. Back up the keystore and passwords securely"
echo "  3. For CI, encode and add to GitHub Secrets:"
echo ""
echo "     base64 -w 0 $KEYSTORE_NAME | pbcopy"
echo "     # Paste as RELEASE_KEYSTORE secret in GitHub"
echo ""
echo "  4. Add to local.properties (for local builds):"
echo "     KEYSTORE_PATH=$KEYSTORE_NAME"
echo "     KEYSTORE_PASSWORD=<your-password>"
echo "     KEY_ALIAS=$KEY_ALIAS"
echo "     KEY_PASSWORD=<your-password>"
