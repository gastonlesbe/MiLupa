# Verificación de Firma SHA1 - MiLupa

## Situación Actual

Google Play está rechazando el App Bundle porque está firmado con una clave incorrecta.

### SHA1 Esperado por Google Play:
```
97:33:B7:09:B1:B5:F4:F8:40:72:DB:F2:5E:C1:39:3D:57:71:6B:23
```

### SHA1 Actual (Incorrecto):
```
D2:EA:FD:99:BB:6B:03:F8:77:F2:FE:60:80:C3:A7:FD:11:8B:F9:40
```

## Estado de la Verificación

Se verificaron los siguientes keystores sin encontrar el SHA1 correcto:
- `/home/gaston/StudioProjects/MiLupa1/milupa.jks`
- `/home/gaston/StudioProjects/MiLupa1/key.jks`
- `/home/gaston/StudioProjects/MiLupa/milupa_nuevo.jks`

**Nota:** Los keystores requieren contraseña y no se pudo verificar con contraseñas comunes.

## Solución

### Opción 1: Encontrar el Keystore Correcto

Si tienes el keystore original que se usó para subir la app a Google Play, verifica su SHA1:

```bash
keytool -list -v -keystore <ruta_keystore> -alias <alias>
```

Busca la línea que dice `SHA1:` y compara con el SHA1 esperado.

### Opción 2: Configurar el Keystore Correcto

Una vez que encuentres el keystore correcto, crea o actualiza el archivo `keystore.properties` en la raíz del proyecto:

```properties
storeFile=/ruta/completa/al/keystore.jks
storePassword=tu_contraseña_keystore
keyAlias=alias_del_certificado
keyPassword=tu_contraseña_certificado
```

### Opción 3: Solicitar Nueva Clave de Firma a Google

Si no tienes acceso al keystore original, puedes solicitar a Google Play que te permita usar una nueva clave de firma. Esto requiere:

1. Ir a Google Play Console
2. Navegar a la sección de "Firma de la app"
3. Solicitar un "Reset de clave de firma"
4. Crear un nuevo keystore y subir el certificado

**⚠️ ADVERTENCIA:** Esto puede afectar las actualizaciones de la app para usuarios existentes.

## Verificación del Keystore Actual

Para verificar el SHA1 del keystore que se está usando actualmente (`milupa_nuevo.jks`):

```bash
cd /home/gaston/StudioProjects/MiLupa
keytool -list -v -keystore milupa_nuevo.jks -alias <alias>
```

Si el SHA1 no coincide con el esperado, necesitas usar el keystore correcto.

## Scripts Disponibles

- `verificar_firma_sha1.sh` - Busca keystores con el SHA1 correcto
- `verificar_keystores_milupa.sh` - Lista keystores relevantes
- `verificar_sha1_interactivo.sh` - Verifica keystores con contraseñas comunes

## Próximos Pasos

1. **Si tienes el keystore original:**
   - Verifica su SHA1 manualmente
   - Crea/actualiza `keystore.properties`
   - Reconstruye el App Bundle

2. **Si NO tienes el keystore original:**
   - Solicita un reset de clave de firma a Google Play
   - Crea un nuevo keystore
   - Sube el nuevo certificado a Google Play

