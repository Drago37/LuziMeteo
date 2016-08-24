package com.luziweb.luzimeteo.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.luziweb.luzimeteo.R;
import com.luziweb.luzimeteo.utils.GlobalTools;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private double mDoubleLat;
    private double mDoubleLon;
    private String mStringVille;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Récupération des données transmises par l'activité
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mDoubleLat = extras.getDouble(GlobalTools.KEY_LAT);
            mDoubleLon = extras.getDouble(GlobalTools.KEY_LON);
            mStringVille = extras.getString(GlobalTools.KEY_VILLE);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng city = new LatLng(mDoubleLat, mDoubleLon);
        mMap.addMarker(new MarkerOptions().position(city).title(mStringVille));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(city));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mDoubleLat, mDoubleLon), 14.0f));
    }
}
