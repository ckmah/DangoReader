package moe.dangoreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// Broadcast receiver for receiving status updates from the IntentService
public class DownloadReceiver extends BroadcastReceiver {
    // Prevents instantiation
    private DownloadStateReceiver() {
    }

    // Called when the BroadcastReceiver gets an Intent it's registered to receive
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadService.Constants.WORKING_ACTION)) {
            // do something
        }
    }
}