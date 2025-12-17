# How to Copy Drawable and Mipmap Resources

The drawable and mipmap folders are in the backup project at:
`/home/gaston/StudioProjects/MiLupa1/app/src/main/res/`

## Option 1: Copy via Android Studio (Easiest)

1. In Android Studio, open both projects:
   - Open `/home/gaston/StudioProjects/MiLupa1` 
   - Open `/home/gaston/StudioProjects/MiLupa` (current project)

2. In the MiLupa1 project, navigate to:
   - `app/src/main/res/`

3. Copy these folders:
   - `drawable`
   - `drawable-hdpi`
   - `drawable-mdpi`
   - `drawable-xhdpi`
   - `drawable-xxhdpi`
   - `mipmap-hdpi`
   - `mipmap-mdpi`
   - `mipmap-xhdpi`
   - `mipmap-xxhdpi`
   - `mipmap-xxxhdpi`

4. Paste them into:
   - `MiLupa/app/src/main/res/`

## Option 2: Copy via File Manager

1. Open your file manager
2. Navigate to `/home/gaston/StudioProjects/MiLupa1/app/src/main/res/`
3. Select all `drawable-*` and `mipmap-*` folders
4. Copy them
5. Navigate to `/home/gaston/StudioProjects/MiLupa/app/src/main/res/`
6. Paste them there

## Option 3: Copy via Terminal

Run these commands in the terminal:

```bash
cd /home/gaston/StudioProjects/MiLupa1/app/src/main/res
cp -r drawable* /home/gaston/StudioProjects/MiLupa/app/src/main/res/
cp -r mipmap* /home/gaston/StudioProjects/MiLupa/app/src/main/res/
```

Then verify:
```bash
ls -d /home/gaston/StudioProjects/MiLupa/app/src/main/res/drawable*
ls -d /home/gaston/StudioProjects/MiLupa/app/src/main/res/mipmap*
```

## After Copying

1. In Android Studio, right-click on the `res` folder in MiLupa project
2. Select "Synchronize" or "Refresh"
3. Uncomment the image resource lines in `CamActivity.java`:
   - Search for `// iBtnFlash.setImageResource(R.mipmap.flashwhite);`
   - Remove the `//` comments to restore the icon functionality

