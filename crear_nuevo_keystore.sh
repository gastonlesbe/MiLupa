#!/bin/bash

echo "=========================================="
echo "Crear Nuevo Keystore para MiLupa"
echo "=========================================="
echo ""
echo "Este script te ayudará a crear un nuevo keystore"
echo "para firmar el AAB de MiLupa."
echo ""
echo "IMPORTANTE: Necesitas tener la información de la"
echo "nueva clave de firma que Google te proporcionó."
echo ""

read -p "¿Tienes la información de la nueva clave de Google? (s/n): " tiene_info

if [ "$tiene_info" != "s" ] && [ "$tiene_info" != "S" ]; then
    echo ""
    echo "Primero solicita el reset de la clave de firma en Google Play Console."
    echo "Luego ejecuta este script nuevamente."
    exit 1
fi

echo ""
echo "Ingresa la información para crear el keystore:"
echo ""

read -p "Nombre del keystore (ej: milupa_nuevo.jks): " KEYSTORE_NAME
read -p "Alias de la clave (ej: upload): " KEY_ALIAS
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
read -p "Nombre y apellidos: " CN
read -p "Unidad organizativa (opcional): " OU
read -p "Organización (opcional): " O
read -p "Ciudad (opcional): " L
read -p "Estado/Provincia (opcional): " ST
read -p "Código de país (2 letras, ej: AR): " C

echo ""
echo "Creando keystore..."

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

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Keystore creado exitosamente: $KEYSTORE_NAME"
    echo ""
    
    # Verificar SHA1
    echo "Verificando SHA1 del keystore..."
    SHA1=$(keytool -list -v -keystore "$KEYSTORE_NAME" -alias "$KEY_ALIAS" -storepass "$STORE_PASS" 2>&1 | grep -i "SHA1:" | head -1 | sed 's/.*SHA1: *//' | tr -d ' ')
    SHA1_FORMATTED=$(echo "$SHA1" | sed 's/\(..\)/\1:/g' | sed 's/:$//')
    
    echo ""
    echo "SHA1 del nuevo keystore:"
    echo "$SHA1_FORMATTED"
    echo ""
    echo "⚠️  IMPORTANTE: Verifica que este SHA1 coincida con el que"
    echo "    Google te proporcionó en la nueva clave de firma."
    echo ""
    echo "=========================================="
    echo "Configuración para keystore.properties:"
    echo "=========================================="
    echo "storeFile=$KEYSTORE_NAME"
    echo "storePassword=$STORE_PASS"
    echo "keyAlias=$KEY_ALIAS"
    echo "keyPassword=$STORE_PASS"
    echo ""
    echo "Guarda esta información de forma segura!"
    echo ""
else
    echo ""
    echo "❌ Error al crear el keystore"
    exit 1
fi

