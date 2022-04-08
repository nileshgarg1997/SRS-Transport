package com.myapp.srstransport.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.srstransport.R;


public class TrackingFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap googleMap;
    DatabaseReference databaseReference;
    String routeNo;
    Marker userLocationMarker;
//    Circle userLocationAccuracyCircle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        supportMapFragment.getMapAsync(this);

        Bundle bundle = getArguments();

        if (bundle != null) {
            routeNo = bundle.getString("route");
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {

        googleMap = map;

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Driver's Location").child(routeNo).child("l");

        ValueEventListener valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Double latitude = snapshot.child("0").getValue(Double.class);
                    Double longitude = snapshot.child("1").getValue(Double.class);

                    LatLng location = new LatLng(latitude, longitude);

                    Location temp = new Location(LocationManager.GPS_PROVIDER);
                    temp.setLatitude(location.latitude);
                    temp.setLongitude(location.longitude);

                    setUserLocationMarker(temp);
//                    googleMap.addMarker(new MarkerOptions().position(location));
//                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));


                } else {
                    Toast.makeText(getContext(), "Data not found!!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUserLocationMarker(Location locationMarker) {

        LatLng latLng = new LatLng(locationMarker.getLatitude(), locationMarker.getLongitude());

        if (userLocationMarker == null) {
            // create the new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus));
//            markerOptions.rotation(locationMarker.getAccuracy());
            markerOptions.title(routeNo);
            markerOptions.anchor((float) 0.5, (float) 0.5);
            userLocationMarker = googleMap.addMarker(markerOptions);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        } else {
            // use the previously created marker
            userLocationMarker.setPosition(latLng);
//            userLocationMarker.setRotation(locationMarker.getBearing());
            userLocationMarker.setTitle(routeNo);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }

//        if (userLocationAccuracyCircle == null) {
//            CircleOptions circleOptions = new CircleOptions();
//            circleOptions.center(latLng);
//            circleOptions.strokeWidth(4);
//            circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
//            circleOptions.fillColor(Color.argb(32, 255, 0, 0));
//            circleOptions.radius(locationMarker.getAccuracy());
//            userLocationAccuracyCircle = googleMap.addCircle(circleOptions);
//        }else {
//            userLocationAccuracyCircle.setCenter(latLng);
//            userLocationAccuracyCircle.setRadius(locationMarker.getAccuracy());
//        }
    }
}