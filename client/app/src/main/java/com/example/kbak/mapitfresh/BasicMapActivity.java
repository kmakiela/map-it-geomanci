package com.example.kbak.mapitfresh;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.RouteManager;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

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
                    map = mapFragment.getMap();
                    map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                    map.setCenter(new GeoCoordinate(50.099433, 19.9955930, 0), Map.Animation.NONE);
                    mapFragment.getMapGesture().addOnGestureListener(new MapOnGestureListener(map, addressField, countryField, actualPosition));
                } else {
                    System.out.println("ERROR: Cannot initialize Map Fragment");
                }

            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                wayPoints.add(actualPosition.position);
                routePlan.addWaypoint(actualPosition.position);
                if (wayPoints.size() >= 10)
                    sendButton.setEnabled(true);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                class RouteListener implements RouteManager.Listener {
                    public void onProgress(int percentage) {
                    }

                    public void onCalculateRouteFinished(RouteManager.Error error, List<RouteResult> routeResult) {
                        if (error == RouteManager.Error.NONE) {
                            MapRoute mapRoute = new MapRoute(routeResult.get(0).getRoute());
                            map.addMapObject(mapRoute);
                        } else {
                        }
                    }
                }

                RequestParams rp = new RequestParams();
                Gson gson = new Gson();
                rp.add("waypoints", gson.toJson(wayPoints));

                RESTprovider.get("/?query=", rp, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("postObjectJSON", throwable.toString());
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String response) {
                            Log.d("postObjectJSON", response.toString());
                            sendButton.setText("Success!");

                    }
                });

                rm.calculateRoute(routePlan, new RouteListener());
            }
        });
    }
}
