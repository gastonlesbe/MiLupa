#!/bin/bash

# SHA1 esperado por Google Play
EXPECTED_SHA1="97:33:B7:09:B1:B5:F4:F8:40:72:DB:F2:5E:C1:39:3D:57:71:6B:23"
EXPECTED_SHA1_CLEAN="9733B709B1B5F4F84072DBF25EC1393D57716B23"

echo "=========================================="
echo "Verificación SHA1 - Keystores MiLupa"
echo "=========================================="
echo ""
echo "SHA1 Esperado por Google Play:"
echo "$EXPECTED_SHA1"
echo ""
echo "SHA1 Actual (incorrecto):"
echo "D2:EA:FD:99:BB:6B:03:F8:77:F2:FE:60:80:C3:A7:FD:11:8B:F9:40"
echo ""
echo "=========================================="
echo ""

# Keystores a verificar
KEYSTORES=(
    "/home/gaston/StudioProjects/MiLupa1/milupa.jks"
    "/home/gaston/StudioProjects/MiLupa1/key.jks"
    "/home/gaston/StudioProjects/MiLupa/milupa_nuevo.jks"
)

# Lista de contraseñas a probar
PASSWORDS=(
    "milupa123"
    "milupa"
    "android"
    "123456"
    "password"
    "MiLupa123"
    "MiLupa"
    "key0"
    "upload"
    "release"
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
        continue
    fi
    
    echo "Verificando: $keystore"
    echo "----------------------------------------"
    
    for password in "${PASSWORDS[@]}"; do
        for alias in "${ALIASES[@]}"; do
            # Intentar obtener el SHA1
            sha1_output=$(keytool -list -v -keystore "$keystore" -storepass "$password" -alias "$alias" 2>/dev/null)
            
            if [ $? -eq 0 ]; then
                # Extraer SHA1
                sha1_line=$(echo "$sha1_output" | grep -i "SHA1:" | head -1)
                if [ -n "$sha1_line" ]; then
                    sha1=$(echo "$sha1_line" | sed 's/.*SHA1: //' | tr -d ' ' | tr '[:lower:]' '[:upper:]')
                    sha1_formatted=$(echo "$sha1" | sed 's/\(..\)/\1:/g' | sed 's/:$//')
                    
                    echo "  ✓ Encontrado - Alias: $alias, Password: $password"
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
storePassword=$password
keyAlias=$alias
keyPassword=$password
EOF
                        
                        echo "✓ keystore.properties creado exitosamente!"
                        echo ""
                        MATCH_FOUND=true
                        break 3
                    fi
                    echo ""
                fi
            fi
        done
    done
    echo ""
done

if [ "$MATCH_FOUND" = false ]; then
    echo "=========================================="
    echo "No se encontró el keystore correcto"
    echo "=========================================="
    echo ""
    echo "El keystore con el SHA1 esperado no se encontró con las"
    echo "contraseñas comunes probadas."
    echo ""
    echo "Para verificar manualmente un keystore:"
    echo "  keytool -list -v -keystore <ruta> -alias <alias>"
    echo ""
    echo "Si conoces la contraseña del keystore correcto, puedes:"
    echo "1. Verificar su SHA1 manualmente"
    echo "2. Crear keystore.properties con la información correcta"
    echo ""
fi

