#!/bin/bash

# SHA1 esperado por Google Play para MiLupa
EXPECTED_SHA1="8C:55:CD:27:16:89:23:DF:8A:EF:60:0A:1F:75:63:C1:3E:68:FE:76"
EXPECTED_SHA1_CLEAN=$(echo "$EXPECTED_SHA1" | tr -d ':')

echo "=========================================="
echo "Verificador de Keystore para MiLupa"
echo "=========================================="
echo ""
echo "SHA1 esperado por Google Play:"
echo "$EXPECTED_SHA1"
echo ""

# Lista de keystores a verificar
KEYSTORE_DIR="/home/gaston/StudioProjects/MiLupa1"
KEYSTORES=("milupa.jks" "key.jks")

for keystore_name in "${KEYSTORES[@]}"; do
    keystore="$KEYSTORE_DIR/$keystore_name"
    
    if [ ! -f "$keystore" ]; then
        echo "⚠️  $keystore_name no encontrado, saltando..."
        echo ""
        continue
    fi
    
    echo "=========================================="
    echo "Verificando: $keystore_name"
    echo "=========================================="
    echo ""
    
    read -sp "Ingresa la contraseña del keystore $keystore_name (o presiona Enter para saltar): " STORE_PASS
    echo ""
    echo ""
    
    if [ -z "$STORE_PASS" ]; then
        echo "⏭️  Saltando este keystore..."
        echo ""
        continue
    fi
    
    # Intentar listar los aliases primero
    ALIASES=$(keytool -list -keystore "$keystore" -storepass "$STORE_PASS" 2>&1 | grep "Alias name:" | awk '{print $3}')
    
    if [ -z "$ALIASES" ]; then
        echo "❌ Error: Contraseña incorrecta o problema con el keystore"
        echo ""
        continue
    fi
    
    echo "✓ Contraseña correcta!"
    echo "Aliases encontrados:"
    echo "$ALIASES" | while read alias; do
        echo "  - $alias"
    done
    echo ""
    
    # Verificar SHA1 de cada alias
    echo "Verificando SHA1 de cada alias..."
    echo ""
    
    echo "$ALIASES" | while read alias; do
        SHA1_INFO=$(keytool -list -v -keystore "$keystore" -storepass "$STORE_PASS" -alias "$alias" 2>&1)
        SHA1=$(echo "$SHA1_INFO" | grep -i "SHA1:" | head -1 | sed 's/.*SHA1: *//' | tr -d ' ' | tr -d ':')
        
        if [ -n "$SHA1" ]; then
            SHA1_FORMATTED=$(echo "$SHA1" | sed 's/\(..\)/\1:/g' | sed 's/:$//')
            echo "Alias: $alias"
            echo "SHA1:  $SHA1_FORMATTED"
            
            if [ "$SHA1" = "$EXPECTED_SHA1_CLEAN" ]; then
                echo ""
                echo "✅ ✅ ✅ ¡COINCIDE! Este es el keystore correcto ✅ ✅ ✅"
                echo ""
                echo "Configuración para keystore.properties:"
                echo "storeFile=$keystore_name"
                echo "storePassword=$STORE_PASS"
                echo "keyAlias=$alias"
                echo "keyPassword=$STORE_PASS"
                echo ""
                echo "Copia el keystore a MiLupa:"
                echo "cp \"$keystore\" /home/gaston/StudioProjects/MiLupa/"
                echo ""
            else
                echo "❌ No coincide (esperado: $EXPECTED_SHA1)"
            fi
            echo ""
        fi
    done
    
    echo ""
    read -p "¿Deseas verificar otro keystore? (s/n): " continuar
    if [ "$continuar" != "s" ] && [ "$continuar" != "S" ]; then
        break
    fi
    echo ""
done

echo ""
echo "=========================================="
echo "Verificación completada"
echo "=========================================="

