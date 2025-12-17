# Solución: Keystore Incorrecto para MiLupa

## Problema
El AAB está firmado con una clave incorrecta. Google Play espera:
- **SHA1 esperado**: `8C:55:CD:27:16:89:23:DF:8A:EF:60:0A:1F:75:63:C1:3E:68:FE:76`
- **SHA1 actual**: `D2:EA:FD:99:BB:6B:03:F8:77:F2:FE:60:80:C3:A7:FD:11:8B:F9:40`

## Solución

### Opción 1: Encontrar el keystore correcto (Recomendado)

1. Ejecuta el script de verificación:
   ```bash
   ./verificar_keystore_milupa.sh
   ```

2. El script te pedirá las contraseñas de los keystores y verificará cuál tiene el SHA1 correcto.

3. Una vez encontrado, copia el keystore correcto:
   ```bash
   cp /home/gaston/StudioProjects/MiLupa1/KEYSTORE_CORRECTO.jks /home/gaston/StudioProjects/MiLupa/
   ```

4. Crea `keystore.properties` con la información del keystore correcto:
   ```properties
   storeFile=KEYSTORE_CORRECTO.jks
   storePassword=TU_PASSWORD
   keyAlias=TU_ALIAS
   keyPassword=TU_PASSWORD
   ```

### Opción 2: Verificar manualmente

1. Verifica el SHA1 de cada keystore:
   ```bash
   keytool -list -v -keystore /home/gaston/StudioProjects/MiLupa1/milupa.jks -storepass TU_PASSWORD
   keytool -list -v -keystore /home/gaston/StudioProjects/MiLupa1/key.jks -storepass TU_PASSWORD
   ```

2. Busca el SHA1 que coincida con: `8C:55:CD:27:16:89:23:DF:8A:EF:60:0A:1F:75:63:C1:3E:68:FE:76`

3. Usa ese keystore para firmar el AAB.

### Opción 3: Si no encuentras el keystore correcto

Si no puedes encontrar el keystore con el SHA1 correcto, tendrás que:
1. Contactar a Google Play Console para resetear la clave de firma
2. O crear un nuevo keystore (pero esto requerirá actualizar la app en Play Store con la nueva clave)

## Después de configurar el keystore correcto

1. Asegúrate de que `keystore.properties` esté configurado correctamente
2. Copia el keystore correcto a la raíz del proyecto MiLupa
3. Construye el AAB:
   ```bash
   ./gradlew bundleRelease
   ```

4. Verifica el SHA1 del AAB generado:
   ```bash
   keytool -printcert -jarfile app/build/outputs/bundle/release/app-release.aab
   ```

5. El SHA1 debe coincidir con: `8C:55:CD:27:16:89:23:DF:8A:EF:60:0A:1F:75:63:C1:3E:68:FE:76`

