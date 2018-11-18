package com.example.kbak.mapitfresh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class BasicMapActivity extends AppCompatActivity {

    // map embedded in the map fragment
    private Map map = null;

    // map fragment embedded in this activity
    private MapFragment mapFragment = null;
    private TextView addressField = null;
    private Button okButton = null;

    private List<GeoCoordinate> wayPoints = new LinkedList<>();
    private ActualPosition actualPosition = new ActualPosition();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {
        setContentView(R.layout.activity_basic_map);

        // Search for the map fragment to finish setup by calling init().
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        addressField = (TextView) findViewById(R.id.address);
        okButton = (Button) findViewById(R.id.ok_button);

        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    // retrieve a reference of the map from the map fragment
                    map = mapFragment.getMap();
                    // Set the map center to the Vancouver region (no animation)
                    map.setCenter(new GeoCoordinate(49.196261, -123.004773, 0.0),
                            Map.Animation.NONE);
                    // Set the zoom level to the average between min and max
                    map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                    mapFragment.getMapGesture().addOnGestureListener(new MapOnGestureListener(map, addressField, actualPosition));


                } else {
                    System.out.println("ERROR: Cannot initialize Map Fragment");
                }

            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                wayPoints.add(actualPosition.position);
            }
        });
    }
}
