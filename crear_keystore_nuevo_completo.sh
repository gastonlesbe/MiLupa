#!/bin/bash

echo "=========================================="
echo "Crear NUEVO Keystore para MiLupa"
echo "=========================================="
echo ""
echo "Google Play requiere un certificado NUEVO"
echo "que sea diferente a los anteriores."
echo ""
echo "Este script creará un keystore completamente nuevo."
echo ""

read -p "¿Continuar? (s/n): " continuar
if [ "$continuar" != "s" ] && [ "$continuar" != "S" ]; then
    echo "Operación cancelada"
    exit 0
fi

echo ""
echo "Ingresa la información para crear el NUEVO keystore:"
echo ""

read -p "Nombre del keystore (ej: milupa_nuevo.jks): " KEYSTORE_NAME
if [ -z "$KEYSTORE_NAME" ]; then
    KEYSTORE_NAME="milupa_nuevo.jks"
fi

read -p "Alias de la clave (ej: upload): " KEY_ALIAS
if [ -z "$KEY_ALIAS" ]; then
    KEY_ALIAS="upload"
fi

read -sp "Contraseña del keystore (guárdala bien!): " STORE_PASS
echo ""
read -sp "Confirma la contraseña: " STORE_PASS_CONF
echo ""

if [ "$STORE_PASS" != "$STORE_PASS_CONF" ]; then
    echo ""
    echo "❌ Las contraseñas no coinciden"
    exit 1
fi

echo ""
echo "Información del certificado (para distinguibilidad):"
read -p "Nombre y apellidos: " CN
read -p "Unidad organizativa (opcional): " OU
read -p "Organización (opcional): " O
read -p "Ciudad (opcional): " L
read -p "Estado/Provincia (opcional): " ST
read -p "Código de país (2 letras, ej: AR): " C

echo ""
echo "Creando NUEVO keystore..."

# Construir el Distinguished Name
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

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Keystore creado exitosamente: $KEYSTORE_NAME"
    echo ""
    
    # Verificar SHA1
    echo "Verificando SHA1 del NUEVO keystore..."
    SHA1_INFO=$(keytool -list -v -keystore "$KEYSTORE_NAME" -alias "$KEY_ALIAS" -storepass "$STORE_PASS" 2>&1)
    SHA1=$(echo "$SHA1_INFO" | grep -i "SHA1:" | head -1 | sed 's/.*SHA1: *//' | tr -d ' ')
    SHA1_FORMATTED=$(echo "$SHA1" | sed 's/\(..\)/\1:/g' | sed 's/:$//')
    
    echo ""
    echo "SHA1 del NUEVO keystore:"
    echo "$SHA1_FORMATTED"
    echo ""
    echo "⚠️  IMPORTANTE: Este SHA1 debe ser DIFERENTE a los anteriores"
    echo ""
    
    # Exportar certificado a PEM
    echo "Exportando certificado a formato PEM..."
    keytool -export -rfc -keystore "$KEYSTORE_NAME" -alias "$KEY_ALIAS" -file upload_certificate.pem -storepass "$STORE_PASS" 2>&1
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "✅ Certificado exportado: upload_certificate.pem"
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
        echo "1. Crea el archivo keystore.properties con la configuración de arriba"
        echo "2. Sube upload_certificate.pem a Google Play Console"
        echo "3. Construye el AAB: ./gradlew bundleRelease"
        echo ""
        echo "⚠️  GUARDA ESTA INFORMACIÓN DE FORMA SEGURA:"
        echo "   - Keystore: $KEYSTORE_NAME"
        echo "   - Contraseña: [la que ingresaste]"
        echo "   - Alias: $KEY_ALIAS"
        echo ""
    else
        echo ""
        echo "❌ Error al exportar el certificado"
        echo "Puedes exportarlo manualmente con:"
        echo "keytool -export -rfc -keystore $KEYSTORE_NAME -alias $KEY_ALIAS -file upload_certificate.pem -storepass [TU_PASSWORD]"
    fi
else
    echo ""
    echo "❌ Error al crear el keystore"
    exit 1
fi

