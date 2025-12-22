#!/bin/bash

# SHA1 esperado por Google Play
EXPECTED_SHA1="97:33:B7:09:B1:B5:F4:F8:40:72:DB:F2:5E:C1:39:3D:57:71:6B:23"
EXPECTED_SHA1_CLEAN="9733B709B1B5F4F84072DBF25EC1393D57716B23"

# SHA1 actual (incorrecto)
CURRENT_SHA1="D2:EA:FD:99:BB:6B:03:F8:77:F2:FE:60:80:C3:A7:FD:11:8B:F9:40"

echo "=========================================="
echo "Verificación de Firma SHA1 para MiLupa"
echo "=========================================="
echo ""
echo "SHA1 Esperado por Google Play:"
echo "$EXPECTED_SHA1"
echo ""
echo "SHA1 Actual (incorrecto):"
echo "$CURRENT_SHA1"
echo ""
echo "=========================================="
echo "Buscando keystores con el SHA1 correcto..."
echo "=========================================="
echo ""

# Directorios comunes donde pueden estar los keystores
SEARCH_DIRS=(
    "/home/gaston/StudioProjects"
    "/home/gaston"
    "/home/gaston/Android"
    "/home/gaston/Documents"
)

# Buscar todos los archivos .jks y .keystore
FOUND_KEYSTORES=()

for dir in "${SEARCH_DIRS[@]}"; do
    if [ -d "$dir" ]; then
        echo "Buscando en: $dir"
        while IFS= read -r -d '' file; do
            FOUND_KEYSTORES+=("$file")
        done < <(find "$dir" -type f \( -name "*.jks" -o -name "*.keystore" \) -print0 2>/dev/null)
    fi
done

# También buscar en el directorio actual
while IFS= read -r -d '' file; do
    FOUND_KEYSTORES+=("$file")
done < <(find /home/gaston/StudioProjects/MiLupa -type f \( -name "*.jks" -o -name "*.keystore" \) -print0 2>/dev/null)

echo ""
echo "Encontrados ${#FOUND_KEYSTORES[@]} keystores"
echo ""

# Función para verificar SHA1 de un keystore
check_keystore_sha1() {
    local keystore_file="$1"
    local password="$2"
    local alias="$3"
    
    if [ -z "$password" ]; then
        return 1
    fi
    
    # Intentar obtener el SHA1
    local sha1_output=$(keytool -list -v -keystore "$keystore_file" -storepass "$password" -alias "$alias" 2>/dev/null | grep -i "SHA1:" | head -1)
    
    if [ -n "$sha1_output" ]; then
        local sha1=$(echo "$sha1_output" | sed 's/.*SHA1: //' | tr -d ' ' | tr '[:lower:]' '[:upper:]')
        if [ "$sha1" = "$EXPECTED_SHA1_CLEAN" ]; then
            return 0
        fi
    fi
    
    return 1
}

# Verificar cada keystore encontrado
MATCH_FOUND=false

for keystore in "${FOUND_KEYSTORES[@]}"; do
    echo "Verificando: $keystore"
    
    # Intentar con contraseñas comunes
    PASSWORDS=(
        "milupa123"
        "milupa"
        "android"
        "123456"
        "password"
        "MiLupa123"
        "MiLupa"
    )
    
    # También intentar sin contraseña
    PASSWORDS+=("")
    
    for password in "${PASSWORDS[@]}"; do
        # Intentar con alias común
        for alias in "milupa" "key0" "upload" "release" "key"; do
            if check_keystore_sha1 "$keystore" "$password" "$alias"; then
                echo ""
                echo "=========================================="
                echo "✓ KEYSTORE CORRECTO ENCONTRADO!"
                echo "=========================================="
                echo "Archivo: $keystore"
                echo "Alias: $alias"
                echo "SHA1: $EXPECTED_SHA1"
                echo ""
                echo "Para usar este keystore, crea/actualiza keystore.properties:"
                echo "storeFile=$(realpath "$keystore")"
                echo "storePassword=$password"
                echo "keyAlias=$alias"
                echo "keyPassword=$password"
                echo ""
                MATCH_FOUND=true
                break 2
            fi
        done
    done
done

if [ "$MATCH_FOUND" = false ]; then
    echo ""
    echo "=========================================="
    echo "⚠ No se encontró un keystore con el SHA1 correcto"
    echo "=========================================="
    echo ""
    echo "Opciones:"
    echo "1. El keystore correcto puede estar en otra ubicación"
    echo "2. El keystore puede tener una contraseña diferente"
    echo "3. Puede que necesites usar el keystore original de Google Play"
    echo ""
    echo "Para verificar manualmente un keystore:"
    echo "  keytool -list -v -keystore <ruta_keystore> -alias <alias>"
    echo ""
fi

echo ""
echo "=========================================="
echo "Verificando keystore actual (milupa_nuevo.jks)"
echo "=========================================="
if [ -f "/home/gaston/StudioProjects/MiLupa/milupa_nuevo.jks" ]; then
    echo "El keystore 'milupa_nuevo.jks' existe"
    echo "Para verificar su SHA1, ejecuta:"
    echo "  keytool -list -v -keystore milupa_nuevo.jks -alias <alias>"
else
    echo "El keystore 'milupa_nuevo.jks' no existe"
fi

