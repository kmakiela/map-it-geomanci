package com.example.kbak.mapitfresh;

import android.gesture.GestureOverlayView;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.widget.TextView;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.search.Address;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.ReverseGeocodeRequest;

import java.util.List;

public class RouteOnGestureListener implements GestureOverlayView.OnGestureListener, MapGesture.OnGestureListener {
    private Map map;
    private TextView addressField;
    private TextView countryField;
    private ActualPosition actualPosition;


    RouteOnGestureListener(Map map, TextView addressField, TextView countryField, ActualPosition actualPosition) {
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
        for (ViewObject viewObj : objects) {
            if (viewObj.getBaseType() == ViewObject.Type.USER_OBJECT) {
                if (((MapObject)viewObj).getType() == MapObject.Type.ROUTE) {
                    // At this point we have the originally added
                    // map marker, so we can do something with it
                    // (like change the visibility, or more
                    // marker-specific actions)
                    ((MapObject)viewObj).setVisible(false);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onTapEvent(PointF p) {
        double level = map.getMinZoomLevel() + map.getMaxZoomLevel() / 2;
        GeoCoordinate position = map.pixelToGeo(p);
        map.setCenter(new GeoCoordinate(position),
                Map.Animation.LINEAR);
        map.setZoomLevel(level);
        ResultListener<Address> listener = new TapGeocodeListener(addressField, countryField);
        ReverseGeocodeRequest request = new ReverseGeocodeRequest(position);
        actualPosition.position = position;
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
