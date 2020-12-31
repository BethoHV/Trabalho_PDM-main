package com.example.trabalho_pdm.ui;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.trabalho_pdm.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private double latitudeDestino;
    private double longitudeDestino;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        latitudeDestino = getArguments().getDouble("latitudeDestino");
        longitudeDestino = getArguments().getDouble("longitudeDestino");

        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();

            String provider = locationManager.getBestProvider(criteria, true);

            mMap = googleMap;

            mMap.getUiSettings().setZoomControlsEnabled(true);

            mMap.setMyLocationEnabled(true);

        }catch (SecurityException ex){
            Log.d("ERRO", "Erro:" + ex);
        }
        LatLng destino = new LatLng(latitudeDestino, longitudeDestino);
        mMap.addMarker(new MarkerOptions().position(destino).title("Marcador no Destino"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(destino));
    }
}