# Proceso para Nueva Clave de Firma de Google Play

## Estado Actual
- **Versión actual en Play Store**: 31 (2.1)
- **Nueva versión a subir**: 32 (2.2)
- **Problema**: SHA1 del keystore actual no coincide con el esperado por Google Play
- **Solución**: Solicitar nueva clave de firma a Google

## Proceso con Google Play

### 1. Solicitar Reset de Clave de Firma
1. Ve a Google Play Console
2. Navega a: **Configuración de la app** → **Integridad de la app**
3. Busca la sección de **Clave de firma de la app**
4. Solicita un **reset de la clave de firma**
5. Google te proporcionará una nueva clave de firma

### 2. Crear Nuevo Keystore (cuando recibas la nueva clave)

Cuando Google te dé la nueva clave, crea un nuevo keystore:

```bash
keytool -genkey -v -keystore milupa_nuevo.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
```

**Información a usar:**
- **Nombre y apellidos**: Tu nombre o nombre de la empresa
- **Unidad organizativa**: (opcional)
- **Organización**: (opcional)
- **Ciudad**: (opcional)
- **Estado o provincia**: (opcional)
- **Código de país**: (ej: AR, US, etc.)
- **Contraseña del keystore**: (elige una segura y guárdala)
- **Contraseña del alias**: (usa la misma que el keystore)

### 3. Configurar keystore.properties

Crea el archivo `keystore.properties` en la raíz del proyecto:

```properties
storeFile=milupa_nuevo.jks
storePassword=TU_CONTRASEÑA_DEL_KEYSTORE
keyAlias=upload
keyPassword=TU_CONTRASEÑA_DEL_ALIAS
```

### 4. Verificar SHA1 del Nuevo Keystore

Verifica que el SHA1 del nuevo keystore coincida con el que Google te proporcionó:

```bash
keytool -list -v -keystore milupa_nuevo.jks -alias upload -storepass TU_PASSWORD | grep SHA1
```

El SHA1 debe coincidir exactamente con el que Google te dio.

### 5. Construir el AAB Firmado

Una vez configurado el keystore correcto:

```bash
./gradlew bundleRelease
```

El AAB firmado estará en:
```
app/build/outputs/bundle/release/app-release.aab
```

### 6. Subir a Google Play

1. Ve a Google Play Console
2. Crea una nueva versión (32 - 2.2)
3. Sube el AAB: `app/build/outputs/bundle/release/app-release.aab`
4. Google debería aceptar el AAB con la nueva clave

## Notas Importantes

⚠️ **IMPORTANTE**: 
- Guarda el nuevo keystore (`milupa_nuevo.jks`) en un lugar seguro
- Guarda las contraseñas en un gestor de contraseñas
- **NO** subas el keystore a Git (ya está en .gitignore)
- Haz una copia de seguridad del keystore

📅 **Planificado para**: Viernes (subida de nueva versión)

## Archivos Preparados

- ✅ `app/build.gradle` - Configurado con signingConfigs
- ✅ Versión actualizada a 32 (2.2)
- ✅ `.gitignore` - Incluye *.jks y keystore.properties
- ✅ `keystore.properties.example` - Template para referencia

## Checklist para el Viernes

- [ ] Recibir nueva clave de firma de Google
- [ ] Crear nuevo keystore con la información de Google
- [ ] Verificar SHA1 del nuevo keystore
- [ ] Crear `keystore.properties` con las credenciales
- [ ] Construir AAB: `./gradlew bundleRelease`
- [ ] Verificar que el AAB se construyó correctamente
- [ ] Subir AAB a Google Play Console
- [ ] Verificar que Google acepta el AAB

