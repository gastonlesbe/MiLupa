#!/bin/bash
cd /home/gaston/StudioProjects/MiLupa

echo "Convirtiendo upload_cert.der a PEM..."
openssl x509 -inform DER -in upload_cert.der -out upload_certificate.pem

if [ $? -eq 0 ]; then
    echo "✅ Certificado convertido exitosamente: upload_certificate.pem"
    echo ""
    echo "Información del certificado:"
    openssl x509 -in upload_certificate.pem -noout -subject -issuer -dates
    echo ""
    echo "SHA1 Fingerprint:"
    openssl x509 -in upload_certificate.pem -noout -fingerprint -sha1
    echo ""
    echo "Archivo creado:"
    ls -lh upload_certificate.pem
else
    echo "❌ Error al convertir el certificado"
fi

