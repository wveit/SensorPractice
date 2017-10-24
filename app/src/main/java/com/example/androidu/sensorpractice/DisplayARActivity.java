package com.example.androidu.sensorpractice;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.example.androidu.sensorpractice.Camera.Camera2BasicFragment;
import com.example.androidu.sensorpractice.Camera.CameraOverlayView;
import com.example.androidu.sensorpractice.GL.GLFragment;
import com.example.androidu.sensorpractice.GL.GLView;
import com.example.androidu.sensorpractice.Network.Interface;
import com.example.androidu.sensorpractice.Sensors.MySensor;
import com.example.androidu.sensorpractice.Sensors.SensorService;
import com.example.androidu.sensorpractice.Utils.MyMath;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

public class DisplayARActivity extends AppCompatActivity {
    private final static String TAG = "DisplayAR";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private static final float DEFAULT_ZOOM = 17.75f; // shows the neighborhood
    private static final float CAMERA_TILT = 88.5f; // shows 3D view

    //private CameraOverlayView mCamOverlay;
    //private View mCamOverlay;
    private GLFragment mGLFragment;
    private Camera2BasicFragment mCamFragment;

    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;

    private static final long DATA_NETWORK_QUERY_INTERVAL = 30000; // 30 sec

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Callback for Location events.
     */
    private LocationCallback mLocationCallback;

    /**
     * Represents a geographical location.
     */
    private Location mCurrentLocation;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    private String mLastUpdateTime;

    private MapFragment mMapFragment;
    private GoogleMap mMap;
    private Context context;

    private SensorService mSensors;

    private boolean mDataConnected;
    private long mLastDataQuery;

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
        final Activity a = this;

        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        final int min_dim = Math.min(screenSize.x, screenSize.y);

