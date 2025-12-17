package lesbegueris.gaston.com.milupa;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Service.START_NOT_STICKY;

import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;

import lesbegueris.gaston.com.milupa.util.AppodealHelper;

/**
 * Created by gaston on 13/08/17.
 */

public class CamActivity extends Activity implements SurfaceHolder.Callback, Camera.PictureCallback, View.OnClickListener {

    private Camera mCamera;
    private SurfaceView mPreview;
    private ImageButton iBtnFlash, imgFoto;
    private ImageView imageView;
    private Camera.Parameters parameters;
    private boolean isFlashOn = false;
    private CameraManager cameraManager;
    private CameraCaptureSession captureSession;
    private CaptureRequest.Builder previewRequestBuilder;
    private ImageReader imageReader;

    ImageButton  iBtnSave, iBtnClose, iBtnCam, iBtnZoomIn, iBtnZoomOut, imgBtnHelp;
    ImageView  imgBtnOpen, imgZoomIn, imgZoomOut;

    boolean isOn1 = false;
    boolean isFlash1 = false;
    boolean videoOn = false;
    int currentZoomLevel = 0;
    int currentCameraId = 0;
    Layout layout2;

    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;
    private Uri imageFileUri;
    Bitmap mImageBitmap;

    private File temp;
    ImageView Imageview3;
    private static final int VIDEO_CAPTURE = 101;

    private int counter = 0;
    private File dest;
    private String tempName;

