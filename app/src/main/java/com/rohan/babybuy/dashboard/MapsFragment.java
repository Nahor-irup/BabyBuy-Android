package com.rohan.babybuy.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import com.rohan.babybuy.R;
import com.rohan.babybuy.databinding.FragmentMapsBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private FragmentMapsBinding binding;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker marker;
    private MarkerOptions markerOptions;
    private String fullAddress;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapInitialize();

        binding.btnSaveMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latValue = binding.txtLatitude.getText().toString();
                String longValue = binding.txtLongitude.getText().toString();

                Intent intent = new Intent(requireActivity(), AddProductActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("latitude", latValue);
                intent.putExtra("longitude", longValue);
                intent.putExtra("address", fullAddress);

                startActivity(intent);
            }
        });

        return binding.getRoot();
    }

    private void mapInitialize() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(16);
        locationRequest.setFastestInterval(3000);

        binding.searchLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    private void goToSearchLocation() {
        String searchLocation = binding.searchLocation.getText().toString();
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchLocation, 1);
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
        binding.txtLatitude.setText(String.valueOf(latitude));
        binding.txtLongitude.setText(String.valueOf(longitude));

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 17f);
        mMap.animateCamera(update);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        Dexter.withContext(getContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mMap.setMyLocationEnabled(true);

                        fusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                    if (params != null) {
                                        a = params.getDouble("latitude");
                                        b = params.getDouble("longitude");
                                        latLng = new LatLng(a, b);
                                    }else{
                                        latLng = new LatLng(location.getLatitude(),  location.getLongitude());
                                    }
                                }else {
                                    latLng = new LatLng(location.getLatitude(),  location.getLongitude());
                                }

                                if (marker != null) {
                                    marker.remove();
                                }
                                markerOptions = new MarkerOptions();
                                markerOptions.draggable(true);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                markerOptions.position(latLng);
                                marker = mMap.addMarker(markerOptions);

                                Geocoder geocoder = new Geocoder(getContext());
                                List<Address> list = null;
                                try {
                                    LatLng markerPosition = marker.getPosition();
                                    list = geocoder.getFromLocation(markerPosition.latitude, markerPosition.longitude, 1);
                                    binding.txtLatitude.setText(String.valueOf(markerPosition.latitude));
                                    binding.txtLongitude.setText(String.valueOf(markerPosition.longitude));
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
                        Toast.makeText(getContext(), "Permission " + permissionDeniedResponse.getPermissionName() + "" + "was denied!!", Toast.LENGTH_SHORT).show();
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
                    Geocoder geocoder = new Geocoder(getContext());
                    List<Address> list = null;
                    try {
                        LatLng markerPosition = marker.getPosition();
                        list = geocoder.getFromLocation(markerPosition.latitude, markerPosition.longitude, 1);
                        binding.txtLatitude.setText(String.valueOf(markerPosition.latitude));
                        binding.txtLongitude.setText(String.valueOf(markerPosition.longitude));
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
        Intent intent = getActivity().getIntent();
        return intent;
    }
}