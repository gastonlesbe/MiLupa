# Instrucciones para Encontrar el Keystore Correcto

## Problema
El AAB está firmado con SHA1 incorrecto. Google Play espera:
- **SHA1 esperado**: `8C:55:CD:27:16:89:23:DF:8A:EF:60:0A:1F:75:63:C1:3E:68:FE:76`

## Solución Rápida

Ejecuta este comando en la terminal:

```bash
./encontrar_keystore_correcto.sh
```

Este script probará automáticamente las tres contraseñas que proporcionaste:
- octopus2317522
- goldfish210809
- gsl2317522

En ambos keystores:
- milupa.jks
- key.jks

## Si el script encuentra el keystore correcto

El script te mostrará la configuración exacta para `keystore.properties`. Luego:

1. **Copia el keystore correcto:**
   ```bash
   cp /home/gaston/StudioProjects/MiLupa1/KEYSTORE_CORRECTO.jks /home/gaston/StudioProjects/MiLupa/
   ```

2. **Crea `keystore.properties`** con la información que el script te mostró

3. **Construye el AAB:**
   ```bash
   ./gradlew bundleRelease
   ```

## Verificación Manual (si el script no funciona)

Si prefieres verificar manualmente, ejecuta:

```bash
# Para milupa.jks
keytool -list -v -keystore /home/gaston/StudioProjects/MiLupa1/milupa.jks -storepass octopus2317522 | grep SHA1
keytool -list -v -keystore /home/gaston/StudioProjects/MiLupa1/milupa.jks -storepass goldfish210809 | grep SHA1
keytool -list -v -keystore /home/gaston/StudioProjects/MiLupa1/milupa.jks -storepass gsl2317522 | grep SHA1

# Para key.jks
keytool -list -v -keystore /home/gaston/StudioProjects/MiLupa1/key.jks -storepass octopus2317522 | grep SHA1
keytool -list -v -keystore /home/gaston/StudioProjects/MiLupa1/key.jks -storepass goldfish210809 | grep SHA1
keytool -list -v -keystore /home/gaston/StudioProjects/MiLupa1/key.jks -storepass gsl2317522 | grep SHA1
```

Busca el SHA1 que coincida con: `8C:55:CD:27:16:89:23:DF:8A:EF:60:0A:1F:75:63:C1:3E:68:FE:76`

