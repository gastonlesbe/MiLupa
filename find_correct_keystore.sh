#!/bin/bash

# SHA1 esperado por Google Play para MiLupa
EXPECTED_SHA1="8C:55:CD:27:16:89:23:DF:8A:EF:60:0A:1F:75:63:C1:3E:68:FE:76"
EXPECTED_SHA1_CLEAN=$(echo "$EXPECTED_SHA1" | tr -d ':')

echo "=========================================="
echo "Buscando keystore correcto para MiLupa"
echo "=========================================="
echo ""
echo "SHA1 esperado por Google Play:"
echo "$EXPECTED_SHA1"
echo ""

# Buscar keystores en MiLupa1
KEYSTORE_DIR="/home/gaston/StudioProjects/MiLupa1"
KEYSTORES=$(find "$KEYSTORE_DIR" -maxdepth 1 -name "*.jks" 2>/dev/null)

if [ -z "$KEYSTORES" ]; then
    echo "❌ No se encontraron archivos .jks en $KEYSTORE_DIR"
    exit 1
fi

echo "Keystores encontrados:"
echo "$KEYSTORES" | while read keystore; do
    echo "  - $(basename "$keystore")"
done
echo ""

for keystore in $KEYSTORES; do
    keystore_name=$(basename "$keystore")
    echo "=========================================="
    echo "Verificando: $keystore_name"
    echo "=========================================="
    echo ""
    
    # Intentar sin contraseña primero
    SHA1_INFO=$(keytool -list -v -keystore "$keystore" -storepass "" 2>&1)
    
    if echo "$SHA1_INFO" | grep -q "keystore password was incorrect"; then
        echo "⚠️  Este keystore requiere contraseña"
        echo "   Ejecuta manualmente:"
        echo "   keytool -list -v -keystore \"$keystore\" -storepass TU_PASSWORD"
        echo ""
        continue
    fi
    
    # Extraer SHA1
    SHA1=$(echo "$SHA1_INFO" | grep -i "SHA1:" | head -1 | sed 's/.*SHA1: *//' | tr -d ' ' | tr -d ':')
    
    if [ -n "$SHA1" ]; then
        SHA1_FORMATTED=$(echo "$SHA1" | sed 's/\(..\)/\1:/g' | sed 's/:$//')
        echo "SHA1 encontrado: $SHA1_FORMATTED"
        
        if [ "$SHA1" = "$EXPECTED_SHA1_CLEAN" ]; then
            echo ""
            echo "✅ ✅ ✅ ¡COINCIDE! Este es el keystore correcto ✅ ✅ ✅"
            echo ""
            echo "Keystore: $keystore_name"
            echo "Ubicación: $keystore"
            echo ""
            echo "Copia este keystore a MiLupa:"
            echo "cp \"$keystore\" /home/gaston/StudioProjects/MiLupa/"
            echo ""
        else
            echo "❌ No coincide"
        fi
    else
        echo "⚠️  No se pudo extraer SHA1 (puede requerir contraseña)"
    fi
    echo ""
done

echo "=========================================="
echo "Búsqueda completada"
echo "=========================================="

