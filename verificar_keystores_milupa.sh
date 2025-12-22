#!/bin/bash

# SHA1 esperado por Google Play
EXPECTED_SHA1="97:33:B7:09:B1:B5:F4:F8:40:72:DB:F2:5E:C1:39:3D:57:71:6B:23"
EXPECTED_SHA1_CLEAN="9733B709B1B5F4F84072DBF25EC1393D57716B23"

echo "=========================================="
echo "Verificación de Keystores MiLupa"
echo "=========================================="
echo ""
echo "SHA1 Esperado: $EXPECTED_SHA1"
echo ""

# Keystores relevantes a verificar
KEYSTORES=(
    "/home/gaston/StudioProjects/MiLupa1/milupa.jks"
    "/home/gaston/StudioProjects/MiLupa1/key.jks"
    "/home/gaston/StudioProjects/MiLupa/milupa_nuevo.jks"
)

# Verificar cada keystore
for keystore in "${KEYSTORES[@]}"; do
    if [ -f "$keystore" ]; then
        echo "----------------------------------------"
        echo "Keystore: $keystore"
        echo "----------------------------------------"
        
        # Intentar listar aliases sin contraseña primero
        echo "Alias disponibles (intentando sin contraseña):"
        keytool -list -keystore "$keystore" -storepass "" 2>/dev/null | grep "Alias name:" || echo "  (requiere contraseña)"
        
        echo ""
        echo "Para verificar el SHA1 de este keystore, ejecuta:"
        echo "  keytool -list -v -keystore \"$keystore\" -alias <alias>"
        echo ""
    else
        echo "Keystore no encontrado: $keystore"
        echo ""
    fi
done

echo "=========================================="
echo "Instrucciones:"
echo "=========================================="
echo ""
echo "1. Para verificar el SHA1 de un keystore, ejecuta:"
echo "   keytool -list -v -keystore <ruta_keystore> -alias <alias>"
echo ""
echo "2. Busca la línea que dice 'SHA1:' y compara con:"
echo "   $EXPECTED_SHA1"
echo ""
echo "3. Si encuentras el keystore correcto, crea/actualiza keystore.properties:"
echo "   storeFile=<ruta_completa_al_keystore>"
echo "   storePassword=<contraseña_del_keystore>"
echo "   keyAlias=<alias_del_certificado>"
echo "   keyPassword=<contraseña_del_certificado>"
echo ""

