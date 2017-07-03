package net.multiform_music.geoloctuto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import net.multiform_music.geoloctuto.bus.GpsBus;
import net.multiform_music.geoloctuto.helper.DatabaseHelper;
import net.multiform_music.geoloctuto.service.GPSService;

import java.io.IOException;
import java.util.List;

import static net.multiform_music.geoloctuto.R.id.map;
import static net.multiform_music.geoloctuto.helper.ParamHelper.zoom;

public class FragmentMaps extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, LocationListener, LocationSource.OnLocationChangedListener {

    // objet de carte
    private GoogleMap mMap;

    // connexion à l'API et permission
    private GoogleApiClient mGoogleApiClient;

    public String mapType;
    public Integer idRunningLastPerformance;

    @Override
    public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
        return super.getLayoutInflater(savedInstanceState);
    }

    public static FragmentMaps newInstance() {
        return new FragmentMaps();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        // récupération du fragment map : conteneur de la carte
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(map);

        // définition méthode rappel sur le fragment dès que carte prête
        mapFragment.getMapAsync(this);

        // permet de manager connexion au google play service
        // lorsque la connexion est ok => on initialise la map setUpMap()
        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        return rootView;
    }


    @Override
    public void onLocationChanged(Location location) {

        LatLng locationLat = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLat, zoom));
        placeMarkerOnMap(new LatLng(location.getLatitude(), location.getLongitude()), R.drawable.icon_point_red_marker);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // récupération de l'objet de carte
        mMap = googleMap;

        setMapType();

        // listener pour savoir quand la carte a été chargé pour pouvoir chargé le parcour de la dernière performance
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {

                if (idRunningLastPerformance != null) {
                    writeMapsPath(idRunningLastPerformance);
                }

                // positionnement du listener pour changement zoom
                mMap.setOnCameraChangeListener(getCameraChangeListener());
            }
        });
    }

    public void setMapType () {

        if ("normal".equalsIgnoreCase(this.mapType.trim())) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if ("satellite".equalsIgnoreCase(this.mapType.trim())) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if ("hybrid".equalsIgnoreCase(this.mapType.trim())) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if ("terrain".equalsIgnoreCase(this.mapType.trim())) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
    }

    /**
     *
     * Dessine le tracé de la course avec centrage
     *
     * @param idRunning id
     */
    public void writeMapsPath(final int idRunning) {

        // nettoyage map des lignes précédentes
        mMap.clear();

        // coordonnées des points pour traçage
        List<LatLng> listRunningPoints = new DatabaseHelper(getActivity()).getAllRunningStepLocalisation(idRunning);

        // test si des points à tracer, cad si au moins une première course existe
        if (listRunningPoints != null && listRunningPoints.size() >0) {

            // points pour centrage carte
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            PolylineOptions polylineOptions = new PolylineOptions();

            int count = 1;
            for (LatLng point : listRunningPoints) {

                //placeMarkerOnMap(point, Integer.toString(count));
                if (count == 1) {
                    placeMarkerOnMap(point, R.drawable.icon_flag_start_size);
                } else if (count == listRunningPoints.size()) {
                    placeMarkerOnMap(point, R.drawable.icon_flag_stop_size);
                }
                polylineOptions.add(point);
                builder.include(point);
                count++;
            }

            // tracé de la course
            if (this.mapType.equalsIgnoreCase("satellite")) {
                polylineOptions.color(Color.GREEN);
            } else {
                polylineOptions.color(Color.BLUE);
            }
            polylineOptions.geodesic(true);
            polylineOptions.width(5);
            mMap.addPolyline(polylineOptions);

            // centrage de la carte sur le tracé
            LatLngBounds bounds = builder.build();
            int padding = 80;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            // animation de la caméra avec callback quand c'est fini
            mMap.animateCamera(cu, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {

                    // si l'activié est celle de Running => fin de course => prise screenshot
                    if (getActivity() instanceof RunningActivity) {
                        getBitmapFromMap(idRunning);
                    }
                }

                @Override
                public void onCancel() {

                }
            });
        } else {

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.866667, 2.333333), 1));
        }

    }

    /**
     *
     * Prend un screenshot de la carte et appelle la méthode de sauvegarde de l'image pour l'id running
     *
     * @param idRunning id
     */
    public void getBitmapFromMap(final int idRunning) {


        mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {

                ((RunningActivity) getActivity()).onGenerationMapToBitmap(idRunning, bitmap);
            }
        });
    }

    /**
     *
     * trace le chemin avec couleurs
     *
     * @param idRunning id
     */
    public void writePathColor(int idRunning) {

        // nettoyage map des lignes précédentes
        mMap.clear();

        // coordonnées des points pour traçage
        List<LatLng> listRunningPoints = new DatabaseHelper(getActivity()).getAllRunningStepLocalisation(idRunning);


        int size = listRunningPoints.size();
        PolylineOptions optline = new PolylineOptions();
        PolylineOptions optline2 = new PolylineOptions();
        optline.geodesic(true);
        optline.width(10);
        optline2.geodesic(true);
        optline2.width(10);
        for (int i = 0; i < size - 1; i++) {

            LatLng pointD = listRunningPoints.get(i);
            LatLng pointA = listRunningPoints.get(i + 1);
            int green = (int) ((float) 255 - (float) (i / (float) size) * (float) 255);
            int red = (int) ((float) 0 + (float) (i / (float) size) * (float) 255);

            optline.add(new LatLng(pointD.latitude, pointD.longitude), new LatLng(pointA.latitude, pointA.longitude));
            optline2.add(new LatLng(pointD.latitude, pointD.longitude), new LatLng(pointA.latitude, pointA.longitude));

            if(i%2 == 0){
                optline.color(Color.rgb(red, green, 0));
                mMap.addPolyline(optline);
                optline = new PolylineOptions();
                optline.geodesic(true);
                optline.width(10);
            }
            else{
                optline2.color(Color.rgb(red, green, 0));
                mMap.addPolyline(optline2);
                optline2 = new PolylineOptions();
                optline2.geodesic(true);
                optline2.width(10);
            }
        }
    }
    /**
     *
     * Listener sur changement position camera (zoom)
     *
     * @return
     */
    public GoogleMap.OnCameraChangeListener  getCameraChangeListener() {
        return new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition position) {

                if (!GPSService.calibration) {
                    zoom = position.zoom;
                }
            }
        };
    }

    /**
     *
     * Evennement envoyé par le GPSService sur le bus (GPSBUS) pour mise à jour sur maps
     *
     * @param locationEvent location
     */

    public void onEvent(Location locationEvent){

        onLocationChanged(locationEvent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {

        super.onResume();

        GpsBus.getInstance().register(this);

    }

    /**
     *
     * Retourne l'adresse d'une localisation
     *
     */
    private String getAddress( LatLng latLng ) {

        Geocoder geocoder = new Geocoder( getContext() );
        String addressText = "";
        List<Address> addresses;
        Address address;
        try {

            addresses = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 );

            if (null != addresses && !addresses.isEmpty()) {
                address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressText += (i == 0)?address.getAddressLine(i):("\n" + address.getAddressLine(i));
                }
            }
        } catch (IOException e ) {

            e.printStackTrace();
        }
        return addressText;
    }

    /**
     *
     * Retourne l'adresse d'une localisation
     *
     */
    public String getCityName( LatLng latLng ) {

        Geocoder geocoder = new Geocoder( getContext() );
        List<Address> addresses;
        Address address;
        String cityName = "";

        try {

            addresses = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 );

            if (null != addresses && !addresses.isEmpty()) {
                address = addresses.get(0);
                cityName = address.getLocality();
            }

        } catch (IOException e ) {

            e.printStackTrace();
        }
        return cityName;
    }

    /**
     *
     * Place un marker à la location LatLng avec icone
     *
     * @param location location
     * @param resource icone du marker
     */
    protected void placeMarkerOnMap(LatLng location, int resource) {

        MarkerOptions markerOptions = new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(resource));
        //String titleStr = getAddress(location);
        //markerOptions.title(titleStr);
        mMap.addMarker(markerOptions).showInfoWindow();
    }

    protected void placeMarkerOnMap(LatLng location, String numero) {

        MarkerOptions markerOptions = new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_point_red_marker));
        //String titleStr = getAddress(location);
        markerOptions.title(numero);
        mMap.addMarker(markerOptions).showInfoWindow();
    }


    @Override
    public void onStart() {

        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {

        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }

        GpsBus.getInstance().unregister(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public Integer getIdRunningLastPerformance() {
        return idRunningLastPerformance;
    }

    public void setIdRunningLastPerformance(Integer idRunningLastPerformance) {
        this.idRunningLastPerformance = idRunningLastPerformance;
    }
}
