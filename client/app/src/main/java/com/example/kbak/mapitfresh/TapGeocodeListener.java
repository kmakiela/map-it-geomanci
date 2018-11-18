package com.example.kbak.mapitfresh;

import android.widget.TextView;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.search.Address;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.ReverseGeocodeRequest;

import java.util.List;

public class TapGeocodeListener implements ResultListener<Address> {
    private TextView addressField;
    private TextView countryField;

    TapGeocodeListener(TextView addressField, TextView countryField) {
        this.addressField = addressField;
        this.countryField = countryField;
    }

    @Override
    public void onCompleted(Address data, ErrorCode error) {
        if (error != ErrorCode.NONE) {
        } else {
            addressField.setText(data.getStreet() + " " + data.getHouseNumber());
            countryField.setText(data.getPostalCode() + " " + data.getCountryName());
        }
    }
}
