#!/bin/bash

echo "=========================================="
echo "Exportar Certificado a formato PEM"
echo "=========================================="
echo ""

# Opción 1: Convertir upload_cert.der a PEM (más rápido)
if [ -f "upload_cert.der" ]; then
    echo "✓ Encontrado: upload_cert.der"
    echo ""
    echo "Convirtiendo DER a PEM..."
    openssl x509 -inform DER -in upload_cert.der -out upload_certificate.pem 2>&1
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "✅ Certificado convertido exitosamente: upload_certificate.pem"
        echo ""
        echo "Mostrando información del certificado:"
        echo "=========================================="
        openssl x509 -in upload_certificate.pem -noout -subject -issuer -dates 2>&1
        echo ""
        echo "SHA1 Fingerprint:"
        SHA1=$(openssl x509 -in upload_certificate.pem -noout -fingerprint -sha1 2>&1 | sed 's/.*=//' | tr '[:lower:]' '[:upper:]')
        echo "$SHA1"
        echo ""
        echo "=========================================="
        echo "✅ Archivo listo: upload_certificate.pem"
        echo "=========================================="
        echo "Puedes subir este archivo a Google Play Console"
        exit 0
    else
        echo "❌ Error al convertir el certificado"
    fi
fi

# Opción 2: Exportar desde keystore
echo ""
echo "¿Quieres exportar desde un keystore? (s/n)"
read -r respuesta

if [ "$respuesta" != "s" ] && [ "$respuesta" != "S" ]; then
    echo "Operación cancelada"
    exit 0
fi

echo ""
echo "Keystores disponibles en MiLupa1:"
ls -1 /home/gaston/StudioProjects/MiLupa1/*.jks 2>/dev/null | xargs -n1 basename

echo ""
read -p "Nombre del keystore (ej: milupa.jks o key.jks): " KEYSTORE_NAME
read -p "Alias de la clave (ej: upload): " KEY_ALIAS
read -sp "Contraseña del keystore: " STORE_PASS
echo ""

KEYSTORE_PATH="/home/gaston/StudioProjects/MiLupa1/$KEYSTORE_NAME"

if [ ! -f "$KEYSTORE_PATH" ]; then
    echo "❌ No se encontró el keystore: $KEYSTORE_PATH"
    exit 1
fi

echo ""
echo "Exportando certificado desde keystore..."
keytool -export -rfc -keystore "$KEYSTORE_PATH" -alias "$KEY_ALIAS" -file upload_certificate.pem -storepass "$STORE_PASS" 2>&1

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Certificado exportado exitosamente: upload_certificate.pem"
    echo ""
    echo "Mostrando información del certificado:"
    echo "=========================================="
    openssl x509 -in upload_certificate.pem -noout -subject -issuer -dates 2>&1
    echo ""
    echo "SHA1 Fingerprint:"
    SHA1=$(openssl x509 -in upload_certificate.pem -noout -fingerprint -sha1 2>&1 | sed 's/.*=//' | tr '[:lower:]' '[:upper:]')
    echo "$SHA1"
    echo ""
    echo "=========================================="
    echo "✅ Archivo listo: upload_certificate.pem"
    echo "=========================================="
    echo "Puedes subir este archivo a Google Play Console"
else
    echo ""
    echo "❌ Error al exportar el certificado"
    echo "Verifica que el alias y la contraseña sean correctos"
    exit 1
fi
