# Configurar GitHub Pages para Política de Privacidad

## Pasos para Activar GitHub Pages

### 1. Verificar que los archivos estén en el repositorio

Los archivos de la política de privacidad están en la carpeta `docs/`:
- `docs/privacy-policy.html` - Política de privacidad completa
- `docs/index.html` - Redirección automática

### 2. Hacer commit y push de los archivos

```bash
# Agregar los archivos
git add docs/privacy-policy.html docs/index.html
git add GITHUB_PAGES_SETUP.md

# Hacer commit
git commit -m "Agregar política de privacidad para GitHub Pages"

# Hacer push
git push origin main
```

### 3. Activar GitHub Pages en GitHub

1. Ve a tu repositorio en GitHub (https://github.com/TU_USUARIO/MiLupa)
2. Haz clic en **Settings** (Configuración)
3. En el menú lateral, busca **Pages** (páginas)
4. En **Source** (fuente), selecciona:
   - **Branch**: `main` (o `master`)
   - **Folder**: `/docs`
5. Haz clic en **Save** (Guardar)

### 4. Esperar a que se active

GitHub puede tardar unos minutos en publicar tu sitio. Verás un mensaje como:
```
Your site is published at https://TU_USUARIO.github.io/MiLupa/
```

### 5. URL de tu Política de Privacidad

Una vez activado, tu política de privacidad estará disponible en:

```
https://TU_USUARIO.github.io/MiLupa/privacy-policy.html
```

O simplemente:

```
https://TU_USUARIO.github.io/MiLupa/
```

(El index.html redirige automáticamente a privacy-policy.html)

## Personalizar la Política

Antes de publicar, edita `docs/privacy-policy.html` y reemplaza:

- `[TU_EMAIL_AQUI]` con tu email de contacto real

## Verificar que Funciona

1. Espera 5-10 minutos después de activar GitHub Pages
2. Visita: `https://TU_USUARIO.github.io/MiLupa/privacy-policy.html`
3. Verifica que la página se vea correctamente

## Usar la URL en Google Play Console

1. Ve a **Google Play Console**
2. Selecciona tu app **MiLupa**
3. Ve a **Política y programas** → **Política de la app**
4. En **Política de privacidad**, pega la URL:
   ```
   https://TU_USUARIO.github.io/MiLupa/privacy-policy.html
   ```
5. Guarda los cambios

## Actualizar la Política

Cada vez que actualices la política:

1. Edita `docs/privacy-policy.html`
2. Haz commit y push:
   ```bash
   git add docs/privacy-policy.html
   git commit -m "Actualizar política de privacidad"
   git push origin main
   ```
3. Los cambios se reflejarán automáticamente en GitHub Pages (puede tardar unos minutos)

## Notas Importantes

- ✅ La URL de GitHub Pages es **gratis** y **permanente**
- ✅ Los cambios se actualizan automáticamente al hacer push
- ✅ La página es accesible públicamente
- ✅ Funciona en dispositivos móviles y de escritorio

## Solución de Problemas

### La página no carga
- Espera 10-15 minutos después de activar GitHub Pages
- Verifica que la carpeta sea `/docs` en la configuración
- Verifica que el branch sea `main` o `master`

### Error 404
- Verifica que los archivos estén en la carpeta `docs/`
- Verifica que hayas hecho push de los archivos
- Verifica la URL (debe ser exactamente como se muestra arriba)

### La página se ve mal
- Verifica que el archivo HTML esté completo
- Abre el archivo localmente en un navegador para verificar
- Revisa la consola del navegador para errores

