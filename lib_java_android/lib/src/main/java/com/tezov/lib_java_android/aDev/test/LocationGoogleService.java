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
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib.type.unit.UnitByte;
import android.app.Activity;
import android.content.AppContext;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class LocationGoogleService{
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    public static final int REQUEST_CHECK_SETTINGS = 100;

    private FusedLocationProviderClient fusedLocationClient;
    private SettingsClient settingsClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;
    private LocationCallback locationCallback;
    private Location currentLocation;

    public void init(AppContext context){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        settingsClient = LocationServices.getSettingsClient(context);
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                if (locationResult == null) {
                    return;
                }
                currentLocation = locationResult.getLastLocation();
                Log.d(">>:", "init.onLocationResult:" + currentLocation);
            }
        };
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    @RequiresPermission("Manifest.permission.ACCESS_FINE_LOCATION")
    public void startLocationUpdates(Activity activity){
        settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>(){
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse){
                Log.d(">>:", "startLocationUpdates.onSuccess");
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        }).addOnFailureListener(activity, new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception error){
                if (error instanceof ResolvableApiException) {
                    int statusCode = ((ResolvableApiException)error).getStatusCode();
                    switch(statusCode){
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.d(">>:", "startLocationUpdates.onFailure.RESOLUTION_REQUIRED");
                            try{
                                ResolvableApiException resolvable = (ResolvableApiException) error;
                                resolvable.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS); //result in activity
                            } catch(Exception e){
                                Log.d(">>:", "PendingIntent unable to execute request");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.d(">>:", "startLocationUpdates.onFailure.SETTINGS_CHANGE_UNAVAILABLE");
                    }
                }
            }
        });
    }

    public void stopLocationUpdates(Activity activity){
        fusedLocationClient.removeLocationUpdates(locationCallback).addOnCompleteListener(activity, new OnCompleteListener<Void>(){
            @Override
            public void onComplete(@NonNull Task<Void> task){
                Log.d(">>:", "stopLocationUpdates");
            }
        });
    }

//    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
//    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
//



}
*/
