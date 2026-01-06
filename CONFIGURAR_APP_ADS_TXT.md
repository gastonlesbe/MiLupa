# Guía Rápida: Configurar app-ads.txt en Google Play Console

## Ubicación Exacta en Google Play Console

### Paso 1: Acceder a la Ficha de Play Store

1. Ve a [Google Play Console](https://play.google.com/console/)
2. Selecciona tu app **MiLupa**
3. En el menú lateral izquierdo, busca y haz clic en:
   ```
   Presencia en Google Play Store
   ```
4. Luego haz clic en:
   ```
   Ficha de Play Store principal
   ```

### Paso 2: Actualizar el Sitio Web

1. Desplázate hacia abajo en la página
2. Busca la sección **"Información de contacto"** o **"Contact details"**
3. En el campo **"Sitio web"** o **"Website"**, ingresa:
   ```
   https://gastonlesbe.github.io/MiLupa
   ```
   
   **O solo el dominio (sin https://):**
   ```
   gastonlesbe.github.io
   ```

4. Haz clic en **"Guardar"** o **"Save"** (botón en la parte superior derecha)

### Paso 3: Verificar que el Archivo Esté Publicado

Antes de guardar, verifica que tu archivo `app-ads.txt` esté accesible:

1. Abre en tu navegador:
   ```
   https://gastonlesbe.github.io/MiLupa/app-ads.txt
   ```

2. Deberías ver:
   ```
   google.com, pub-9841764898906750, DIRECT, f08c47fec0942fa0
   ```

3. Si ves el contenido, el archivo está correctamente publicado ✅

## Si No Encuentras la Opción "Sitio web"

### Alternativa 1: Buscar en Otras Secciones

1. **Política y programas** → **Política de la app**
   - A veces el sitio web se configura aquí junto con la política de privacidad

2. **Configuración de la app** → **Detalles de la app**
   - Puede estar en la información general de la app

### Alternativa 2: Configurar Directamente en AdMob

Si no encuentras la opción en Google Play Console, puedes configurarlo en AdMob:

1. Ve a [AdMob Console](https://apps.admob.com/)
2. Selecciona tu app **MiLupa**
3. En el menú lateral, busca:
   - **Monetize** → **app-ads.txt** (en inglés)
   - O **Monetización** → **app-ads.txt** (en español)
4. Ingresa el dominio:
   ```
   gastonlesbe.github.io
   ```
   (Sin `https://` al principio)
5. Guarda los cambios

## Verificación Final

### 1. Verificar que el Archivo Esté Accesible

Abre en tu navegador (o en modo incógnito):
```
https://gastonlesbe.github.io/MiLupa/app-ads.txt
```

### 2. Verificar el Contenido

El archivo debe mostrar exactamente:
```
google.com, pub-9841764898906750, DIRECT, f08c47fec0942fa0
```

### 3. Esperar la Verificación de AdMob

- **Tiempo:** 24-48 horas
- AdMob verificará automáticamente el archivo
- No necesitas hacer nada más

### 4. Verificar el Estado en AdMob (Después de 24 horas)

1. Ve a [AdMob Console](https://apps.admob.com/)
2. Selecciona tu app
3. Ve a **Monetize** → **app-ads.txt**
4. Verifica el estado:
   - ✅ **Verified** = Verificado correctamente
   - ⚠️ **Pending** = Aún esperando verificación
   - ❌ **Error** = Hay un problema (revisa la URL)

## Resumen de URLs Importantes

- **Archivo app-ads.txt:** `https://gastonlesbe.github.io/MiLupa/app-ads.txt`
- **Sitio web a registrar:** `https://gastonlesbe.github.io/MiLupa` o `gastonlesbe.github.io`
- **Política de privacidad:** `https://gastonlesbe.github.io/MiLupa/privacy-policy.html`
- **Repositorio GitHub:** `https://github.com/gastonlesbe/MiLupa`

## Solución de Problemas

### "No encuentro la opción 'Sitio web'"

**Solución:** 
- Busca en **"Información de contacto"** o **"Contact details"**
- O configura directamente en AdMob Console (ver Alternativa 2 arriba)

### "El archivo no se encuentra (404)"

**Solución:**
1. Verifica que GitHub Pages esté activo (Settings → Pages)
2. Verifica que el archivo esté en `docs/app-ads.txt`
3. Espera 5-10 minutos después de hacer push

### "AdMob no verifica después de 24 horas"

**Solución:**
1. Verifica que la URL del sitio web en Google Play Console sea exacta
2. Verifica que el archivo sea accesible públicamente
3. Verifica que el contenido del archivo sea correcto (sin espacios extra)

## ¿Necesitas Ayuda?

Si aún no encuentras dónde configurar el sitio web, puedes:
1. Buscar en Google Play Console usando la barra de búsqueda: "sitio web" o "website"
2. Revisar todas las secciones del menú lateral
3. Configurar directamente en AdMob Console (funciona igual)

