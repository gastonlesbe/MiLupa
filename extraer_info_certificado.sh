#!/bin/bash

CERT_FILE="/home/gaston/StudioProjects/MiLupa/upload_cert.der"

echo "=========================================="
echo "Información del Certificado de Google Play"
echo "=========================================="
echo ""

if [ ! -f "$CERT_FILE" ]; then
    echo "❌ No se encontró el archivo: $CERT_FILE"
    exit 1
fi

echo "Archivo: $CERT_FILE"
echo "Tamaño: $(ls -lh "$CERT_FILE" | awk '{print $5}')"
echo ""

echo "=========================================="
echo "SHA1 Fingerprint (lo que necesitas):"
echo "=========================================="
SHA1=$(openssl x509 -inform DER -in "$CERT_FILE" -noout -fingerprint -sha1 2>/dev/null | sed 's/.*=//' | tr '[:lower:]' '[:upper:]')
if [ -n "$SHA1" ]; then
    echo "$SHA1"
    echo ""
    echo "Este es el SHA1 que debe tener tu keystore."
else
    echo "No se pudo extraer el SHA1"
fi

echo ""
echo "=========================================="
echo "Información completa del certificado:"
echo "=========================================="
keytool -printcert -file "$CERT_FILE" 2>&1

echo ""
echo "=========================================="
echo "Información adicional (OpenSSL):"
echo "=========================================="
openssl x509 -inform DER -in "$CERT_FILE" -noout -subject -issuer -dates 2>&1

echo ""
echo "=========================================="
echo "SHA256 Fingerprint:"
echo "=========================================="
openssl x509 -inform DER -in "$CERT_FILE" -noout -fingerprint -sha256 2>&1 | sed 's/.*=//'

echo ""
echo "=========================================="
echo "Próximos pasos:"
echo "=========================================="
echo "1. Anota el SHA1 mostrado arriba"
echo "2. Crea un nuevo keystore con ese SHA1"
echo "3. Usa: ./crear_nuevo_keystore.sh"
echo "4. Verifica que el SHA1 coincida con el del certificado"
echo ""

