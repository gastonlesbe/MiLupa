package lesbegueris.gaston.com.milupa;

import android.*;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import lesbegueris.gaston.com.milupa.util.AppodealHelper;

public class MainActivity extends AppCompatActivity {

    ImageButton btnGoCamera;

    private int counter = 0;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startNotification();

        // Initialize Appodeal
        String appodealAppKey = getString(R.string.appodeal_app_key);
        AppodealHelper.initialize(this, appodealAppKey);
        AppodealHelper.showBanner(this, R.id.appodealBannerView);

        btnGoCamera= (ImageButton)findViewById(R.id.btnGoCamera);
        btnGoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irCamera();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();
        } else {
            // If below API level 23, permission check not required
            irCamera();
        }

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 50);
    }

    private void irCamera() {
        Intent a = new Intent(this, CamActivity.class);
        startActivity(a);
    }

    private boolean checkAndRequestPermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA);

        int storagePermission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);

        int storagePermission1 = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (storagePermission1 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted Successfully. Write working code here.
                    Intent e = new Intent(this, CamActivity.class);
                    startActivity(e);
                } else {
                    //You did not accept the request can not use the functionality.
                }
                break;
        }
    }

    public void startNotification(){
        Intent intent1 = new Intent(this, CamActivity.class);
        PendingIntent pIntent1 = PendingIntent.getActivity(this, (int)
                System.currentTimeMillis(), intent1, PendingIntent.FLAG_IMMUTABLE);

        CharSequence titulo = getText(R.string.app_name);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentText(titulo)
                .setSmallIcon(R.drawable.ic_noti)
                .setContentIntent(pIntent1)
                .setOngoing(false)
                .build();

        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(33, notification);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Show banner when activity resumes
        String appodealAppKey = getString(R.string.appodeal_app_key);
        AppodealHelper.initialize(this, appodealAppKey);
        AppodealHelper.showBanner(this, R.id.appodealBannerView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Hide banner when activity pauses
        AppodealHelper.hideBanner(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hide banner when activity is destroyed
        AppodealHelper.hideBanner(this);
    }
}
