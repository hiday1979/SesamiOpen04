package com.android.pribo.vice.amdroidgeofencing;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by user on 24/06/2017.
 */

public class Constants {

    public Constants() {
    }

    static final HashMap <String , LatLng> nofim = new HashMap<>();
    static {
        nofim.put("Gate" , new LatLng(32.149629, 35.108205));
        /*nofim.put("Home" , new LatLng(32.154260, 35.099799));*/
        nofim.put("Netania" , new LatLng(32.295594, 34.875832));
    }
}
