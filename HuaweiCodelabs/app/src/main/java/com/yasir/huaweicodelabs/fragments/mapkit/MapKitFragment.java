package com.yasir.huaweicodelabs.fragments.mapkit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.CameraPosition;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.MarkerOptions;
import com.yasir.huaweicodelabs.R;
import com.yasir.huaweicodelabs.Utilities.AppLog;
import com.yasir.huaweicodelabs.fragments.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapKitFragment extends BaseFragment implements OnMapReadyCallback {

    @BindView(R.id.mapView)
    MapView mMapView;

    private View rootView;
    private HuaweiMap mHuaweiMap;
    private LatLng riyadhCoordinates = new LatLng(24.730961, 46.727261);
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    public static MapKitFragment newInstance() {
        return new MapKitFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMainActivity().setHeading("Map Kit");

        // Map initialization
        initMap(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_map_kit, container, false);
        } else {
            container.removeView(rootView);
        }

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    private void initMap(Bundle savedInstanceState) {
        // Get mapView instance
        Bundle mapViewBundle = null;

        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);  // Get map instance
    }

    @Override
    public void onMapReady(HuaweiMap map) {
        AppLog.Debug(MapKitFragment.class.getSimpleName(), "onMapReady");

        // Get HuaweiMap instance in this call back method
        mHuaweiMap = map;

        // Marker Click Listener
        mHuaweiMap.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return true;
        });

        addMarkerLocation();
    }

    private void addMarkerLocation() {
        if (mHuaweiMap != null) {
            // Adding User location
            MarkerOptions markerOptions = new MarkerOptions().position(riyadhCoordinates).title("Riyadh").snippet("This is marker location");
            mHuaweiMap.addMarker(markerOptions);
            CameraPosition build = new CameraPosition.Builder().target(riyadhCoordinates).zoom(15).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(build);
            mHuaweiMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
