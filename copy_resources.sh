#!/bin/bash
# Script to copy drawable and mipmap resources from backup

SOURCE="/home/gaston/StudioProjects/MiLupa1/app/src/main/res"
DEST="/home/gaston/StudioProjects/MiLupa/app/src/main/res"

echo "Copying drawable resources..."
for dir in "$SOURCE"/drawable*; do
    if [ -d "$dir" ]; then
        dirname=$(basename "$dir")
        echo "Copying $dirname..."
        mkdir -p "$DEST/$dirname"
        cp -r "$dir"/* "$DEST/$dirname/" 2>/dev/null
    fi
done

echo "Copying mipmap resources..."
for dir in "$SOURCE"/mipmap*; do
    if [ -d "$dir" ]; then
        dirname=$(basename "$dir")
        echo "Copying $dirname..."
        mkdir -p "$DEST/$dirname"
        cp -r "$dir"/* "$DEST/$dirname/" 2>/dev/null
    fi
done

echo "Done! Checking results..."
ls -d "$DEST"/drawable* 2>/dev/null | wc -l
ls -d "$DEST"/mipmap* 2>/dev/null | wc -l

