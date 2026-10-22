package lesbegueris.gaston.com.milupa.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Google Play In-App Review, shown only after a positive moment.
 *
 * Rules:
 *  - A "successful use" is a magnifier session where the user actually zoomed in
 *    or captured a photo. Only one successful use is counted per calendar day.
 *  - The review flow is eligible once there are at least MIN_USE_DAYS distinct days
 *    of successful use, and at most once every COOLDOWN_MS (~60 days).
 *  - It is only requested at the end of a task (photo saved / leaving the magnifier),
 *    never at launch. No custom pre-dialog: Play's own flow is launched directly.
 *  - Any error is swallowed; the caller's continuation always runs.
 */
public class ReviewHelper {
    private static final String TAG = "ReviewHelper";
    private static final String PREFS_NAME = "in_app_review";
    private static final String KEY_USE_DAYS = "use_days";
    private static final String KEY_LAST_USE_DAY = "last_use_day";
    private static final String KEY_LAST_PROMPT_MS = "last_prompt_ms";

    private static final int MIN_USE_DAYS = 3;
    private static final long COOLDOWN_MS = 60L * 24L * 60L * 60L * 1000L;

    /** Records a successful magnifier use. Counted at most once per calendar day. */
    public static void recordSuccessfulUse(Context context) {
        try {
            SharedPreferences prefs = prefs(context);
            String today = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
            if (today.equals(prefs.getString(KEY_LAST_USE_DAY, null))) {
                return;
            }
            prefs.edit()
                    .putString(KEY_LAST_USE_DAY, today)
                    .putInt(KEY_USE_DAYS, prefs.getInt(KEY_USE_DAYS, 0) + 1)
                    .apply();
        } catch (Exception e) {
            Log.w(TAG, "recordSuccessfulUse failed", e);
        }
    }

    /** True if the user has enough successful-use days and the cooldown has elapsed. */
    public static boolean isEligible(Context context) {
        try {
            SharedPreferences prefs = prefs(context);
            if (prefs.getInt(KEY_USE_DAYS, 0) < MIN_USE_DAYS) {
                return false;
            }
            long lastPrompt = prefs.getLong(KEY_LAST_PROMPT_MS, 0L);
            long now = System.currentTimeMillis();
            // now < lastPrompt guards against the clock being set back
            return lastPrompt == 0L || now < lastPrompt || now - lastPrompt >= COOLDOWN_MS;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Launches the Play review flow if eligible. {@code onDone} (nullable) is always
     * run exactly once on the main thread: after the flow finishes, on any error,
     * or immediately if not eligible.
     */
    public static void maybeRequestReview(final Activity activity, final Runnable onDone) {
        final Runnable done = new Runnable() {
            private boolean ran = false;
            @Override
            public void run() {
                if (ran) return;
                ran = true;
                if (onDone != null) onDone.run();
            }
        };

        if (activity == null || activity.isFinishing() || activity.isDestroyed() || !isEligible(activity)) {
            done.run();
            return;
        }

        try {
            final ReviewManager manager = ReviewManagerFactory.create(activity);
            manager.requestReviewFlow().addOnCompleteListener(task -> {
                try {
                    if (!task.isSuccessful() || activity.isFinishing() || activity.isDestroyed()) {
                        done.run();
                        return;
                    }
                    ReviewInfo reviewInfo = task.getResult();
                    prefs(activity).edit().putLong(KEY_LAST_PROMPT_MS, System.currentTimeMillis()).apply();
                    manager.launchReviewFlow(activity, reviewInfo)
                            .addOnCompleteListener(launchTask -> done.run());
                } catch (Exception e) {
                    Log.w(TAG, "launchReviewFlow failed", e);
                    done.run();
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "requestReviewFlow failed", e);
            done.run();
        }
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
