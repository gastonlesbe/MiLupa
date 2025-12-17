# Crear Nuevo Keystore y Certificado

Google Play Console te está pidiendo un **nuevo certificado de carga** porque el anterior ya fue usado. Necesitas crear un keystore completamente nuevo.

## Solución Rápida

Ejecuta el script interactivo:

```bash
./crear_keystore_y_certificado.sh
```

Este script te guiará para:
1. ✅ Crear un nuevo keystore
2. ✅ Exportar el certificado en formato PEM
3. ✅ Verificar que todo está correcto

## Proceso Manual

Si prefieres hacerlo manualmente:

### 1. Crear el nuevo keystore

```bash
keytool -genkey -v -keystore milupa_nuevo.jks \
    -keyalg RSA -keysize 2048 -validity 10000 \
    -alias upload \
    -storepass TU_CONTRASEÑA \
    -keypass TU_CONTRASEÑA \
    -dname "CN=Tu Nombre, OU=Unidad, O=Organización, L=Ciudad, ST=Estado, C=AR"
```

**Reemplaza:**
- `milupa_nuevo.jks` con el nombre que quieras
- `TU_CONTRASEÑA` con una contraseña segura
- La información del `-dname` con tus datos

### 2. Exportar el certificado a PEM

```bash
keytool -export -rfc -keystore milupa_nuevo.jks \
    -alias upload \
    -file upload_certificate.pem \
    -storepass TU_CONTRASEÑA
```

### 3. Verificar el certificado

```bash
# Ver información del certificado
openssl x509 -in upload_certificate.pem -noout -text

# Ver SHA1
openssl x509 -in upload_certificate.pem -noout -fingerprint -sha1
```

## Configurar keystore.properties

Después de crear el keystore, crea el archivo `keystore.properties`:

```properties
storeFile=milupa_nuevo.jks
storePassword=TU_CONTRASEÑA
keyAlias=upload
keyPassword=TU_CONTRASEÑA
```

## Subir a Google Play Console

1. Ve a Google Play Console
2. Navega a: **Configuración de la app** → **Integridad de la app**
3. Busca la sección de **Clave de firma de la app**
4. Sube el archivo `upload_certificate.pem`

## Construir el AAB

Una vez que Google acepte el nuevo certificado:

```bash
./gradlew bundleRelease
```

El AAB firmado estará en:
```
app/build/outputs/bundle/release/app-release.aab
```

## ⚠️ IMPORTANTE

- **Guarda el keystore en un lugar seguro** (haz una copia de seguridad)
- **Guarda las contraseñas** en un gestor de contraseñas
- **Sin el keystore no podrás actualizar la app** en el futuro
- El keystore está en `.gitignore` (no se subirá a Git)

## Verificar que el SHA1 es diferente

Antes de subir, verifica que el SHA1 del nuevo certificado es diferente al anterior:

```bash
# SHA1 del nuevo certificado
openssl x509 -in upload_certificate.pem -noout -fingerprint -sha1
```

Este SHA1 debe ser **diferente** al que Google rechazó.

