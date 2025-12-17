#!/bin/bash

# SHA1 esperado por Google Play para MiLupa
EXPECTED_SHA1="8C:55:CD:27:16:89:23:DF:8A:EF:60:0A:1F:75:63:C1:3E:68:FE:76"
EXPECTED_SHA1_CLEAN=$(echo "$EXPECTED_SHA1" | tr -d ':')

echo "=========================================="
echo "Buscando keystore correcto para MiLupa"
echo "=========================================="
echo ""
echo "SHA1 esperado: $EXPECTED_SHA1"
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
        echo "⚠️  $keystore_name no encontrado"
        continue
    fi
    
    echo "=========================================="
    echo "Keystore: $keystore_name"
    echo "=========================================="
    
    for password in "${PASSWORDS[@]}"; do
        echo ""
        echo "Probando contraseña: $password"
        
        # Verificar si la contraseña es correcta
        ALIAS_OUTPUT=$(keytool -list -keystore "$keystore" -storepass "$password" 2>&1)
        
        if echo "$ALIAS_OUTPUT" | grep -q "keystore password was incorrect"; then
            echo "  ❌ Contraseña incorrecta"
            continue
        fi
        
        if echo "$ALIAS_OUTPUT" | grep -q "Alias name:"; then
            echo "  ✓ Contraseña correcta!"
            
            # Obtener todos los aliases
            ALIASES=$(echo "$ALIAS_OUTPUT" | grep "Alias name:" | awk '{print $3}')
            
            # Verificar SHA1 de cada alias
            for alias in $ALIASES; do
                SHA1_OUTPUT=$(keytool -list -v -keystore "$keystore" -storepass "$password" -alias "$alias" 2>&1)
                SHA1=$(echo "$SHA1_OUTPUT" | grep -i "SHA1:" | head -1 | sed 's/.*SHA1: *//' | tr -d ' ' | tr -d ':')
                
                if [ -n "$SHA1" ]; then
                    SHA1_FORMATTED=$(echo "$SHA1" | sed 's/\(..\)/\1:/g' | sed 's/:$//')
                    echo ""
                    echo "  Alias: $alias"
                    echo "  SHA1:  $SHA1_FORMATTED"
                    
                    if [ "$SHA1" = "$EXPECTED_SHA1_CLEAN" ]; then
                        echo ""
                        echo "  ✅ ✅ ✅ ¡ENCONTRADO! Este es el keystore correcto ✅ ✅ ✅"
                        echo ""
                        echo "  =========================================="
                        echo "  Configuración para keystore.properties:"
                        echo "  =========================================="
                        echo "  storeFile=$keystore_name"
                        echo "  storePassword=$password"
                        echo "  keyAlias=$alias"
                        echo "  keyPassword=$password"
                        echo ""
                        echo "  =========================================="
                        echo "  Próximos pasos:"
                        echo "  =========================================="
                        echo "  1. Copia el keystore:"
                        echo "     cp \"$keystore\" /home/gaston/StudioProjects/MiLupa/"
                        echo ""
                        echo "  2. Crea keystore.properties con la configuración de arriba"
                        echo ""
                        echo "  3. Construye el AAB:"
                        echo "     ./gradlew bundleRelease"
                        echo ""
                        FOUND=true
                        break 2
                    else
                        echo "  ❌ SHA1 no coincide"
                    fi
                fi
            done
        fi
    done
    echo ""
done

if [ "$FOUND" = false ]; then
    echo ""
    echo "=========================================="
    echo "❌ No se encontró el keystore correcto"
    echo "=========================================="
    echo ""
    echo "Ninguna de las contraseñas probadas funcionó."
    echo ""
    echo "Verifica manualmente con:"
    echo "  keytool -list -v -keystore /home/gaston/StudioProjects/MiLupa1/milupa.jks -storepass TU_PASSWORD"
    echo ""
fi

