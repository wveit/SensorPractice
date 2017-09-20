package com.example.androidu.sensorpractice;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.androidu.sensorpractice.Sensors.MySensor;
import com.example.androidu.sensorpractice.Sensors.SensorService;
import com.example.androidu.sensorpractice.Utils.MyMath;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.SphericalUtil;

public class DisplayMapActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = "DisplayMapActivity";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private static final float DEFAULT_ZOOM = (float) 16.5; // shows the neighborhood

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private Context context;
    private LatLng currentLoc;

    private SensorService mSensors;

    private float[] mGravityVector = null;
    private float[] mMagnetVector = null;
    private float[] mRotationVector = null;
    private float[] mPhoneFrontVector = {0, 0, -1};
    private float[] mPhoneUpVector = {1, 0, 0};

    private float mBearing = 0;

    private boolean mMapAnimating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);

        context = this;

        findViewById(R.id.locator_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setCompassEnabled(false);
                getCurrentLocation();
            }
        });
    }

    // overriding onStart to load from db every time the app starts/resumes
    @Override
    protected void onStart() {
        super.onStart();
    }

    // overriding onStop to "clean up" every time the app is shut down
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        if(mSensors != null)
            mSensors.start();

        super.onResume();

        // renew gps data
        if(mMap != null)
            getCurrentLocation();
    }

    @Override
    protected void onPause() {
        if(mSensors != null)
            mSensors.stop();

        super.onPause();
    }

    public void getCurrentLocation() {
        Log.d(TAG, "getting current location");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }

        final Activity a = this;

        OnSuccessListener<Location> listener = new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
                    if(location.hasBearing())
                        mBearing = location.getBearing();
                    Log.d(TAG, "my current position is " + location.getLatitude() + ", " + location.getLongitude());
                    CameraPosition camPos = CameraPosition.builder(mMap.getCameraPosition()).target(currentLoc).bearing(mBearing).zoom(DEFAULT_ZOOM).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos), 500, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            Log.d(TAG, "sensors started");

                            // start the sensors
                            mSensors.start();
                        }

                        @Override
                        public void onCancel() {

                        }
                    });

                    mSensors = new SensorService(context, new SensorService.Callback() {
                        @Override
                        public void sensorServiceCallback(SensorEvent event) {
                            handleSensorEvent(event);
                        }
                    }, new int[] {SensorService.GRAVITY, SensorService.MAGNETIC_FIELD});
                }
            }
        };

        if(currentLoc != null) {
            listener = new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition camPos = CameraPosition.builder(mMap.getCameraPosition()).target(currentLoc).build();
                    mMapAnimating = true;
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos), 500, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            mMapAnimating = false;
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
            };
        }

        mMap.setMyLocationEnabled(true);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(a, listener);
    }

    // return the distance from user in minutes
    private double getTimeFromUser(double lat, double lon) {
        // adjust this constant to change the reading
        final double USER_SPEED = 67.8; // in meters per hour
        return SphericalUtil.computeDistanceBetween(currentLoc, new LatLng(lat, lon)) / USER_SPEED;
    }

    private void updateCameraBearing(GoogleMap googleMap, final int bearing) {
        if (googleMap == null) return;
        CameraPosition camP = CameraPosition.builder(googleMap.getCameraPosition()).bearing(bearing).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camP));
    }

    private void updateDirection() {
        float[] R = new float[9];
        float[] I = new float[9];

        if(SensorManager.getRotationMatrix(R, I, mGravityVector, mMagnetVector)) {
            float[] angles = new float[3];
            SensorManager.getOrientation(R, angles);
            double angle = angles[0] * 180.0 / Math.PI;

            updateCameraBearing(mMap, (int)Math.round(angle));
        }
    }

    private void handleSensorEvent(SensorEvent event) {
        if(event.sensor.getType() == SensorService.GRAVITY) {
            if(mGravityVector == null) {
                mGravityVector = event.values.clone();
                return;
            }
            else
                MyMath.lowPass(event.values.clone(), mGravityVector);
        }
        else {
            if(mMagnetVector == null) {
                mMagnetVector = event.values.clone();
                return;
            }
            else
                MyMath.lowPass(event.values.clone(), mMagnetVector);
        }

        if(mGravityVector != null && mMagnetVector != null)
            updateDirection();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "permission request answered");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Log.d(TAG, "permission granted");
                break;
        }
        // this means the device will keep asking for permission until it is granted
        // TODO: display a proper error message
        getCurrentLocation();
    }
}