        // setting up location services
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // setting up data network
        mDataConnected = false;
        mLastDataQuery = 0;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        // replace the camera container with a basic camera2 fragment
        if (null == savedInstanceState) {
            // initialize camera view (bottom)
            mCamFragment = Camera2BasicFragment.newInstance();
            getFragmentManager().beginTransaction().add(R.id.ar_cam_container, mCamFragment).commit();

            // initialize gl view (middle)
            mGLFragment = new GLFragment();
            getFragmentManager().beginTransaction().add(R.id.ar_gl_container, mGLFragment).commit();
            /*
            FrameLayout glLayout = (FrameLayout) findViewById(R.id.ar_gl_container);
            ViewGroup.LayoutParams glParams = glLayout.getLayoutParams();
            glParams.height = (int) Math.round(min_dim / 2.0);
            glParams.width = (int) Math.round(min_dim / 2.0);
            glLayout.setLayoutParams(glParams);
            */

            // initialize map view (top)
            mMapFragment = MapFragment.newInstance();
            mMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    Log.d(TAG, "Google Maps fetched");
                    mMap = googleMap;
                    mMap.getUiSettings().setMapToolbarEnabled(false);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mMap.getUiSettings().setCompassEnabled(false);

                    mSensors = new SensorService(context, new SensorService.Callback() {
                        @Override
                        public void sensorServiceCallback(SensorEvent event) {
                            handleSensorEvent(event);
                        }
                    }, new int[] {SensorService.GRAVITY, SensorService.MAGNETIC_FIELD});

                    startLocationUpdates();
                }
            });
            getFragmentManager().beginTransaction().add(R.id.ar_map_container, mMapFragment).commit();

            FrameLayout mapLayout = (FrameLayout) findViewById(R.id.ar_map_container);
            ViewGroup.LayoutParams params = mapLayout.getLayoutParams();
            params.height = (int) Math.round(min_dim / 2.15);
            params.width = (int) Math.round(min_dim / 2.15);
            mapLayout.setLayoutParams(params);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // renew gps data
        //if(mMap != null)
        //getCurrentLocation();

        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (mMap != null && mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "paused!");

        if(mRequestingLocationUpdates) {
            stopLocationUpdates();
            mSensors.stop();
        }
    }

    private void connectDataNetwork() {
        StringRequest sr = Interface.getInstance(context).authenticateYelp(new Interface.AuthenticationCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Received Yelp authentication confirmation!");
                mDataConnected = true;
                Toast.makeText(context, "Yelp authenticated", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "Uh oh! Something went wrong!");
            }
        });

        // do something to sr (maybe cancel if it's taking too long)
    }

    private void queryNearbyLocations() {
        long time = System.currentTimeMillis();
        if(time - mLastDataQuery <= DATA_NETWORK_QUERY_INTERVAL) return;

        Log.d(TAG, "querying nearby locations");
        StringRequest sr = Interface.getInstance(context).queryYelp(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), new Interface.NetworkCallback() {
            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Nearby locations:\n" + response);
                Toast.makeText(context, "Retrieved: " + response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(context, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });

        mLastDataQuery = time;
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                //mBearing = mCurrentLocation.getBearing();

                /*
                CameraPosition camPos = CameraPosition
                        .builder(mMap.getCameraPosition())
                        .target(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                        .zoom(DEFAULT_ZOOM)
                        .bearing(mBearing)
                        .tilt(CAMERA_TILT)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
                */

                // data network
                if(mDataConnected)
                    queryNearbyLocations();

                // if sensors are not running, start them
                if(!mSensors.running())
                    mSensors.start();
            }
        };
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.gps_permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(DisplayARActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(DisplayARActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates() {
        Log.d(TAG, "getting current location");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        // setup map
                        mMap.setIndoorEnabled(true);
                        mMap.setBuildingsEnabled(true);

                        // setup data network
                        //connectDataNetwork();

                        // setup sensors
                        mSensorData = new ArrayMap<>();

                        mRequestingLocationUpdates = true;
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(DisplayARActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(DisplayARActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }
                    }
                });
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    private void updateCameraBearing(GoogleMap googleMap, final float bearing) {
        if (googleMap == null) return;
        CameraPosition camP = CameraPosition.builder(googleMap.getCameraPosition())
                .target(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                .zoom(DEFAULT_ZOOM)
                .bearing(bearing)
                .tilt(CAMERA_TILT)
                .build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camP));
    }

    private void updateDirection(){
        float[] gravityVector = mSensorData.get(new Integer(SensorService.GRAVITY));
        float[] magnetVector = mSensorData.get(new Integer(SensorService.MAGNETIC_FIELD));

        //float tilt = MyMath.landscapeTiltAngle(gravityVector, magnetVector);
        float tilt = MyMath.landscapeTiltAngle(gravityVector, mPhoneUpVector);
        float bearing = MyMath.compassBearing(gravityVector, magnetVector, mPhoneFrontVector);
        if (Math.abs(bearing - mBearing) >= 1.10) {
            mBearing = bearing;
            //((CameraOverlayView) mCamOverlay).setBearing(Math.round(bearing * 10) / 10.0f);
            mGLFragment.setBearing(Math.round(bearing * 10) / 10.0f);
            updateCameraBearing(mMap, 360 - bearing);
        }
        float vTilt = MyMath.portraitTiltAngle(gravityVector, magnetVector);
        mGLFragment.setViewTilt((vTilt * 10) / 10.0f);
        mGLFragment.setTilt((tilt * 10) / 10.0f);
        //((CameraOverlayView) mCamOverlay).setTilt((tilt * 10) / 10.0f);
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

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult with requestCode=" + requestCode);
        Log.d(TAG, "permissions=" + permissions.toString());
        Log.d(TAG, "grantResults=" + grantResults.toString());
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Awesome!");
                startLocationUpdates();
            } else {
                Log.d(TAG, "sumfink wong!");
                showSnackbar(R.string.gps_permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
        else
            Log.d(TAG, "sumfink is very wong!");
    }
}
