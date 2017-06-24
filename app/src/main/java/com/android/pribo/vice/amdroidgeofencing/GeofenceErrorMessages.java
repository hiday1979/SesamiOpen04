package com.android.pribo.vice.amdroidgeofencing;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.GeofenceStatusCodes;

/**
 * Created by user on 24/06/2017.
 */

class GeofenceErrorMessages {

    public GeofenceErrorMessages() {
    }

    public static String getErrorString(Context context , Exception e){
        if (e instanceof ApiException)
            return "ApiException ";
        else
            return "UKNOWN Error";
    }

    public static String getErrorString(Context context , int errorCode){
        Resources mResources = context.getResources();
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GEO Services not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "You've provided too many Intents";
            default:
                return "UNKNOWN Error";
        }

    }

}
