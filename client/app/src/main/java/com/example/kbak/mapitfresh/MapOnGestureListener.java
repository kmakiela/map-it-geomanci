package com.example.kbak.mapitfresh;

import android.app.Activity;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.MapCircle;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.search.Address;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.ReverseGeocodeRequest;

import java.io.IOException;
import java.util.List;

public class MapOnGestureListener implements GestureOverlayView.OnGestureListener, MapGesture.OnGestureListener {
    private Map map;
    private TextView addressField;
    private TextView countryField;
    private ActualPosition actualPosition;


    MapOnGestureListener(Map map, TextView addressField, TextView countryField, ActualPosition actualPosition) {
        this.map = map;
        this.addressField = addressField;
        this.countryField = countryField;
        this.actualPosition = actualPosition;
    }

    @Override
    public void onPanStart() {
    }

    @Override
    public void onPanEnd() {
    }

    @Override
    public void onMultiFingerManipulationStart() {
    }

    @Override
    public void onMultiFingerManipulationEnd() {
    }

    @Override
    public boolean onMapObjectsSelected(List<ViewObject> objects) {
        return false;
    }

    @Override
    public boolean onTapEvent(PointF p) {
        double level = map.getMinZoomLevel() + map.getMaxZoomLevel() / 2;
        GeoCoordinate position = map.pixelToGeo(p);
        map.setCenter(new GeoCoordinate(position), Map.Animation.LINEAR);
        map.setZoomLevel(level * 2, Map.Animation.LINEAR);
        ResultListener<Address> listener = new TapGeocodeListener(addressField, countryField);
        ReverseGeocodeRequest request = new ReverseGeocodeRequest(position);
        actualPosition.position = position;

        if (actualPosition.marker != null) {
            map.removeMapObject(actualPosition.marker);
        }
        MapCircle m_circle = new MapCircle(100.0, new GeoCoordinate(position));
        m_circle.setLineColor(Color.BLACK);
        m_circle.setFillColor(Color.MAGENTA);
        m_circle.setLineWidth(2);
        actualPosition.marker = m_circle;
        map.addMapObject(actualPosition.marker);

        if (request.execute(listener) != ErrorCode.NONE){
            addressField.setText("Invalid starting point");
        }
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(PointF p) {
        return false;
    }

    @Override
    public void onPinchLocked() {
    }

    @Override
    public boolean onPinchZoomEvent(float scaleFactor, PointF p) {
        return false;
    }

    @Override
    public void onRotateLocked() {
    }

    @Override
    public boolean onRotateEvent(float rotateAngle) {
        return false;
    }

    @Override
    public boolean onTiltEvent(float angle) {
        return false;
    }

    @Override
    public boolean onLongPressEvent(PointF p) {
        return false;
    }

    @Override
    public void onLongPressRelease() {
    }

    @Override
    public boolean onTwoFingerTapEvent(PointF p) {
        return true;
    }

    @Override
    public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {

    }

    @Override
    public void onGesture(GestureOverlayView overlay, MotionEvent event) {

    }

    @Override
    public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {

    }

    @Override
    public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {

    }
}
