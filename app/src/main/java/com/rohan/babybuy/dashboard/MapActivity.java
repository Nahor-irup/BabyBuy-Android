package com.rohan.babybuy.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.rohan.babybuy.R;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker marker;
    private MarkerOptions markerOptions;
    private String fullAddress;
    private Button btnSaveMap, btnCloseMap;
    private TextView txtLatitude, txtLongitude;
    private EditText searchLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().hide();

//        binding = ActivityMapBinding.inflate(getLayoutInflater());
        btnSaveMap = findViewById(R.id.btnSaveMap);
        btnCloseMap = findViewById(R.id.btnCloseMap);
        txtLatitude = findViewById(R.id.txtLatitude);
        txtLongitude = findViewById(R.id.txtLongitude);
        searchLocation = findViewById(R.id.searchLocation);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //initialize the map on ready
        mapInitialize();

        //set save and close button visibility
        buttonVisibility();

        //button save action
        btnSaveMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latValue = txtLatitude.getText().toString();
                String longValue = txtLongitude.getText().toString();

                Intent intent = new Intent(MapActivity.this, AddProductActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("latitude", latValue);
                intent.putExtra("longitude", longValue);
                intent.putExtra("address", fullAddress);

                setResult(202, intent);
                finish();
            }
        });

        //button close action
        btnCloseMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void mapInitialize() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(16);
        locationRequest.setFastestInterval(3000);

        searchLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
                if (i == EditorInfo.IME_ACTION_SEARCH
                        || i == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == event.ACTION_DOWN
                        || event.getAction() == event.KEYCODE_ENTER
                ) {
                    goToSearchLocation();
                }
                return false;
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
    }

    private void goToSearchLocation() {
        String searchedLocation = searchLocation.getText().toString();
        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchedLocation, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            String location = address.getAdminArea();
            double latitude = address.getLatitude();
            double longitude = address.getLongitude();
            goToLatlng(latitude, longitude, 17f);
            fullAddress = address.getAddressLine(0);

            if (marker != null) {
                marker.remove();
            }
            markerOptions = new MarkerOptions();
            markerOptions.title(location);
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            markerOptions.position(new LatLng(latitude, longitude));
            marker = mMap.addMarker(markerOptions);
        }
    }

    private void goToLatlng(double latitude, double longitude, float v) {
        LatLng latLng = new LatLng(latitude, longitude);
        txtLatitude.setText(String.valueOf(latitude));
        txtLongitude.setText(String.valueOf(longitude));

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 17f);
        mMap.animateCamera(update);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mMap.setMyLocationEnabled(true);

                        fusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location == null) {
                                    return;
                                }
                                LatLng latLng;

                                double a, b;
                                if (getFromIntent().getExtras() != null) {
                                    Bundle params = getFromIntent().getExtras();
                                    if (params.getDouble("latitude") > 0 && params.getDouble("longitude") > 0) {
                                        a = params.getDouble("latitude");
                                        b = params.getDouble("longitude");
                                        latLng = new LatLng(a, b);
                                    } else {
                                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    }
                                } else {
                                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                }

                                if (marker != null) {
                                    marker.remove();
                                }
                                markerOptions = new MarkerOptions();
                                markerOptions.draggable(true);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                markerOptions.position(latLng);
                                marker = mMap.addMarker(markerOptions);

                                Geocoder geocoder = new Geocoder(getApplicationContext());
                                List<Address> list = null;
                                try {
                                    LatLng markerPosition = marker.getPosition();
                                    list = geocoder.getFromLocation(markerPosition.latitude, markerPosition.longitude, 1);
                                    txtLatitude.setText(String.valueOf(markerPosition.latitude));
                                    txtLongitude.setText(String.valueOf(markerPosition.longitude));
                                    Address address = list.get(0);
                                    marker.setTitle(address.getAdminArea());
                                    fullAddress = address.getAddressLine(0);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getApplicationContext(), "Permission " + permissionDeniedResponse.getPermissionName() + "" + "was denied!!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

        if (mMap != null) {
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDrag(@NonNull Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(@NonNull Marker marker) {
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    List<Address> list = null;
                    try {
                        LatLng markerPosition = marker.getPosition();
                        list = geocoder.getFromLocation(markerPosition.latitude, markerPosition.longitude, 1);
                        txtLatitude.setText(String.valueOf(markerPosition.latitude));
                        txtLongitude.setText(String.valueOf(markerPosition.longitude));
                        Address address = list.get(0);
                        marker.setTitle(address.getAdminArea());
                        fullAddress = address.getAddressLine(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onMarkerDragStart(@NonNull Marker marker) {

                }
            });
        }
    }

    public Intent getFromIntent() {
        Intent intent = getIntent();
        return intent;
    }

    public void buttonVisibility() {
        if (getFromIntent().getExtras() != null) {
            if (getFromIntent().getStringExtra("page").equals("product_add")||
                    getFromIntent().getStringExtra("page").equals("product_update")) {
                btnSaveMap.setVisibility(View.VISIBLE);
                btnCloseMap.setVisibility(View.INVISIBLE);
            } else if (getFromIntent().getStringExtra("page").equals("product_detail")) {
                btnSaveMap.setVisibility(View.INVISIBLE);
                btnCloseMap.setVisibility(View.VISIBLE);
            }
        } else {
            btnSaveMap.setVisibility(View.VISIBLE);
            btnCloseMap.setVisibility(View.INVISIBLE);
        }
    }
}