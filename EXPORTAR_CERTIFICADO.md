# Exportar Certificado a formato PEM

Google Play Console te está pidiendo que exportes el certificado de clave de carga en formato PEM.

## Opción 1: Convertir upload_cert.der a PEM (Recomendado)

Si ya tienes el archivo `upload_cert.der` que Google te proporcionó, simplemente conviértelo a PEM:

```bash
openssl x509 -inform DER -in upload_cert.der -out upload_certificate.pem
```

Esto creará el archivo `upload_certificate.pem` que puedes subir a Google Play Console.

## Opción 2: Exportar desde tu keystore

Si prefieres exportar directamente desde tu keystore, usa este comando:

```bash
keytool -export -rfc -keystore upload-keystore.jks -alias upload -file upload_certificate.pem
```

**Reemplaza:**
- `upload-keystore.jks` con la ruta a tu keystore (ej: `/home/gaston/StudioProjects/MiLupa1/milupa.jks`)
- `upload` con el alias de tu clave (puede ser `upload`, `key0`, o el alias que usaste)
- Te pedirá la contraseña del keystore

## Script Automatizado

También puedes usar el script que creé:

```bash
./exportar_certificado_pem.sh
```

Este script:
1. Primero intenta convertir `upload_cert.der` a PEM (si existe)
2. Si no existe, te permite exportar desde un keystore

## Verificar el Certificado

Después de crear el archivo PEM, puedes verificar su información:

```bash
# Ver información del certificado
openssl x509 -in upload_certificate.pem -noout -text

# Ver SHA1 fingerprint
openssl x509 -in upload_certificate.pem -noout -fingerprint -sha1

# Ver información básica
openssl x509 -in upload_certificate.pem -noout -subject -issuer -dates
```

## Subir a Google Play Console

Una vez que tengas el archivo `upload_certificate.pem`:
1. Ve a Google Play Console
2. Navega a: **Configuración de la app** → **Integridad de la app**
3. Busca la sección de **Clave de firma de la app**
4. Sube el archivo `upload_certificate.pem`

## Nota

El archivo PEM es un formato de texto que contiene el certificado. Es el formato estándar que Google Play Console espera para los certificados de clave de carga.

