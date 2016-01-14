package ckmah.mangoreader;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Pass the boot notification onto the refresh service, to tell it to restart
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent intent2 = new Intent(context, RefreshService.class);
            context.startService(intent2.setAction(RefreshService.BOOT));
        }
    }

    // WIFI CODE
//    ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//
//    if (mWifi.isConnected()) {
//        // Do whatever
//    }

    public static class RefreshService extends IntentService {
        public RefreshService() {
            super("RefreshService");
        }

        private static String KEY_CYCLING = "ckmah.mangoreader.cycling";
        private static String KEY_STARTED = "ckmah.mangoreader.started";

        // Helper functions
        private boolean isCycling() {
            return getSharedPreferences(KEY_CYCLING, 0).getBoolean(KEY_CYCLING, false);
        }

        private void setCycling(boolean value) {
            getSharedPreferences(KEY_CYCLING, 0).edit().putBoolean(KEY_CYCLING, value).commit();
        }

        public static boolean isStarted(Context context) {
            return context.getSharedPreferences(KEY_STARTED, 0).getBoolean(KEY_STARTED, false);
        }

        public static void setStarted(Context context, boolean value) {
            context.getSharedPreferences(KEY_STARTED, 0).edit().putBoolean(KEY_STARTED, value).commit();
        }

        /**
         * Toggle between polling and not polling
         */
        public static void toggle(Context context) {
            Intent intent = new Intent(context, RefreshService.class);
            context.startService(intent.setAction(RefreshService.TOGGLE));
        }

        private final static String TOGGLE = "ckmah.mangoreader.TOGGLE";
        private final static String BOOT = "ckmah.mangoreader.BOOT";
        private final static String UPDATE = "ckmah.mangoreader.UPDATE";

        @Override
        protected void onHandleIntent(Intent intent) {
            Intent intent2 = new Intent(this, UpdateService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent2, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

//            long period = SettingsActivity.getRefreshSeconds(this) * 1000;
            long period = AlarmManager.INTERVAL_HOUR; // TODO make configurable
            alarmManager.cancel(pendingIntent); //Cancels any past refresh

            if (intent.getAction().equals(BOOT)) {
                // Device was just started
                if (isCycling()) {
                    // Start polling in half a period, and repeat every period
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                            SystemClock.elapsedRealtime() + period / 2, period, pendingIntent);
                }
            } else if (intent.getAction().equals(TOGGLE)) {
                // Toggle between polling and not polling
                setCycling(!isCycling());

                if (isCycling()) {
                    // Start polling now, and repeat every period
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, 0, period, pendingIntent);
                } else {
                    // Stop by doing nothing
                }
            } else if (intent.getAction().equals(UPDATE)) {
                // Update the period between polls
                if (isCycling()) {
                    // Start polling in a period, and repeat every period
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                            SystemClock.elapsedRealtime() + period, period, pendingIntent);
                }
            }

        }
    }
}