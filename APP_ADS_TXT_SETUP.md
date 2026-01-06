# Configuración de app-ads.txt para AdMob

## ¿Qué es app-ads.txt?

El archivo `app-ads.txt` (Authorized Digital Sellers) es un estándar de IAB Tech Lab que permite a los editores declarar públicamente las empresas autorizadas para vender su inventario de publicidad digital. Esto ayuda a prevenir fraudes en publicidad.

## Archivo Creado

He creado el archivo `docs/app-ads.txt` con el siguiente contenido:

```
google.com, pub-9841764898906750, DIRECT, f08c47fec0942fa0
```

Este archivo declara que:
- **google.com** es el dominio autorizado
- **pub-9841764898906750** es tu Publisher ID de AdMob
- **DIRECT** indica una relación directa con el vendedor
- **f08c47fec0942fa0** es el certificado de verificación

## Pasos para Publicar

### 1. Hacer Commit y Push del Archivo

```bash
# Agregar el archivo
git add docs/app-ads.txt

# Hacer commit
git commit -m "Agregar app-ads.txt para AdMob"

# Hacer push
git push origin main
```

### 2. Verificar que GitHub Pages Esté Activo

Asegúrate de que GitHub Pages esté configurado para la carpeta `/docs`:

1. Ve a tu repositorio en GitHub
2. **Settings** → **Pages**
3. Verifica que:
   - **Source**: `main` branch
   - **Folder**: `/docs`

### 3. Verificar la URL del Archivo

Una vez publicado, el archivo debe estar disponible en:

```
https://TU_USUARIO.github.io/MiLupa/app-ads.txt
```

**Importante:** Reemplaza `TU_USUARIO` con tu nombre de usuario de GitHub.

### 4. Configurar el Dominio en Google Play Console

El dominio que debes registrar en Google Play Console debe coincidir exactamente con el dominio de GitHub Pages. Hay dos formas de hacerlo:

#### Opción A: En la Ficha de Play Store (Recomendado)

1. Ve a **Google Play Console**
2. Selecciona tu app **MiLupa**
3. En el menú lateral izquierdo, ve a **Presencia en Google Play Store** → **Ficha de Play Store principal**
4. Desplázate hacia abajo hasta la sección **Información de contacto**
5. En el campo **Sitio web**, ingresa la URL completa de tu GitHub Pages:
   ```
   https://TU_USUARIO.github.io/MiLupa
   ```
   O solo el dominio (sin https://):
   ```
   TU_USUARIO.github.io
   ```
6. Haz clic en **Guardar** (arriba a la derecha)

#### Opción B: En AdMob Console (Alternativa)

Si no encuentras la opción en Google Play Console, puedes configurarlo directamente en AdMob:

1. Ve a [AdMob Console](https://apps.admob.com/)
2. Selecciona tu app **MiLupa**
3. Ve a **Monetize** → **app-ads.txt** (o **Monetización** → **app-ads.txt**)
4. Ingresa el dominio:
   ```
   TU_USUARIO.github.io
   ```
5. Guarda los cambios

**Nota:** El dominio debe coincidir exactamente con el dominio donde está publicado tu archivo `app-ads.txt`.

### 5. Esperar la Verificación de AdMob

- **Tiempo de espera:** Al menos 24 horas
- AdMob rastreará y verificará automáticamente tu archivo `app-ads.txt`
- No necesitas hacer nada más, AdMob lo verificará automáticamente

### 6. Verificar el Estado en AdMob

Después de 24 horas:

1. Ve a [AdMob Console](https://apps.admob.com/)
2. Selecciona tu app
3. Ve a **Monetize** → **app-ads.txt**
4. Verifica el estado:
   - ✅ **Verified** (Verificado) - El archivo está correcto
   - ⚠️ **Pending** (Pendiente) - Aún esperando verificación
   - ❌ **Error** (Error) - Hay un problema con el archivo

## Verificar que el Archivo Funciona

### Opción 1: Navegador Web

Abre en tu navegador:
```
https://TU_USUARIO.github.io/MiLupa/app-ads.txt
```

Deberías ver:
```
google.com, pub-9841764898906750, DIRECT, f08c47fec0942fa0
```

### Opción 2: Terminal (curl)

```bash
curl https://TU_USUARIO.github.io/MiLupa/app-ads.txt
```

Deberías ver el contenido del archivo.

### Opción 3: Verificador de app-ads.txt

Puedes usar herramientas online para verificar:
- [app-ads.txt Validator](https://adstxt.guru/)
- [IAB app-ads.txt Validator](https://iabtechlab.com/ads-txt/)

## Solución de Problemas

### El archivo no se encuentra (404)

1. **Verifica que GitHub Pages esté activo:**
   - Ve a Settings → Pages en tu repositorio
   - Asegúrate de que esté configurado para `/docs`

2. **Verifica que el archivo esté en la carpeta correcta:**
   - El archivo debe estar en `docs/app-ads.txt`
   - No debe estar en `docs/docs/app-ads.txt`

3. **Espera unos minutos:**
   - GitHub Pages puede tardar 5-10 minutos en actualizar

### AdMob no verifica el archivo después de 24 horas

1. **Verifica la URL exacta:**
   - El dominio en Google Play Console debe coincidir exactamente
   - No debe tener `https://` al principio (solo el dominio)
   - Ejemplo: `TU_USUARIO.github.io` (no `https://TU_USUARIO.github.io`)

2. **Verifica el contenido del archivo:**
   - Debe ser exactamente: `google.com, pub-9841764898906750, DIRECT, f08c47fec0942fa0`
   - No debe tener espacios extra al final
   - No debe tener líneas adicionales (excepto una línea en blanco al final si es necesario)

3. **Verifica que el archivo sea accesible:**
   - Abre la URL en un navegador en modo incógnito
   - Verifica que no requiera autenticación

### El archivo se ve mal en el navegador

- Asegúrate de que el archivo tenga extensión `.txt`
- Verifica que no tenga caracteres especiales o codificación incorrecta
- El archivo debe ser texto plano (UTF-8)

## Estructura del Archivo app-ads.txt

El formato es:
```
<DOMAIN>, <PUBLISHER_ID>, <RELATIONSHIP>, <CERTIFICATION_AUTHORITY_ID>
```

Donde:
- **DOMAIN**: `google.com` (dominio del vendedor)
- **PUBLISHER_ID**: `pub-9841764898906750` (tu ID de AdMob)
- **RELATIONSHIP**: `DIRECT` (relación directa) o `RESELLER` (revendedor)
- **CERTIFICATION_AUTHORITY_ID**: `f08c47fec0942fa0` (certificado de verificación)

## Notas Importantes

- ✅ El archivo debe estar en la **raíz del dominio** (o en la carpeta configurada en GitHub Pages)
- ✅ El dominio debe coincidir **exactamente** con el registrado en Google Play Console
- ✅ AdMob verificará automáticamente después de 24 horas
- ✅ No necesitas hacer nada más después de publicar el archivo
- ✅ El archivo debe ser accesible públicamente (sin autenticación)

## Referencias

- [IAB Tech Lab app-ads.txt Specification](https://iabtechlab.com/ads-txt/)
- [Google AdMob app-ads.txt Documentation](https://support.google.com/admob/answer/9363764)
- [Google Play Console Help](https://support.google.com/googleplay/android-developer/answer/9888179)

