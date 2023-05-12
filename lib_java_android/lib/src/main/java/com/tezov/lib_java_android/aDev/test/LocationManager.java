/*
package com.tezov.lib.aDev.test;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import com.tezov.lib.toolbox.debug.DebugException;
import com.tezov.lib.toolbox.debug.DebugLog;
import com.tezov.lib.toolbox.debug.DebugTrack;

import com.tezov.lib.type.primitive.IntTo;
import com.tezov.lib.toolbox.CompareType;
import com.tezov.lib.type.primitive.ObjectTo;
import com.tezov.lib.util.UtilsString;
import com.tezov.lib.toolbox.Clock;
import com.tezov.lib.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib.database.sqlLite.filter.chunk.ChunkCommand;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib.type.unit.UnitByte;
import android.content.AppContext;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

class LocationManager{
    LocationListener listener = null;
    android.location.Location lastLocation = null;

    @RequiresPermission("Manifest.permission.ACCESS_FINE_LOCATION")
    public void listen(AppContext context, long intervalDelay, TimeUnit unit, int intervalDistance_metre){
        if(listener != null){
            throw new RuntimeException();
        }
        listener = new LocationListener(){
            @Override
            public void onLocationChanged(android.location.Location location){
                onLocationChange(location);
                lastLocation = location;
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras){
                Log.d(">>: ", "onStatusChanged:" + provider + ":" + status);
            }
            @Override
            public void onProviderEnabled(String provider){
                Log.d(">>:", "onProviderEnabled:" + provider);
            }
            @Override
            public void onProviderDisabled(String provider){
                Log.d(">>:", "onProviderEnabled:" + provider);
            }
        };
        android.location.LocationManager locationManager = (android.location.LocationManager)context.getSystemService(AppContext.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, TimeUnit.MILLISECONDS.convert(intervalDelay, unit), intervalDistance_metre, listener);
    }
    public void unListen(AppContext context){
        if(listener == null){
            throw new RuntimeException();
        }
        android.location.LocationManager locationManager = (android.location.LocationManager)context.getSystemService(AppContext.LOCATION_SERVICE);
        locationManager.removeUpdates(listener);
        listener = null;
    }

    public boolean isGPSLocationEnable(AppContext context){
        android.location.LocationManager locationManager = (android.location.LocationManager)context.getSystemService(AppContext.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }
    public boolean isNETWORKLocationEnable(AppContext context){
        android.location.LocationManager locationManager = (android.location.LocationManager)context.getSystemService(AppContext.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
    }
    public boolean isPASSIVELocationEnable(AppContext context){
        android.location.LocationManager locationManager = (android.location.LocationManager)context.getSystemService(AppContext.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(android.location.LocationManager.PASSIVE_PROVIDER);
    }

    @RequiresPermission("Manifest.permission.ACCESS_FINE_LOCATION")
    public android.location.Location getLastLocation(AppContext context){
        if(lastLocation != null){
            return lastLocation;
        } else {
            android.location.LocationManager locationManager = (android.location.LocationManager)context.getSystemService(AppContext.LOCATION_SERVICE);

            android.location.Location lastLocationGPS = locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
            long GPSLocationTime = 0;
            if(lastLocationGPS != null){
                GPSLocationTime = lastLocationGPS.getTime();
            }

            android.location.Location lastLocationNETWORK = locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);
            long NetLocationTime = 0;
            if(lastLocationNETWORK != null){
                NetLocationTime = lastLocationNETWORK.getTime();
            }
            if((GPSLocationTime - NetLocationTime) > 0){
                return lastLocationGPS;
            } else {
                return lastLocationNETWORK;
            }
        }
    }

    protected void onLocationChange(android.location.Location location){
        Log.d(">>:", "onLocationChange:" + location.toString());
    }

    public String getCity(AppContext context, android.location.Location location){
        String cityName = null;
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try{
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if(addresses.size() > 0){
                cityName = addresses.get(0).getLocality();
            }
        } catch(Exception e){

        }
        Log.d(">>:", "cityName:" + location.toString());
        return cityName;
    }

//        <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
//    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

}
*/
