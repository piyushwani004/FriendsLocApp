/*
 * <!--
 *   ~ /*******************************************************
 *   ~  * Copyright (C) 2021-2031 {Piyush Wani and  Mayur Sapkale} <{piyushwani04@gmail.com}>
 *   ~  *
 *   ~  * This file is part of {FriendLocatorApp}.
 *   ~  *
 *   ~  * {FriendLocatorApp} can not be copied and/or distributed without the express
 *   ~  * permission of {Piyush Wani and  Mayur Sapkale}
 *   ~  ******************************************************
 *   -->
 */

package com.piyush004.friendslocapp.Home.Fragments.Map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.piyush004.friendslocapp.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener {

    private static final String TAG = MapsFragment.class.getSimpleName();

    private GoogleMap GoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private LocationRequest mLocationRequest;
    private Context context;
    private FirebaseAuth firebaseAuth;
    private Double latitude, longitude;
    private View view;
    private RecyclerView recyclerView;
    private FirebaseRecyclerOptions<MapUserModel> options;
    private FirebaseRecyclerAdapter<MapUserModel, MapUserHolder> adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps, container, false);
        Log.e(TAG, "onCreateView: ");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated: ");
        context = view.getContext();
        firebaseAuth = FirebaseAuth.getInstance();
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        recyclerView = view.findViewById(R.id.mapRecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        final DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("Friends").child(firebaseAuth.getCurrentUser().getUid());
        options = new FirebaseRecyclerOptions.Builder<MapUserModel>().setQuery(df, snapshot -> new MapUserModel(

                snapshot.child("id").getValue(String.class)

        )).build();
        adapter = new FirebaseRecyclerAdapter<MapUserModel, MapUserHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final MapUserHolder holder, int position, @NonNull final MapUserModel model) {

                DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("AppUsers").child(model.getMUId());

                user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String imgUrl = snapshot.child("ImageURL").getValue(String.class);
                        String Name = snapshot.child("Name").getValue(String.class);
                        Double Lat = snapshot.child("Location").child("latitude").getValue(Double.class);
                        Double Lon = snapshot.child("Location").child("longitude").getValue(Double.class);

                        Picasso.get().load(imgUrl)
                                .resize(500, 500)
                                .centerCrop()
                                .rotate(0)
                                .placeholder(R.drawable.person_placeholder)
                                .into(holder.circleImageView);

                        addMarker(Lat, Lon, Name, imgUrl);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                holder.itemView.setOnClickListener(v -> {

                    DatabaseReference user1 = FirebaseDatabase.getInstance().getReference().child("AppUsers").child(model.getMUId());

                    user1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            Double Lat = snapshot.child("Location").child("latitude").getValue(Double.class);
                            Double Lon = snapshot.child("Location").child("longitude").getValue(Double.class);

                            moveMap(Lat, Lon);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                });


            }

            @NonNull
            @Override
            public MapUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_user_card, parent, false);
                return new MapUserHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new com.google.android.gms.location.LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    mLastLocation = location;
                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }

                    final DatabaseReference locationDf = FirebaseDatabase.getInstance().getReference().child("AppUsers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    final HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("latitude", location.getLatitude());
                    hashMap.put("longitude", location.getLongitude());
                    locationDf.child("Location").updateChildren(hashMap);

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("AppUsers").child(firebaseAuth.getCurrentUser().getUid());
                    user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String ImgUrl = snapshot.child("ImageURL").getValue(String.class);
                            String Name = snapshot.child("Name").getValue(String.class);

                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.title(Name);
                            markerOptions.snippet(ImgUrl);
                            markerOptions.icon(bitmapDescriptorFromVector(getContext(), R.drawable.user_marker_64x64));
                            mCurrLocationMarker = GoogleMap.addMarker(markerOptions);
                            CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(getActivity());
                            GoogleMap.setInfoWindowAdapter(adapter);
                            GoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            GoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    if (mGoogleApiClient != null) {
                        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                    }
                }
            });
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        GoogleMap = googleMap;
        GoogleMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                GoogleMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            GoogleMap.setMyLocationEnabled(true);
        }

        GoogleMap.setOnInfoWindowClickListener(marker -> {
            LatLng latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            GoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            GoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            GoogleMap.getUiSettings().setZoomControlsEnabled(true);
        });


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }


    private void getCurrentLocation() {
        GoogleMap.clear();
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            moveMap();
        }
    }

    private void moveMap() {
        LatLng latLng = new LatLng(latitude, longitude);
        GoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        GoogleMap.animateCamera(CameraUpdateFactory.zoomTo(20));
        GoogleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void moveMap(Double lat, Double lon) {
        LatLng latLng = new LatLng(lat, lon);
        GoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        GoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        GoogleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        //GoogleMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
        moveMap();
    }

    public BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorId) {
        Drawable VectorDrawable = ContextCompat.getDrawable(context, vectorId);
        assert VectorDrawable != null;
        VectorDrawable.setBounds(0, 0, VectorDrawable.getIntrinsicWidth(), VectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(VectorDrawable.getIntrinsicWidth(), VectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        VectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    public void addMarker(Double lat, Double lon, String name, String imgUrl) {

        LatLng location = new LatLng(lat, lon);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.title(name);
        markerOptions.snippet(imgUrl);
        markerOptions.icon(bitmapDescriptorFromVector(getContext(), R.drawable.user_marker_64x64));
        mCurrLocationMarker = GoogleMap.addMarker(markerOptions);
        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(getActivity());
        GoogleMap.setInfoWindowAdapter(adapter);

    }




}



