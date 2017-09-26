package com.example.androidu.sensorpractice;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.androidu.sensorpractice.Camera.Camera2BasicFragment;
import com.example.androidu.sensorpractice.Camera.CameraOverlayView;
import com.example.androidu.sensorpractice.Sensors.MySensor;
import com.example.androidu.sensorpractice.Sensors.SensorService;
import com.example.androidu.sensorpractice.Utils.MyMath;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Map;

public class DisplayARActivity extends AppCompatActivity {
    private final static String TAG = "DisplayAR";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private static final float DEFAULT_ZOOM = (float) 16.5; // shows the neighborhood

    private CameraOverlayView mCamOverlay;
    private Camera2BasicFragment mCamFragment;
    private MapFragment mMapFragment;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private Context context;
    private LatLng currentLoc;

    private SensorService mSensors;

    private float[] mPhoneFrontVector = {0, 0, -1};
    private float[] mPhoneUpVector = {0, 1, 0};
    private Map<Integer, float[]> mSensorData = null;

    private float mBearing = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ar);

        // keeps the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // hide the action bar (gets fullscreen)
        getSupportActionBar().hide();

        // try to request full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        context = this;

        // replace the camera container with a basic camera2 fragment
        if (null == savedInstanceState) {
            mCamFragment = Camera2BasicFragment.newInstance();
            mCamOverlay = new CameraOverlayView(context);
            // can't just add the view to a layout (since it may not exist yet before the camera is initialized)
            // so we will add the camera overlay whenever the camera preview is ready
            mCamFragment.setCamLayoutCallback(new CameraActivity.CameraLayoutCallback() {
                @Override
                public void setUpLayout(LinearLayout layout) {
                    layout.addView(mCamOverlay);
                }
            });
            //getFragmentManager().beginTransaction().replace(R.id.ar_cam_container, mCamFragment).commit();

            mMapFragment = new MapFragment();
            getFragmentManager().beginTransaction().replace(R.id.ar_map_container, mMapFragment).commit();
            mMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    Log.d(TAG, "Google Maps fetched");
                    mMap = googleMap;
                    mMap.getUiSettings().setMapToolbarEnabled(false);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mMap.getUiSettings().setCompassEnabled(false);
                    getCurrentLocation();
                }
            });
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @Override
    protected void onResume() {
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
                    if(location.hasBearing()) {
                        Log.d(TAG, "this location has bearing");
                        mBearing = location.getBearing();
                    }
                    Log.d(TAG, "my current position is " + location.getLatitude() + ", " + location.getLongitude() + " with bearing = " + mBearing);
                    CameraPosition camPos = CameraPosition.builder(mMap.getCameraPosition()).target(currentLoc).zoom(DEFAULT_ZOOM).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos), 500, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            Log.d(TAG, "sensors started");

                            // start the sensors
                            mSensorData = new ArrayMap<>();
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
                    CameraPosition camPos = CameraPosition.builder(mMap.getCameraPosition()).target(currentLoc).zoom(DEFAULT_ZOOM).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos), 500, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            // start the sensors
                            mSensors.start();
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

    private void updateCameraBearing(GoogleMap googleMap, final float bearing) {
        if (googleMap == null) return;
        CameraPosition camP = CameraPosition.builder(googleMap.getCameraPosition()).bearing(bearing).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camP));
    }

    private void updateDirection(){
        float[] gravityVector = mSensorData.get(new Integer(SensorService.GRAVITY));
        float[] magnetVector = mSensorData.get(new Integer(SensorService.MAGNETIC_FIELD));

        float tilt = MyMath.tiltAngle(gravityVector, mPhoneUpVector);
        float bearing = MyMath.compassBearing(magnetVector, gravityVector, tilt);
        if (Math.abs(bearing - mBearing) >= 1.10) {
            mBearing = bearing;
            mCamOverlay.setBearing(Math.round(bearing * 10) / 10.0f);
            updateCameraBearing(mMap, bearing);
        }
        //mCamOverlay.setTilt((int)MyMath.landscapeTiltAngle(mGravityVector, mPhoneUpVector));
        mCamOverlay.setTilt((tilt * 10) / 10.0f);
    }

    private void handleSensorEvent(SensorEvent event) {
        //if(event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)  return;

        Integer key = new Integer(event.sensor.getType());

        if(mSensorData.containsKey(key)) {
            float[] outputData = mSensorData.get(key);
            MyMath.lowPass(event.values.clone(), outputData);
            mSensorData.put(key, outputData);
        }
        else
            mSensorData.put(key, event.values.clone());

        if(mSensorData.size() > 1)
            updateDirection();
    }
}
