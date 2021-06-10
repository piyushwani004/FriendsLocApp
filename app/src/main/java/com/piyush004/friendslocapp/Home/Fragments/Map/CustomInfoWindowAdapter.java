package com.piyush004.friendslocapp.Home.Fragments.Map;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.piyush004.friendslocapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static final String TAG = CustomInfoWindowAdapter.class.getSimpleName();
    private Activity context;

    public CustomInfoWindowAdapter(Activity context) {
        this.context = context;
    }


    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.custom_info_window_card, null);

        CircleImageView circleImageView = view.findViewById(R.id.mapImg);
        TextView name = view.findViewById(R.id.map_userName);

        name.setText(marker.getTitle());

        Log.e(TAG, "getInfoContents: " + marker.getId());

        return view;
    }


}