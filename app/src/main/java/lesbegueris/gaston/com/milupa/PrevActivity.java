package lesbegueris.gaston.com.milupa;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import lesbegueris.gaston.com.milupa.util.AppodealHelper;

/**
 * Created by gaston on 05/09/17.
 */

public class PrevActivity extends Activity {

    ImageView imageView3;
    ImageButton iBtnClose, iBtnShare, iBtnSave;
    Bitmap mImageBitmap;
    private String foto, foto1;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "photo";
    private File file;
    private WebView webView1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previ);

        // Initialize Appodeal
        String appodealAppKey = getString(R.string.appodeal_app_key);
        AppodealHelper.initialize(this, appodealAppKey);
        AppodealHelper.showBanner(this, R.id.appodealBannerView);

        foto1 = getIntent().getExtras().getString("foto", foto);

        //iBtnShare = (ImageButton) findViewById(R.id.iBtnSave);
        webView1 = (WebView) findViewById(R.id.webView1);
        webView1.getSettings().setBuiltInZoomControls(true);
        webView1.getSettings().setUseWideViewPort(true);
        webView1.getSettings().setLoadWithOverviewMode(true);
        webView1.getSettings().setSupportZoom(true);
        // webView1.setInitialScale(1);

        file = new File(foto1);

        //webView1.loadData(foto1, "text/html; charset=utf-8", null);
        webView1.loadUrl(foto1);
       // Toast.makeText(this, foto1,
         //       Toast.LENGTH_LONG).show();

        iBtnClose = (ImageButton) findViewById(R.id.iBtnClose);
        iBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show interstitial ad when closing activity
                AppodealHelper.showInterstitial(PrevActivity.this, new Runnable() {
                    @Override
                    public void run() {
                        Intent e = new Intent(PrevActivity.this, CamActivity.class);
                        startActivity(e);
                        finish();
                    }
                });
            }
        });

        iBtnShare = (ImageButton) findViewById(R.id.iBtnShare);
        iBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
    }

    private void shareImage(Uri imagePath) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        } else {
            sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, imagePath);
        startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
    }
    private void share(){
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        String imagePath = Environment.getExternalStorageDirectory()
                + foto1;
        File imageFileToShare = new File(foto1);
        Uri uri = Uri.parse(foto1.toString());
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Image!"));
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

    @Override
    public void onBackPressed() {
        // Show interstitial ad when closing activity
        AppodealHelper.showInterstitial(PrevActivity.this, new Runnable() {
            @Override
            public void run() {
                PrevActivity.super.onBackPressed();
            }
        });
    }
}

