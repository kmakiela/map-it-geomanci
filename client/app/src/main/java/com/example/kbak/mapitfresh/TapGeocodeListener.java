package com.example.kbak.mapitfresh;

import android.widget.TextView;

import com.here.android.mpa.search.Address;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.ResultListener;

public class TapGeocodeListener implements ResultListener<Address> {
    private TextView addressField;

    TapGeocodeListener(TextView addressText) {
        this.addressField = addressText;
    }

    @Override
    public void onCompleted(Address data, ErrorCode error) {
        if (error != ErrorCode.NONE) {
        } else {
            addressField.setText(data.getText());

        }
    }
}
