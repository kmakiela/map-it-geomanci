package com.example.kbak.mapitfresh;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapCircle;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.RouteManager;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static java.lang.Thread.sleep;

public class BasicMapActivity extends AppCompatActivity {

    // map embedded in the map fragment
    private Map map = null;

    // map fragment embedded in this activity
    private MapFragment mapFragment = null;
    private TextView addressField = null;
    private TextView countryField = null;
    private Button okButton = null;
    private Button sendButton = null;

    private List<GeoCoordinate> wayPoints = new LinkedList<>();
    private ActualPosition actualPosition;

    RouteManager rm = new RouteManager();
    RoutePlan routePlan = new RoutePlan();
    RoutePlan routePlan2 = new RoutePlan();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
        RouteOptions options = new RouteOptions();
        options.setTransportMode(RouteOptions.TransportMode.PEDESTRIAN);
        routePlan2.setRouteOptions(options);
        routePlan.setRouteOptions(options);
    }

    private void initialize() {
        setContentView(R.layout.activity_basic_map);

        // Search for the map fragment to finish setup by calling init().
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        addressField = (TextView) findViewById(R.id.address);
        countryField = (TextView) findViewById(R.id.country);
        okButton = (Button) findViewById(R.id.ok_button);
        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setEnabled(false);

        actualPosition = new ActualPosition();

        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    map = mapFragment.getMap();
                    map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                    map.setCenter(new GeoCoordinate(50.099433, 19.9955930, 0), Map.Animation.NONE);
                    mapFragment.getMapGesture().addOnGestureListener(
                            new MapOnGestureListener(map, addressField, countryField, actualPosition));
                } else {
                    System.out.println("ERROR: Cannot initialize Map Fragment");
                }
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MapCircle m_circle = new MapCircle(70.0, actualPosition.position);
                m_circle.setLineColor(Color.BLACK);
                m_circle.setFillColor(Color.RED);
                m_circle.setLineWidth(2);
                map.addMapObject(m_circle);

                wayPoints.add(actualPosition.position);
                routePlan.addWaypoint(actualPosition.position);
                if (wayPoints.size() >= 4)
                    sendButton.setEnabled(true);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                map.setZoomLevel(map.getMinZoomLevel() + map.getMaxZoomLevel() / 2);

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
                //rp.add("waypoints", gson.toJson(wayPoints));

                RESTprovider.get("/?query=" + gson.toJson(wayPoints), null, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("failJSON", throwable.toString());
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String response) {
                        Log.d("succJSON", response.toString());
                        sendButton.setText("Success!");
                        //Intent intent = new Intent(BasicMapActivity.this, ChooseRouteActivity.class);
                        //startActivity(intent);
                    }
                });
                try{
                    sleep(60000);

                } catch(InterruptedException e ){
                    e.printStackTrace();
                }
             RESTprovider.get("/result", null, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("failJSON2", throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String response) {
                    Log.d("succJSON2", response.toString());
                    JsonElement elem = new JsonParser().parse(response);
                    JsonObject jobj = elem.getAsJsonObject();
                    JsonArray jarr = jobj.getAsJsonArray("route");
                    Float smog = jobj.getAsJsonPrimitive("smog_intake").getAsFloat();
                    for( JsonElement job : jarr){
                        Log.d("decode json", job.toString());
                        Float lat = job.getAsJsonObject().getAsJsonPrimitive("lat").getAsFloat();
                        Float lon = job.getAsJsonObject().getAsJsonPrimitive("lon").getAsFloat();
                        routePlan2.addWaypoint(new GeoCoordinate(lat, lon));
                    }




                    sendButton.setText("Success!");
                    //Intent intent = new Intent(BasicMapActivity.this, ChooseRouteActivity.class);
                    //startActivity(intent);
                }
            });
                rm.calculateRoute(routePlan, new RouteListener());
                rm.calculateRoute(routePlan2, new RouteListener());

            }
        });
    }
}
