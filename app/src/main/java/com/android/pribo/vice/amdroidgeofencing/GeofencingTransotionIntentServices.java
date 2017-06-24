package com.android.pribo.vice.amdroidgeofencing;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 24/06/2017.
 */

class GeofencingTransotionIntentServices extends IntentService {

    private static final String TAG = "GeofenceTransitionIS";

    public GeofencingTransotionIntentServices() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this, geofencingEvent.getErrorCode());
            Toast.makeText(this, "Error handeling the Intent", Toast.LENGTH_SHORT).show();
            return;
        }

        int geofencingTransition = geofencingEvent.getGeofenceTransition();
        if (geofencingTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofencingTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String geofenceTransitionDestails = getGeoFenceTransitionDetails(geofencingTransition , triggeringGeofences);

            sendNotification(geofenceTransitionDestails);

        } else {
            Toast.makeText(this, "Geofence Transition error :(", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotification(String notificationDetails) {
        Intent notificationIntent = new Intent(getApplicationContext() , MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this );
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0 , PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.drawable.download).setLargeIcon(BitmapFactory.decodeResource(getResources() , R.drawable.gate))
                .setColor(Color.RED).setContentTitle(notificationDetails).setContentIntent(notificationPendingIntent);

        builder.setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0 , builder.build());

    }

    private String getGeoFenceTransitionDetails(int geofenceTransition, List<Geofence> triggeringGeofences) {
        String geofenceTransitionString = getTransitionString(geofenceTransition);
        ArrayList<String> triggeredGeofencesIdList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeredGeofencesIdList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(" , ", triggeredGeofencesIdList);

        return geofenceTransitionString + " : " + triggeringGeofencesIdsString;
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(this, "I'm IN!! YEAH!!", Toast.LENGTH_SHORT).show();
                return "Entered";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(this, "I'm out man..see ya!", Toast.LENGTH_SHORT).show();
                return "Exited";
            default:
                Toast.makeText(this, "Dancing inside...", Toast.LENGTH_SHORT).show();
                return "Unknown Transition";

        }
    }
}
