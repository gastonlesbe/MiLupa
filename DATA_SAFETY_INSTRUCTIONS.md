# Instrucciones para Actualizar Data Safety en Google Play Console

## Problema Detectado

Google Play detectó que tu aplicación está recolectando **Device Or Other IDs** (Advertising ID, Android ID, etc.) a través del SDK de AppLovin (`com.applovin:applovin-sdk`), que viene incluido automáticamente en el SDK de Appodeal.

**SDK detectado:** `com.applovin:applovin-sdk` (incluido en Appodeal SDK 3.8.0.0)

## Solución: Actualizar el Formulario de Data Safety

### Paso 1: Acceder al Formulario de Data Safety

1. Ve a [Google Play Console](https://play.google.com/console)
2. Selecciona tu app: **MiLupa - Lupa - Camara - Flash fijo**
3. En el menú lateral, ve a **Política** → **Seguridad de datos** (o **App content** → **Data safety**)

### Paso 2: Actualizar la Sección de Recolección de Datos

#### 2.1. Data Collection and Security

1. En la pregunta **"Does your app collect or share any of the required user data types?"**
   - Selecciona **"Yes"** (si actualmente dice "No", cámbialo a "Yes")

#### 2.2. Data Types Section

1. Busca la sección **"Device or other IDs"**
2. Selecciona **"Device or other IDs"** para indicar que tu app recolecta este tipo de datos

#### 2.3. Detalles de Device or other IDs

Configura los siguientes campos:

**¿Qué datos recolectas?**
- ✅ **Advertising ID** (ID de publicidad)
- ✅ **Android ID** (ID de Android)
- ⚠️ **IMEI** (solo si AppLovin lo recolecta - generalmente no)
- ⚠️ **BSSID** (solo si AppLovin lo recolecta - generalmente no)
- ⚠️ **MAC address** (solo si AppLovin lo recolecta - generalmente no)

**¿Cómo se recolectan estos datos?**
- Selecciona: **"Automatically"** (automáticamente)

**¿Por qué se recolectan estos datos?**
- Selecciona: **"Advertising or marketing"** (Publicidad o marketing)
- También puedes seleccionar: **"Analytics"** (si aplica)

**¿Se comparten estos datos con terceros?**
- Selecciona: **"Yes"** (Sí, porque Appodeal/AppLovin los comparten con redes de publicidad)

**¿Es necesario para la funcionalidad de la app?**
- Selecciona: **"No"** (No es necesario para la funcionalidad principal, solo para publicidad)

**¿Los usuarios pueden optar por no compartir estos datos?**
- Selecciona: **"Yes"** (Sí, los usuarios pueden desactivar la publicidad personalizada en la configuración de Android)

### Paso 3: Declarar SDKs de Terceros

En la sección de **"Third-party data sharing"** o **"SDKs"**:

1. Busca o agrega: **AppLovin SDK**
2. Indica que este SDK recolecta:
   - Device or other IDs
   - Para propósitos de: Advertising/Marketing

### Paso 4: Revisar Otras Categorías de Datos

Asegúrate de revisar y declarar (si aplica):

- **App activity** (si Appodeal/AppLovin recolecta información sobre la actividad de la app)
- **Device or other IDs** (ya mencionado arriba)
- **Location** (solo si se recolecta - generalmente no para apps de cámara)
- **Personal info** (solo si se recolecta - generalmente no)

### Paso 5: Guardar y Enviar para Revisión

1. **Guarda** todos los cambios
2. Ve a **"Publishing overview"** (Resumen de publicación)
3. Haz clic en **"Send for review"** (Enviar para revisión) o **"Submit update"** (Enviar actualización)

## Información Adicional

### ¿Por qué AppLovin recolecta Device IDs?

AppLovin es una red de publicidad que forma parte del ecosistema de Appodeal. Recolecta Advertising IDs y Android IDs para:
- Mostrar publicidad relevante
- Medir el rendimiento de los anuncios
- Prevenir fraudes en publicidad

### Referencias Útiles

- [Google Play SDK Index](https://play.google.com/sdks) - Para ver qué SDKs están declarados
- [AppLovin Data Safety Information](https://www.applovin.com/privacy/) - Información sobre privacidad de AppLovin
- [Appodeal Privacy Policy](https://appodeal.com/privacy-policy/) - Política de privacidad de Appodeal

### Nota Importante

Aunque tu app no use directamente AppLovin, Appodeal lo incluye como parte de su red de publicidad. Por lo tanto, debes declarar la recolección de datos que AppLovin realiza automáticamente.

## Checklist Final

Antes de enviar para revisión, verifica que:

- [ ] Has seleccionado "Yes" en "Does your app collect or share any of the required user data types?"
- [ ] Has declarado "Device or other IDs" como tipo de dato recolectado
- [ ] Has indicado que se recolecta "Automatically"
- [ ] Has seleccionado "Advertising or marketing" como propósito
- [ ] Has indicado que los datos se comparten con terceros ("Yes")
- [ ] Has indicado que los usuarios pueden optar por no compartir ("Yes")
- [ ] Has guardado todos los cambios
- [ ] Has enviado la actualización para revisión

## Tiempo de Revisión

Google Play generalmente revisa las actualizaciones del formulario de Data Safety en **24-48 horas**. Una vez aprobado, tu app volverá a estar disponible en Google Play.

