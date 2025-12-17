#!/bin/bash

echo "=========================================="
echo "Crear Nuevo Keystore y Certificado PEM"
echo "=========================================="
echo ""
echo "Este script creará un NUEVO keystore y exportará"
echo "el certificado en formato PEM para subir a Google Play."
echo ""

read -p "Nombre del nuevo keystore (ej: milupa_nuevo.jks): " KEYSTORE_NAME
if [ -z "$KEYSTORE_NAME" ]; then
    KEYSTORE_NAME="milupa_nuevo.jks"
fi

read -p "Alias de la clave (ej: upload): " KEY_ALIAS
if [ -z "$KEY_ALIAS" ]; then
    KEY_ALIAS="upload"
fi
read -sp "Contraseña del keystore: " STORE_PASS
echo ""
read -sp "Confirma la contraseña: " STORE_PASS_CONF
echo ""

if [ "$STORE_PASS" != "$STORE_PASS_CONF" ]; then
    echo ""
    echo "❌ Las contraseñas no coinciden"
    exit 1
fi

echo ""
echo "Información para el certificado:"
read -p "Nombre y apellidos: " CN
if [ -z "$CN" ]; then
    echo "❌ El nombre es requerido"
    exit 1
fi

read -p "Unidad organizativa (opcional): " OU
read -p "Organización (opcional): " O
read -p "Ciudad (opcional): " L
read -p "Estado/Provincia (opcional): " ST
read -p "Código de país (2 letras, ej: AR): " C
if [ -z "$C" ]; then
    C="AR"
fi

echo ""
echo "=========================================="
echo "Creando nuevo keystore..."
echo "=========================================="
echo "Keystore: $KEYSTORE_NAME"
echo "Alias: $KEY_ALIAS"
echo ""

# Construir el comando DN
DN="CN=$CN"
if [ -n "$OU" ]; then
    DN="$DN, OU=$OU"
fi
if [ -n "$O" ]; then
    DN="$DN, O=$O"
fi
if [ -n "$L" ]; then
    DN="$DN, L=$L"
fi
if [ -n "$ST" ]; then
    DN="$DN, ST=$ST"
fi
if [ -n "$C" ]; then
    DN="$DN, C=$C"
fi

# Crear el keystore
keytool -genkey -v -keystore "$KEYSTORE_NAME" \
    -keyalg RSA -keysize 2048 -validity 10000 \
    -alias "$KEY_ALIAS" \
    -storepass "$STORE_PASS" \
    -keypass "$STORE_PASS" \
    -dname "$DN" 2>&1

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ Error al crear el keystore"
    exit 1
fi

echo ""
echo "✅ Keystore creado exitosamente: $KEYSTORE_NAME"
echo ""

# Verificar SHA1
echo "=========================================="
echo "Información del nuevo keystore:"
echo "=========================================="
SHA1=$(keytool -list -v -keystore "$KEYSTORE_NAME" -alias "$KEY_ALIAS" -storepass "$STORE_PASS" 2>&1 | grep -i "SHA1:" | head -1 | sed 's/.*SHA1: *//' | tr -d ' ')
SHA1_FORMATTED=$(echo "$SHA1" | sed 's/\(..\)/\1:/g' | sed 's/:$//')

echo "SHA1: $SHA1_FORMATTED"
echo ""

# Exportar certificado a PEM
echo "=========================================="
echo "Exportando certificado a formato PEM..."
echo "=========================================="

CERT_FILE="upload_certificate.pem"

keytool -export -rfc -keystore "$KEYSTORE_NAME" \
    -alias "$KEY_ALIAS" \
    -file "$CERT_FILE" \
    -storepass "$STORE_PASS" 2>&1

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Certificado exportado exitosamente: $CERT_FILE"
    echo ""
    
    # Verificar el certificado PEM
    echo "=========================================="
    echo "Verificando certificado PEM:"
    echo "=========================================="
    openssl x509 -in "$CERT_FILE" -noout -subject -issuer -dates 2>&1
    echo ""
    echo "SHA1 del certificado PEM:"
    openssl x509 -in "$CERT_FILE" -noout -fingerprint -sha1 2>&1 | sed 's/.*=//'
    echo ""
    
    # Verificar que los SHA1 coinciden
    SHA1_PEM=$(openssl x509 -in "$CERT_FILE" -noout -fingerprint -sha1 2>&1 | sed 's/.*=//' | tr -d ':' | tr '[:lower:]' '[:upper:]')
    SHA1_KEYSTORE_CLEAN=$(echo "$SHA1" | tr -d ':' | tr '[:lower:]' '[:upper:]')
    
    if [ "$SHA1_PEM" = "$SHA1_KEYSTORE_CLEAN" ]; then
        echo "✅ SHA1 del keystore y del certificado PEM coinciden"
    else
        echo "⚠️  Los SHA1 no coinciden (esto es extraño)"
    fi
    
    echo ""
    echo "=========================================="
    echo "✅ Proceso completado exitosamente"
    echo "=========================================="
    echo ""
    echo "Archivos creados:"
    echo "  1. Keystore: $KEYSTORE_NAME"
    echo "  2. Certificado PEM: $CERT_FILE"
    echo ""
    echo "=========================================="
    echo "Configuración para keystore.properties:"
    echo "=========================================="
    echo "storeFile=$KEYSTORE_NAME"
    echo "storePassword=$STORE_PASS"
    echo "keyAlias=$KEY_ALIAS"
    echo "keyPassword=$STORE_PASS"
    echo ""
    echo "=========================================="
    echo "Próximos pasos:"
    echo "=========================================="
    echo "1. Sube el archivo '$CERT_FILE' a Google Play Console"
    echo "2. Crea 'keystore.properties' con la configuración de arriba"
    echo "3. Construye el AAB: ./gradlew bundleRelease"
    echo ""
    echo "⚠️  IMPORTANTE: Guarda el keystore y las contraseñas"
    echo "    en un lugar seguro. Sin ellos no podrás actualizar la app."
    echo ""
else
    echo ""
    echo "❌ Error al exportar el certificado"
    exit 1
fi

