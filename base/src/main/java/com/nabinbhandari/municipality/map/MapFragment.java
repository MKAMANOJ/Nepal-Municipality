package com.nabinbhandari.municipality.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nabinbhandari.firebaseutils.RemoteConfig;
import com.nabinbhandari.municipality.R;

/**
 * Created at 8:19 PM on 1/7/2018.
 *
 * @author bnabin51@gmail.com
 */

public class MapFragment extends Fragment implements OnMapReadyCallback {
    MapView mapView;
    GoogleMap map;

    public MapFragment() {
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        View view = inflater.inflate(R.layout.fragment_map, null, false);

        // Gets the MapView from the XML layout and creates it
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Gets to GoogleMap from the MapView and does initialization stuff
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        LatLng municipality = RemoteConfig.getMunicipalityCoordinates();
        // Sets the map type to be "hybrid"
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(municipality, 13);
        map.addMarker(new MarkerOptions().position(municipality).title(getContext().getString(R.string.app_name)));
        map.moveCamera(CameraUpdateFactory.newLatLng(municipality));
        map.animateCamera(cameraUpdate);
    }
}
