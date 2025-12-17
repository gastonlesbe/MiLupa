#!/bin/bash

# SHA1 esperado por Google Play para MiLupa
EXPECTED_SHA1="8C:55:CD:27:16:89:23:DF:8A:EF:60:0A:1F:75:63:C1:3E:68:FE:76"
EXPECTED_SHA1_CLEAN=$(echo "$EXPECTED_SHA1" | tr -d ':')

echo "=========================================="
echo "Probando contraseñas para encontrar keystore correcto"
echo "=========================================="
echo ""
echo "SHA1 esperado por Google Play:"
echo "$EXPECTED_SHA1"
echo ""

# Contraseñas a probar
PASSWORDS=("octopus2317522" "goldfish210809" "gsl2317522")

# Keystores a verificar
KEYSTORE_DIR="/home/gaston/StudioProjects/MiLupa1"
KEYSTORES=("milupa.jks" "key.jks")

FOUND=false

for keystore_name in "${KEYSTORES[@]}"; do
    keystore="$KEYSTORE_DIR/$keystore_name"
    
    if [ ! -f "$keystore" ]; then
        echo "⚠️  $keystore_name no encontrado, saltando..."
        echo ""
        continue
    fi
    
    echo "=========================================="
    echo "Probando keystore: $keystore_name"
    echo "=========================================="
    echo ""
    
    for password in "${PASSWORDS[@]}"; do
        echo "Probando contraseña: $password"
        
        # Intentar listar los aliases
        ALIASES=$(keytool -list -keystore "$keystore" -storepass "$password" 2>&1 | grep "Alias name:" | awk '{print $3}')
        
        if [ -z "$ALIASES" ]; then
            echo "  ❌ Contraseña incorrecta"
            continue
        fi
        
        echo "  ✓ Contraseña correcta!"
        echo "  Aliases encontrados:"
        echo "$ALIASES" | while read alias; do
            echo "    - $alias"
        done
        echo ""
        
        # Verificar SHA1 de cada alias
        echo "$ALIASES" | while read alias; do
            SHA1_INFO=$(keytool -list -v -keystore "$keystore" -storepass "$password" -alias "$alias" 2>&1)
            SHA1=$(echo "$SHA1_INFO" | grep -i "SHA1:" | head -1 | sed 's/.*SHA1: *//' | tr -d ' ' | tr -d ':')
            
            if [ -n "$SHA1" ]; then
                SHA1_FORMATTED=$(echo "$SHA1" | sed 's/\(..\)/\1:/g' | sed 's/:$//')
                echo "  Alias: $alias"
                echo "  SHA1:  $SHA1_FORMATTED"
                
                if [ "$SHA1" = "$EXPECTED_SHA1_CLEAN" ]; then
                    echo ""
                    echo "  ✅ ✅ ✅ ¡ENCONTRADO! Este es el keystore correcto ✅ ✅ ✅"
                    echo ""
                    echo "  Configuración para keystore.properties:"
                    echo "  storeFile=$keystore_name"
                    echo "  storePassword=$password"
                    echo "  keyAlias=$alias"
                    echo "  keyPassword=$password"
                    echo ""
                    echo "  Copia el keystore a MiLupa:"
                    echo "  cp \"$keystore\" /home/gaston/StudioProjects/MiLupa/"
                    echo ""
                    FOUND=true
                else
                    echo "  ❌ SHA1 no coincide"
                fi
                echo ""
            fi
        done
        
        # Si encontramos el correcto, salir
        if [ "$FOUND" = true ]; then
            break 2
        fi
    done
    
    echo ""
done

if [ "$FOUND" = false ]; then
    echo "=========================================="
    echo "❌ No se encontró el keystore correcto"
    echo "=========================================="
    echo ""
    echo "Ninguna de las contraseñas probadas funcionó con el SHA1 esperado."
    echo "Posibles razones:"
    echo "1. Las contraseñas no son correctas"
    echo "2. El keystore correcto no está en MiLupa1"
    echo "3. El keystore correcto tiene un alias diferente"
    echo ""
    echo "Prueba verificar manualmente con:"
    echo "keytool -list -v -keystore /home/gaston/StudioProjects/MiLupa1/milupa.jks -storepass TU_PASSWORD"
fi

