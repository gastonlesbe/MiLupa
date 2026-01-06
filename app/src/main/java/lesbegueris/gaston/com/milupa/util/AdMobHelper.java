package lesbegueris.gaston.com.milupa.util;

import android.app.Activity;
import android.util.Log;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

/**
 * Helper class for AdMob Interstitial ads integration
 */
public class AdMobHelper {
    private static final String TAG = "AdMobHelper";
    
    // Interstitial Ad Unit ID for MiLupa
    private static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-9841764898906750/5507215775";
    
    private static InterstitialAd mInterstitialAd;
    private static Runnable pendingCallback;

    /**
     * Load interstitial ad
     * Call this early in the activity lifecycle (e.g., onCreate or onResume)
     */
    public static void loadInterstitial(Activity activity) {
        // Don't load if already loading or loaded
        if (mInterstitialAd != null) {
            return;
        }
        
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(activity, INTERSTITIAL_AD_UNIT_ID, adRequest,
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;
                    Log.d(TAG, "Interstitial ad loaded");
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    Log.e(TAG, "Interstitial ad failed to load: " + loadAdError.getMessage());
                    mInterstitialAd = null;
                }
            });
    }

    /**
     * Show interstitial ad with callback
     * @param activity The activity where interstitial will be shown
     * @param onAdClosed Runnable to execute when ad is closed or if ad is not available
     */
    public static void showInterstitial(Activity activity, Runnable onAdClosed) {
        if (mInterstitialAd != null) {
            // Store the callback
            pendingCallback = onAdClosed;
            
            // Set callback for when ad is closed
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad dismissed");
                    mInterstitialAd = null;
                    
                    // Execute callback if provided
                    if (pendingCallback != null) {
                        Runnable callback = pendingCallback;
                        pendingCallback = null;
                        callback.run();
                    }
                    
                    // Reload ad for next time
                    loadInterstitial(activity);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                    Log.e(TAG, "Interstitial ad failed to show: " + adError.getMessage());
                    mInterstitialAd = null;
                    
                    // Execute callback if provided
                    if (pendingCallback != null) {
                        Runnable callback = pendingCallback;
                        pendingCallback = null;
                        callback.run();
                    }
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad showed");
                }
            });
            
            // Show the ad
            mInterstitialAd.show(activity);
            Log.d(TAG, "Interstitial ad shown");
        } else {
            Log.d(TAG, "Interstitial ad not loaded yet, executing callback immediately");
            if (onAdClosed != null) {
                onAdClosed.run();
            }
            // Try to load ad for next time
            loadInterstitial(activity);
        }
    }

    /**
     * Show interstitial ad (simple version without callback)
     */
    public static void showInterstitial(Activity activity) {
        showInterstitial(activity, null);
    }

    /**
     * Check if interstitial is loaded
     */
    public static boolean isInterstitialLoaded() {
        return mInterstitialAd != null;
    }
}

