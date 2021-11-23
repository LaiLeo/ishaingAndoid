package com.taiwanmobile.volunteers.v2.gcm;

import org.apache.commons.lang3.StringUtils;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.api.PushMessageResponseTask;

public class GcmIntentService extends IntentService {
	private static final String TAG = GcmIntentService.class.getSimpleName();

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you
		// received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);
		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				if (!StringUtils.isBlank(extras.getString("id"))) {
					Log.e(TAG, "APSN id " + extras.getString("id"));

					new PushMessageResponseTask(this, extras.getString("id"))
							.execute();
				}

				sendNotification(extras);
				Log.e(TAG, "Received: " + extras.toString());
			} else {
				Log.e("test", messageType);
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	public void sendNotification(Bundle extras) {

        String page_type = extras.getString("page_type", "");
        String event_id = extras.getString("event_id");
        String content = extras.getString("content");
        String message = extras.getString("message");
        if(page_type.equals(GCMUtils.PAGE_TYPE_5180_DETAIL) || page_type.equals(GCMUtils.PAGE_TYPE_5180_LIST)){
            event_id = extras.getString("donation_npo_id");
        }

		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent notificationIntent = new Intent(this, MainActivity.class);
		// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
		// | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		notificationIntent.putExtra(GCMUtils.EXTRA_MESSAGE_KEY, event_id);
        notificationIntent.putExtra(GCMUtils.EXTRA_PAGE_TYPE_KEY, page_type);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(notificationIntent);
		PendingIntent contentIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.ic_logo)
				.setContentTitle(message)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(content))
				.setContentText(content)
				.setDefaults(Notification.DEFAULT_VIBRATE).setAutoCancel(true);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		// Log.e(TAG, "sending: " + msg);
	}
}
