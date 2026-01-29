package lesbegueris.gaston.com.milupa;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.core.app.NotificationCompat;
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
        startNotification();

        // Initialize Appodeal
        String appodealAppKey = getString(R.string.appodeal_app_key);
        AppodealHelper.initialize(this, appodealAppKey);
        AppodealHelper.showBanner(this, R.id.appodealBannerView);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        // Proceed with permissions check
        if (checkAndRequestPermissions()) {
            // Permissions already granted, initialize camera directly
            initializeCamera();
        }

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setVisibility(View.INVISIBLE);

        imgFoto = (ImageButton)findViewById(R.id.imgFoto);
        imgFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera == null || parameters == null) {
                    return;
                }
                try {
                    if (safeToTakePicture) {
                        mCamera.takePicture(null, null, CamActivity.this);
                        safeToTakePicture = false;
                    }

                    if (isOn1) { // Turn off flash if it's on
                        iBtnFlash.setImageResource(R.mipmap.flashwhite);
                        parameters = mCamera.getParameters();
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(parameters);
                        isOn1 = false;
                    }
                } catch (Exception e) {
                    Log.e("TakePictureError", "Error taking picture: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        imgZoomIn= (ImageButton)findViewById(R.id.imgZoomIn);
        imgZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera == null || parameters == null) {
                    return;
                }
                try {
                    parameters = mCamera.getParameters();
                    if (!parameters.isZoomSupported()) {
                        return;
                    }
                    final int maxZoomLevel = parameters.getMaxZoom();
                    Log.i("max ZOOM ", "is " + maxZoomLevel);
                    if (currentZoomLevel < maxZoomLevel) {
                        currentZoomLevel++;
                        mCamera.stopPreview();
                        parameters.setZoom(currentZoomLevel);
                        mCamera.setParameters(parameters);
                        mCamera.startPreview();
                        mPreview.setFocusable(true);
                        if (Build.VERSION.SDK_INT > 23) {
                            parameters = mCamera.getParameters();
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                            mCamera.setParameters(parameters);
                        }
                    }
                } catch (Exception e) {
                    Log.e("ZoomInError", "Error zooming in: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        imgZoomOut=(ImageButton)findViewById(R.id.imgZoomOut);
        imgZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera == null || parameters == null) {
                    return;
                }
                try {
                    if (currentZoomLevel > 0) {
                        currentZoomLevel--;
                        mCamera.stopPreview();
                        parameters = mCamera.getParameters();
                        parameters.setZoom(currentZoomLevel);
                        mCamera.setParameters(parameters);
                        mCamera.startPreview();
                        mPreview.setFocusable(true);
                        if (Build.VERSION.SDK_INT > 23) {
                            parameters = mCamera.getParameters();
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                            mCamera.setParameters(parameters);
                        }
                    }
                } catch (Exception e) {
                    Log.e("ZoomOutError", "Error zooming out: " + e.getMessage());
                    e.printStackTrace();
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
        if (Build.VERSION.SDK_INT > 23 && mCamera != null) {
            try {
                parameters = mCamera.getParameters();
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                Log.e("CameraError", "Error setting focus mode: " + e.getMessage());
            }
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (mCamera == null) {
                initializeCamera();
            }
        }
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
                iBtnFlash.setImageResource(R.mipmap.flashwhite);
                Toast.makeText(getApplicationContext(), "Light Off", Toast.LENGTH_LONG).show();
                parameters = mCamera.getParameters();
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
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
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
                boolean allPermissionsGranted = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }
                if (allPermissionsGranted && grantResults.length > 0) {
                    // All permissions granted, initialize camera
                    initializeCamera();
                } else {
                    // Permissions denied, show message and finish
                    Toast.makeText(this, "Camera permission is required to use this app", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    private void initializeCamera() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                if (mCamera != null) {
                    mCamera.setDisplayOrientation(90);
                    parameters = mCamera.getParameters();
                    parameters.setRotation(90);
                    mCamera.setParameters(parameters);
                    isFlash1 = true;
                }
            } catch (Exception e) {
                // Handle camera initialization failure
                Log.e("CameraError", "Failed to initialize camera: " + e.getMessage());
                Toast.makeText(this, "Failed to open camera: " + e.getMessage(), Toast.LENGTH_LONG).show();
                finish(); // Or handle the error differently
                return; // Exit early if camera failed to initialize
            }
        }

        // Only proceed if camera was successfully initialized
        if (mCamera != null) {
            try {
                parameters = mCamera.getParameters();
                parameters.setRotation(90);
                mCamera.setDisplayOrientation(90);
                isFlash1 = true;
            } catch (Exception e) {
                Log.e("CameraError", "Error setting camera parameters: " + e.getMessage());
            }
        }
    }

    public void turnOn() {
        if (isFlash1 && mCamera != null) { // Added null check for mCamera
            try {
                if (!isOn1) {
                    // Activate flash
                    iBtnFlash.setImageResource(R.mipmap.flashoutwhite);
                    iBtnFlash.refreshDrawableState();
                    parameters = mCamera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCamera.stopPreview(); // Stop preview before changing parameters
                    mCamera.setParameters(parameters);
                    mCamera.startPreview(); // Restart preview after applying parameters
                    isOn1 = true;
                    Log.d("Flash", "Flash turned ON, icon changed to flashoutwhite, isOn1=" + isOn1);
                } else {
                    // Deactivate flash - return to original state
                    iBtnFlash.setImageResource(R.mipmap.flashwhite);
                    iBtnFlash.refreshDrawableState();
                    parameters = mCamera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.stopPreview(); // Stop preview
                    mCamera.setParameters(parameters);
                    mCamera.startPreview(); // Restart preview
                    isOn1 = false;
                    Log.d("Flash", "Flash turned OFF, icon changed to flashwhite, isOn1=" + isOn1);
                }
            } catch (Exception e) {
                Log.e("FlashError", "Error toggling flash: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.w("Flash", "Cannot toggle flash: isFlash1=" + isFlash1 + ", mCamera=" + (mCamera != null));
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Check camera permission before opening camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.w("CameraError", "Camera permission not granted in surfaceChanged");
            return;
        }

        if (mCamera == null) {
            Log.w("CameraError", "Camera not initialized yet in surfaceChanged");
            return;
        }

        try {
            Camera.Parameters parameters = mCamera.getParameters();
            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            if (sizes != null && !sizes.isEmpty()) {
                Camera.Size selected = sizes.get(0);
                parameters.setPreviewSize(selected.width, selected.height);
            }

            mCamera.setParameters(parameters);
            if (Build.VERSION.SDK_INT > 23) {
                parameters = mCamera.getParameters();
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                mCamera.setParameters(parameters);
            }

            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
            safeToTakePicture = true;

            mCamera.setPreviewDisplay(mPreview.getHolder());
            mCamera.startPreview();
        } catch (Exception e) {
            Log.e("CameraError", "Error in surfaceChanged: " + e.getMessage());
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mCamera == null) {
            return;
        }
        try {
            SurfaceTexture surfaceTexture = new SurfaceTexture(10);
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.setPreviewDisplay(mPreview.getHolder());
            mCamera.setDisplayOrientation(90);
            setCameraDisplayOrientation(CamActivity.this, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
        } catch (Exception e) {
            Log.e("CameraError", "Error in surfaceCreated: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (mCamera == null) {
            return;
        }
        if (safeToTakePicture) {
            try {
                mCamera.takePicture(null, null, CamActivity.this);
                safeToTakePicture = false;
            } catch (Exception e) {
                Log.e("CameraError", "Error taking picture: " + e.getMessage());
            }
        }

        if (isOn1 && mCamera != null) { // Turn off flash if it's on
            try {
                iBtnFlash.setImageResource(R.mipmap.flashwhite);
                iBtnFlash.refreshDrawableState();
                parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
                isOn1 = false;
            } catch (Exception e) {
                Log.e("CameraError", "Error turning off flash: " + e.getMessage());
            }
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
        if (mCamera == null || parameters == null) {
            return super.onKeyDown(keyCode, event);
        }
        
        try {
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
                            parameters = mCamera.getParameters();
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                            mCamera.setParameters(parameters);
                        }
                    }
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    if (currentZoomLevel > 0) {
                        currentZoomLevel--;
                        mCamera.stopPreview(); // Stop preview
                        parameters = mCamera.getParameters();
                        parameters.setZoom(currentZoomLevel);
                        mCamera.setParameters(parameters);
                        mCamera.startPreview(); // Restart preview
                        mPreview.setFocusable(true);
                        if (Build.VERSION.SDK_INT > 23) {
                            parameters = mCamera.getParameters();
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                            mCamera.setParameters(parameters);
                        }
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e("CameraError", "Error in onKeyDown: " + e.getMessage());
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Handled by onBackPressed()
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    public void startNotification(){
        // Create notification channel for Android 8.0+ (API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "milupa_notification_channel";
            String channelName = "MiLupa Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent intent1 = new Intent(this, CamActivity.class);
        PendingIntent pIntent1 = PendingIntent.getActivity(this, (int)
                System.currentTimeMillis(), intent1, PendingIntent.FLAG_IMMUTABLE);

        CharSequence titulo = getText(R.string.app_name);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? "milupa_notification_channel" : "";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentText(titulo)
                .setSmallIcon(R.drawable.ic_noti)
                .setContentIntent(pIntent1)
                .setOngoing(false);
        
        Notification notification = builder.build();

        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNotificationManager != null) {
            mNotificationManager.notify(33, notification);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
}
