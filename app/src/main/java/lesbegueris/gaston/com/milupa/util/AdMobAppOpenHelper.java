package lesbegueris.gaston.com.milupa.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

/**
 * Helper class for AdMob App Open Ads integration
 */
public class AdMobAppOpenHelper {
    private static final String TAG = "AdMobAppOpenHelper";
    
    // App Open Ad Unit ID for MiLupa
    // Ad unit ID lives in res/values/ads.xml (production); src/debug/res/values/ads.xml
    // overrides it with Google's sample ID for debug builds.
    
    private static AppOpenAd appOpenAd = null;
    private static boolean isLoadingAd = false;
    private static Runnable pendingCallback;

    /**
     * Load app open ad
     * Call this early in the activity lifecycle
     */
    public static void loadAppOpenAd(Activity activity) {
        // Don't load if already loading or loaded
        if (isLoadingAd || appOpenAd != null) {
            return;
        }
        
        isLoadingAd = true;
        AdRequest request = new AdRequest.Builder().build();
        
        AppOpenAd.load(activity, activity.getString(lesbegueris.gaston.com.milupa.R.string.admob_app_open_id), request,
            new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdLoaded(AppOpenAd ad) {
                    appOpenAd = ad;
                    isLoadingAd = false;
                    Log.d(TAG, "App Open ad loaded");
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    isLoadingAd = false;
                    appOpenAd = null;
                    Log.e(TAG, "App Open ad failed to load: " + loadAdError.getMessage());
                }
            });
    }

    /**
     * Show app open ad with callback
     * @param activity The activity where ad will be shown
     * @param onAdClosed Runnable to execute when ad is closed or if ad is not available
     */
    public static void showAppOpenAd(Activity activity, Runnable onAdClosed) {
        if (appOpenAd != null) {
            // Store the callback
            pendingCallback = onAdClosed;
            
            // Set callback for when ad is closed
            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    Log.d(TAG, "App Open ad dismissed");
                    appOpenAd = null;
                    
                    // Execute callback if provided
                    if (pendingCallback != null) {
                        Runnable callback = pendingCallback;
                        pendingCallback = null;
                        callback.run();
                    }
                    
                    // Reload ad for next time
                    loadAppOpenAd(activity);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    Log.e(TAG, "App Open ad failed to show: " + adError.getMessage());
                    appOpenAd = null;
                    
                    // Execute callback if provided
                    if (pendingCallback != null) {
                        Runnable callback = pendingCallback;
                        pendingCallback = null;
                        callback.run();
                    }
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    Log.d(TAG, "App Open ad showed");
                }
            });
            
            // Show the ad
            appOpenAd.show(activity);
            Log.d(TAG, "App Open ad shown");
        } else {
            Log.d(TAG, "App Open ad not loaded yet, executing callback immediately");
            if (onAdClosed != null) {
                onAdClosed.run();
            }
            // Try to load ad for next time
            loadAppOpenAd(activity);
        }
    }

    /**
     * Show app open ad (simple version without callback)
     */
    public static void showAppOpenAd(Activity activity) {
        showAppOpenAd(activity, null);
    }

    /**
     * Check if app open ad is loaded
     */
    public static boolean isAppOpenAdLoaded() {
        return appOpenAd != null;
    }
}
