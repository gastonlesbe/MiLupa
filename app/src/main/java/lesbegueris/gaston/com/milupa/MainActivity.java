package lesbegueris.gaston.com.milupa;

import android.*;
import android.app.Notification;
import android.app.NotificationChannel;
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
import lesbegueris.gaston.com.milupa.util.AdMobAppOpenHelper;

public class MainActivity extends AppCompatActivity {

    ImageButton btnGoCamera;

    private int counter = 0;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;
    private static final String PREFS_NAME = "MiLupaPrefs";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_WELCOME_SHOWN = "welcome_shown";
    private static final String KEY_APP_OPEN_SHOWN = "app_open_shown";
    private boolean isNavigating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startNotification();

        // Initialize Appodeal
        String appodealAppKey = getString(R.string.appodeal_app_key);
        AppodealHelper.initialize(this, appodealAppKey);
        AppodealHelper.showBanner(this, R.id.appodealBannerView);

        // Load App Open Ad early
        AdMobAppOpenHelper.loadAppOpenAd(this);

        btnGoCamera= (ImageButton)findViewById(R.id.btnGoCamera);
        btnGoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maybeShowWelcomeOrRequestPermissions();
            }
        });

        maybeShowWelcomeOrRequestPermissions();
    }

    private void maybeShowWelcomeOrRequestPermissions() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean(KEY_FIRST_LAUNCH, true);
        boolean welcomeShown = prefs.getBoolean(KEY_WELCOME_SHOWN, false);

        if (isFirstLaunch && !welcomeShown) {
            showWelcomeDialog();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkAndRequestPermissions()) {
                proceedAfterPermissions();
            }
        } else {
            // If below API level 23, permission check not required
            proceedAfterPermissions();
        }
    }

    private void proceedAfterPermissions() {
        if (isNavigating) {
            return;
        }
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean shouldShowAppOpen = !prefs.getBoolean(KEY_APP_OPEN_SHOWN, false);
        if (shouldShowAppOpen) {
            prefs.edit().putBoolean(KEY_APP_OPEN_SHOWN, true).apply();
            AdMobAppOpenHelper.showAppOpenAd(this, new Runnable() {
                @Override
                public void run() {
                    goToCamera();
                }
            });
        } else {
            goToCamera();
        }
    }

    private void goToCamera() {
        if (isNavigating) {
            return;
        }
        isNavigating = true;
        Intent a = new Intent(this, CamActivity.class);
        startActivity(a);
        finish();
    }

    private boolean checkAndRequestPermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
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
                boolean allPermissionsGranted = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }
                if (allPermissionsGranted && grantResults.length > 0) {
                    proceedAfterPermissions();
                }
                break;
        }
    }

    private void showWelcomeDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("¡Gracias por instalar MiLupa!\n\n" +
                "Esta aplicación se mantiene gracias a la publicidad que se mostrará. " +
                "Agradecemos tu comprensión y apoyo.\n\n" +
                "A continuación se solicitarán los permisos necesarios para usar la cámara.");
        builder.setPositiveButton("Entendido", (dialog, which) -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().putBoolean(KEY_WELCOME_SHOWN, true).putBoolean(KEY_FIRST_LAUNCH, false).apply();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkAndRequestPermissions()) {
                    proceedAfterPermissions();
                }
            } else {
                proceedAfterPermissions();
            }
        });
        builder.setCancelable(false);
        builder.show();
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
