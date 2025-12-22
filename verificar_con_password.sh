#!/bin/bash

# SHA1 esperado por Google Play
EXPECTED_SHA1="97:33:B7:09:B1:B5:F4:F8:40:72:DB:F2:5E:C1:39:3D:57:71:6B:23"
EXPECTED_SHA1_CLEAN="9733B709B1B5F4F84072DBF25EC1393D57716B23"

PASSWORD="Bru1034Bri"

echo "=========================================="
echo "Verificación SHA1 con contraseña"
echo "=========================================="
echo ""
echo "SHA1 Esperado: $EXPECTED_SHA1"
echo ""

# Keystores a verificar
KEYSTORES=(
    "/home/gaston/StudioProjects/MiLupa1/milupa.jks"
    "/home/gaston/StudioProjects/MiLupa1/key.jks"
    "/home/gaston/StudioProjects/MiLupa/milupa_nuevo.jks"
)

# Lista de aliases comunes
ALIASES=(
    "milupa"
    "key0"
    "upload"
    "release"
    "key"
    "milupa_key"
)

MATCH_FOUND=false

for keystore in "${KEYSTORES[@]}"; do
    if [ ! -f "$keystore" ]; then
        echo "Keystore no encontrado: $keystore"
        continue
    fi
    
    echo "Verificando: $keystore"
    echo "----------------------------------------"
    
    for alias in "${ALIASES[@]}"; do
        # Intentar obtener el SHA1
        sha1_output=$(keytool -list -v -keystore "$keystore" -storepass "$PASSWORD" -alias "$alias" 2>/dev/null)
        
        if [ $? -eq 0 ]; then
            # Extraer SHA1
            sha1_line=$(echo "$sha1_output" | grep -i "SHA1:" | head -1)
            if [ -n "$sha1_line" ]; then
                sha1=$(echo "$sha1_line" | sed 's/.*SHA1: //' | tr -d ' ' | tr '[:lower:]' '[:upper:]')
                sha1_formatted=$(echo "$sha1" | sed 's/\(..\)/\1:/g' | sed 's/:$//')
                
                echo "  ✓ Alias encontrado: $alias"
                echo "    SHA1: $sha1_formatted"
                
                if [ "$sha1" = "$EXPECTED_SHA1_CLEAN" ]; then
                    echo ""
                    echo "=========================================="
                    echo "✓✓✓ KEYSTORE CORRECTO ENCONTRADO! ✓✓✓"
                    echo "=========================================="
                    echo "Archivo: $keystore"
                    echo "Alias: $alias"
                    echo "SHA1: $sha1_formatted"
                    echo ""
                    echo "Creando keystore.properties..."
                    
                    cat > /home/gaston/StudioProjects/MiLupa/keystore.properties << EOF
storeFile=$(realpath "$keystore")
storePassword=$PASSWORD
keyAlias=$alias
keyPassword=$PASSWORD
EOF
                    
                    echo "✓ keystore.properties creado exitosamente!"
                    echo ""
                    echo "Contenido del archivo:"
                    cat /home/gaston/StudioProjects/MiLupa/keystore.properties
                    echo ""
                    MATCH_FOUND=true
                    break 2
                else
                    echo "    (SHA1 no coincide)"
                    echo ""
                fi
            fi
        fi
    done
    
    # También intentar listar todos los aliases para ver cuáles existen
    echo "Alias disponibles en este keystore:"
    keytool -list -keystore "$keystore" -storepass "$PASSWORD" 2>/dev/null | grep "Alias name:" || echo "  (no se pudo listar)"
    echo ""
done

if [ "$MATCH_FOUND" = false ]; then
    echo "=========================================="
    echo "No se encontró el keystore correcto"
    echo "=========================================="
    echo ""
    echo "Se verificaron los keystores con la contraseña proporcionada"
    echo "pero ninguno tiene el SHA1 esperado."
    echo ""
    echo "Puede que:"
    echo "1. El keystore correcto esté en otra ubicación"
    echo "2. El alias sea diferente"
    echo "3. La contraseña del certificado sea diferente"
    echo ""
fi

