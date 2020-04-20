package com.example.fyp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {
    private GPSTracker gpsTracker;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, null);

        //call this method to check gps enable or not
        setLocation();

        return rootView;
    }

    public void setLocation(){
        gpsTracker = new GPSTracker(getContext());

        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            //position found, show in map
            setMap(latitude,longitude);
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
    }
    //this method is to show map
    public void setMap(final double latitude, final double longitude){
        MapView mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(
                new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googlemap) {
                        final GoogleMap map = googlemap;

                        MapsInitializer.initialize(getContext());
                        //change map type as your requirements
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        //user will see a blue dot in the map at his location
                        map.setMyLocationEnabled(true);
                        LatLng marker =new LatLng(latitude, longitude);
                        LatLng marker_2 = new LatLng(1.4297, 103.7961);

                        //move the camera default animation
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));

                        //add a default marker in the position
                        map.addMarker(new MarkerOptions()
                                .position(marker));

                        map.addMarker(new MarkerOptions().position(marker_2).title("Vista Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mappin_40)));

                    }
                }
        );
    }

}