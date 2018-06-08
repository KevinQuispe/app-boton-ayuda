package com.pcquispe.appbotonayuda.fragmentos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pcquispe.appbotonayuda.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsUbicameFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, android.location.LocationListener {

    //declare variables
    private GoogleMap mMap;
    public MapView mMapView;
    public View mView;
    private Marker marcador;
    double lat = 0.0;
    double lng = 0.0;
    String mensaje1;
    String direccion = "";
    String pais = "";
    Button btnubicame;
    Button btncompartir;
    EditText direction, country;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    public MapsUbicameFragment() {
    }
    public static MapsUbicameFragment newInstance(String param1, String param2) {
        MapsUbicameFragment fragment = new MapsUbicameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_maps_ubicame, container, false);
        direction = (EditText) mView.findViewById(R.id.textdireccion);
        country = (EditText) mView.findViewById(R.id.txtciudad);
        btncompartir = (Button) mView.findViewById(R.id.btn_compartir);
        mMapView=(MapView) mView.findViewById(R.id.map);
        mMapView.setOnClickListener(this);
        btncompartir.setOnClickListener(this);
        country.setEnabled(false);
        direction.setEnabled(false);
        return mView;
    }

    //MEETODO INIT TO MY MAPVIEW
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        miUbicacion();
    }

    //METODO PARA PEDIR PERSMISOS A MI UBICACION FINE_LOCATION
    private static final int PETICION_PERMISO_LOCALIZACION = 101;

    private void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            ActualizarUbicacion(location);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locListener);
        }
    }

    //activar los servicios del gps cuando esten apagados
    public void locationStart() {
        LocationManager mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
    }

    //setear la localizacion
    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this.getContext(), Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    //obtner la direccion
                    Address DirCalle = list.get(0);
                    direccion = (DirCalle.getAddressLine(0));
                    //obtener el pais
                    Locale country = new Locale("", "PE");
                    pais = country.getDisplayCountry();
                } else {
                    Log.i("Dirección no encontrada", null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //agregar el marcador en el mapa
    private void AgregarMarcador(double lat, double lng) {
        LatLng coordenadas = new LatLng(lat, lng);
        //tipo de mapa
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        float zoomlevel = 17.5f;
        CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, zoomlevel);
        if (marcador != null)
            marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions().position(coordenadas).title("Dirección:" + direccion)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            //mostrar direccion
            mostrarDireccion();

        mMap.animateCamera(MiUbicacion);

    }
   //METODO PARA MOSTRAR LA DIRECCION
    public void mostrarDireccion() {
        String country_name = null;
        String calle_name = null;
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Geocoder geocoder = new Geocoder(getActivity());
        for (String provider : lm.getAllProviders()) {
            @SuppressWarnings("ResourceType") Location location = lm.getLastKnownLocation(provider);
            if (location != null) {
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses != null && addresses.size() > 0) {
                        Address DirCalle = addresses.get(0);
                        calle_name=addresses.get(0).getAddressLine(0); //Direccion calle
                        direccion = (DirCalle.getAddressLine(0)); //full direccion
                        country_name = addresses.get(0).getCountryName();
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (direccion.equals("")&& calle_name.equals("")){
            Toast.makeText(getActivity(), "Actulize mapa", Toast.LENGTH_LONG).show();
        }
        else {
            direction.setText(calle_name); //direccion completa
            country.setText(country_name); //ciudad Pias
        }
    }
    //actualizar la ubicacion
    private void ActualizarUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            AgregarMarcador(lat, lng);
        }
    }

    //control del gps
    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            try {
                ActualizarUbicacion(location);
                setLocation(location);
            } catch (Exception e) {
                Log.i("error", e.getMessage());
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
            mensaje1 = ("GPS Activado");
            Mensaje();
        }

        @Override
        public void onProviderDisabled(String s) {
            mensaje1 = ("GPS Desactivado");
            locationStart();
            Mensaje();
        }
    };

    public void Mensaje() {
        Toast toast = Toast.makeText(getActivity(), mensaje1, Toast.LENGTH_LONG);
        //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //METODO CLICK LISTENER
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map:
                break;
            case  R.id.btn_compartir:
                compartirRedes();


        }
    }
    //METODO PARA GUARDAR DATOS DE DIRECCION
    public void guardarDatos() {
        //Fragment fragment =new LugaresFavoritos();
        //bundle para pasar los datos
        // statusMessage.setText("value is " + i[0]);
        Bundle bundle = new Bundle();
        bundle.putString("direKey", direction.getText().toString());
        bundle.putString("paisKey", country.getText().toString());
        // fragment.setArguments(bundle);
        //declaramos el fragment managmet para hacer coming y transat
        FragmentManager fragmentManager = getFragmentManager();
        // fragmentManager.beginTransaction().replace(R.id.contenedorFragment, fragment).commit();
        Toast.makeText(getActivity(), "add favorite", Toast.LENGTH_LONG).show();
    }

    private Dialog crear_dialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setMessage("Mostrar Direccion?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Mi direccion", Toast.LENGTH_LONG).show();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.cancel();
            }
        });
        return builder.create();
    }

    public void compartirRedes(){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("email"));
        String[]s={"kquispe07@gmail.com"};
        intent.putExtra(Intent.EXTRA_EMAIL,s);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Helpme");
        intent.putExtra(Intent.EXTRA_TEXT,"Necesito ayuda urgente porfavor estoy en:" +direccion);
        intent.setType("message/ffc822");
        //compartir ws
        Intent intent1 = new Intent(Intent.ACTION_SEND);
        intent1.setType("text/plain");
        intent1.putExtra(Intent.EXTRA_TEXT,"Necesito ayuda urgente porfavor estoy en:" +direccion);
        intent1.setPackage("com.whatsapp");
        Intent chooser=Intent.createChooser(intent1,"Compartir por WhatsApp");
        startActivity(chooser);
    }
    //config marcador
    public void MarcadorDefault() {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(10, 10))
                .title("Hello world"));
    }

}