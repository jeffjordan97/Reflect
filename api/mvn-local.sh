#!/usr/bin/env bash
# Wrapper to bypass the Dizplai Maven proxy (expired TLS cert on maven.never.no).
# Uses an empty settings file so dependencies resolve directly from Maven Central.
#
# Usage:  ./mvn-local.sh spring-boot:run -Dspring-boot.run.profiles=dev
#         ./mvn-local.sh test
#         ./mvn-local.sh verify

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SETTINGS="$SCRIPT_DIR/.mvn-local-settings.xml"

# Create empty settings if it doesn't exist
if [ ! -f "$SETTINGS" ]; then
  cat > "$SETTINGS" << 'XML'
<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0"/>
XML
fi

exec mvn -s "$SETTINGS" "$@"
