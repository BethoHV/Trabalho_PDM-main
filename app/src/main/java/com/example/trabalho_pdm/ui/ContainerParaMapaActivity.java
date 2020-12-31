package com.example.trabalho_pdm.ui;

import android.app.AppComponentFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.trabalho_pdm.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContainerParaMapaActivity extends AppCompatActivity {
    private static final String TITULO_APPBAR = "Mapa";


    private FragmentManager fragmentManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_para_mapa);
        setTitle(TITULO_APPBAR);

        final String destino = new String(getIntent().getStringExtra("DESTINO"));

        double latitudeDestino = 0;
        double longitudeDestino = 0;
        List<Address> listaDestino = new ArrayList<Address>();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        //Criando coordenadas destino
        try {
            listaDestino = (ArrayList<Address>) geocoder.getFromLocationName(destino, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(listaDestino != null && listaDestino.size() > 0){
            Address a = listaDestino.get(0);
            latitudeDestino = a.getLatitude();
            longitudeDestino = a.getLongitude();
        }
        else{
            Toast.makeText(this, "Local não encontrado.", Toast.LENGTH_SHORT).show();
            finish();
        }

        fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        MapaFragment fragmentoMapa = new MapaFragment();

        Bundle bundle = new Bundle();
        bundle.putDouble("latitudeDestino", latitudeDestino);
        bundle.putDouble("longitudeDestino", longitudeDestino);
        fragmentoMapa.setArguments(bundle);

        transaction.add(R.id.activity_container, fragmentoMapa, "Mapa");

        transaction.commitAllowingStateLoss();
    }
}