    private final static String DEBUG_TAG = "CamActivity";
    private Camera camera;
    private int cameraId = 0;
    private String foto;
    private Uri file;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap bm;
    private boolean safeToTakePicture = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);

        // Initialize Appodeal
        String appodealAppKey = getString(R.string.appodeal_app_key);
        AppodealHelper.initialize(this, appodealAppKey);
        AppodealHelper.showBanner(this, R.id.appodealBannerView);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                mCamera.setDisplayOrientation(90);
                parameters = mCamera.getParameters();
                parameters.setRotation(90);
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                // Handle camera initialization failure
                Log.e("CameraError", "Failed to initialize camera: " + e.getMessage());
                // Display error message to user
                finish(); // Or handle the error differently
            }
        }

        parameters = mCamera.getParameters();
        parameters.setRotation(90);
        mCamera.setDisplayOrientation(90);
        isFlash1 = true;

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setVisibility(View.INVISIBLE);

        imgFoto = (ImageButton)findViewById(R.id.imgFoto);
        imgFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (safeToTakePicture) {
                    mCamera.takePicture(null, null, CamActivity.this);
                    safeToTakePicture = false;
                }

                if (isOn1 = true) {
                    iBtnFlash.setImageResource(R.mipmap.flashwhite);
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(parameters);
                    isOn1 = false;
                }
            }
        });
        imgZoomIn= (ImageButton)findViewById(R.id.imgZoomIn);
        imgZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int maxZoomLevel = parameters.getMaxZoom();
                Log.i("max ZOOM ", "is " + maxZoomLevel);
                if (currentZoomLevel < maxZoomLevel) {
                    currentZoomLevel++;
                    mCamera.startSmoothZoom(currentZoomLevel);
                    parameters.setZoom(currentZoomLevel);
                    mCamera.setParameters(parameters);
                    mPreview.setFocusable(true);
                    if (Build.VERSION.SDK_INT > 23) {
                        mCamera.getParameters();
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        mCamera.setParameters(parameters);
                    }
                }
            }
        });

        imgZoomOut=(ImageButton)findViewById(R.id.imgZoomOut);
        imgZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentZoomLevel > 0) {
                    currentZoomLevel--;
                    parameters.setZoom(currentZoomLevel);
                    mCamera.setParameters(parameters);
                    mPreview.setFocusable(true);
                    if (Build.VERSION.SDK_INT > 23) {
                        mCamera.getParameters();
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        mCamera.setParameters(parameters);
                    }
                }
            }
        });

        iBtnFlash = (ImageButton) findViewById(R.id.iBtnFlash);
        iBtnFlash.setRotation(-90);
        iBtnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOn();
            }
        });

        mPreview = (SurfaceView) findViewById(R.id.preview);
        mPreview.getHolder().addCallback(this);
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mPreview.setFocusable(true);
        mPreview.setClickable(true);
        mPreview.setOnClickListener(this);
        if (Build.VERSION.SDK_INT > 23) {
            mCamera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(parameters);
        }
    }

    private void help() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.TocaPantallaFoto);
        dialog.setMessage(R.string.zoom);

        dialog.setNegativeButton("OK",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCameraAndPreview();
        // Hide banner when activity pauses
        AppodealHelper.hideBanner(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseCameraAndPreview();
        // Hide banner when activity is destroyed
        AppodealHelper.hideBanner(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Show banner when activity resumes
        String appodealAppKey = getString(R.string.appodeal_app_key);
        AppodealHelper.initialize(this, appodealAppKey);
        AppodealHelper.showBanner(this, R.id.appodealBannerView);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isFlash1) {
            if (!isOn1) {
                iBtnFlash.setImageResource(R.mipmap.flashoutwhite);
                Toast.makeText(getApplicationContext(), "Light On", Toast.LENGTH_LONG).show();
                mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                if (Build.VERSION.SDK_INT > 23) {
                    mCamera.setParameters(parameters);
                }
                mCamera.startPreview();
                isOn1 = true;
            } else {
                // iBtnFlash.setImageResource(R.mipmap.flashwhite); // TODO: Copy mipmap resources
                Toast.makeText(getApplicationContext(), "Light Off", Toast.LENGTH_LONG).show();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                if (Build.VERSION.SDK_INT > 23) {
                    mCamera.setParameters(parameters);
                    mCamera.stopPreview();
                }
                isOn1 = false;
            }
        }
        return START_NOT_STICKY;
    }

    private boolean checkAndRequestPermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);

        int storagePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        int storagePermission1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (storagePermission1 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted Successfully. Write working code here.
                } else {
                    //You did not accept the request can not use the functionality.
                }
                break;
        }
    }

    public void turnOn() {
        if (isFlash1 && mCamera != null) { // Added null check for mCamera
            if (!isOn1) {
                iBtnFlash.setImageResource(R.mipmap.flashoutwhite);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.stopPreview(); // Stop preview before changing parameters
                mCamera.setParameters(parameters);
                mCamera.startPreview(); // Restart preview after applying parameters
                isOn1 = true;
            } else {
                // iBtnFlash.setImageResource(R.mipmap.flashwhite); // TODO: Copy mipmap resources
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.stopPreview(); // Stop preview
                mCamera.setParameters(parameters);
                mCamera.startPreview(); // Restart preview
                isOn1 = false;
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            mCamera = android.hardware.Camera.open();
        }catch (RuntimeException ex){}

        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size selected = sizes.get(0);
        parameters.setPreviewSize(selected.width, selected.height);

        mCamera.setParameters(parameters);
        if (Build.VERSION.SDK_INT > 23) {
            mCamera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(parameters);
        }

        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
        safeToTakePicture = true;

        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
            mCamera.startPreview();
        }catch (Exception e){
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            SurfaceTexture surfaceTexture = new SurfaceTexture(10);
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.setPreviewDisplay(mPreview.getHolder());
            mCamera.setDisplayOrientation(90);
            setCameraDisplayOrientation(CamActivity.this, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (safeToTakePicture) {
            mCamera.takePicture(null, null, CamActivity.this);
            safeToTakePicture = false;
        }

        if (isOn1) { // Turn off flash if it's on
            // iBtnFlash.setImageResource(R.mipmap.flashwhite); // TODO: Copy mipmap resources
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
            isOn1 = false;
        }
    }

    public void onPictureTaken(byte[] data, Camera camera) {
        mCamera.stopPreview();

        Uri imageFileUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());

        try {
            OutputStream imageFileOS = getContentResolver().openOutputStream(imageFileUri);
            imageFileOS.write(data);
            imageFileOS.flush();
            imageFileOS.close();
            foto = imageFileUri.toString();

            MediaActionSound sound = new MediaActionSound();
            sound.play(MediaActionSound.SHUTTER_CLICK);
            mCamera.setPreviewDisplay(mPreview.getHolder());

            Toast t = Toast.makeText(this, "Click", Toast.LENGTH_SHORT);
            t.show();
            imageView.setVisibility(View.VISIBLE);

            Bitmap bitmap = getThumbnail(imageFileUri);
            imageView.setImageBitmap(bitmap);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    irPrev();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        //finished saving picture
        safeToTakePicture = true;
        mCamera.startPreview();
    }

    public void irPrev(){
        Intent i = new Intent(this, PrevActivity.class);
        i.putExtra("foto", foto);
        startActivity(i);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException{
        InputStream input = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        int THUMBNAIL_SIZE = 50;
        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (parameters.isZoomSupported()) {
            final int maxZoomLevel = parameters.getMaxZoom();
            Log.i("max ZOOM ", "is " + maxZoomLevel);

            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                if (currentZoomLevel < maxZoomLevel) {
                    currentZoomLevel++;
                    mCamera.stopPreview(); // Stop preview before changing parameters
                    parameters.setZoom(currentZoomLevel);
                    mCamera.setParameters(parameters);
                    mCamera.startPreview(); // Restart preview after applying parameters
                    mPreview.setFocusable(true);
                    if (Build.VERSION.SDK_INT > 23) {
                        mCamera.getParameters();
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        mCamera.setParameters(parameters);
                    }
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                if (currentZoomLevel > 0) {
                    currentZoomLevel--;
                    mCamera.stopPreview(); // Stop preview
                    parameters.setZoom(currentZoomLevel);
                    mCamera.setParameters(parameters);
                    mCamera.startPreview(); // Restart preview
                    mPreview.setFocusable(true);
                    if (Build.VERSION.SDK_INT > 23) {
                        mCamera.getParameters();
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        mCamera.setParameters(parameters);
                    }
                }
            }
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Handled by onBackPressed()
            return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        // Show interstitial ad when closing activity
        AppodealHelper.showInterstitial(CamActivity.this, new Runnable() {
            @Override
            public void run() {
                CamActivity.super.onBackPressed();
            }
        });
    }

    private void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
}

