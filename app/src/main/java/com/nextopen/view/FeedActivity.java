package com.nextopen.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nextopen.R;
import com.nextopen.common.util.IContants;
import com.nextopen.common.util.QRDialog;
import com.nextopen.common.util.Utility;
import com.nextopen.common.util.Validator;

public class FeedActivity extends Activity implements IContants, View.OnClickListener{

    private Context mContext;
    private IntentIntegrator mQRScan;
    private String mQRValidityDate = "Not Found!";
    private String mQRCodeFirst;
    private String mQRCodeSecond;
    private String[] mQRDateStrings;

    private Validator validator;

    private SharedPreferences sharedPref;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Button mBtnScan = findViewById(R.id.btn_qr_scan);
        Button mBtnAuto = findViewById(R.id.btn_auto);
        mBtnAuto.setOnClickListener(this);
        mBtnScan.setOnClickListener(this);

        mContext = this;
        mQRScan = new IntentIntegrator(this);
        mQRScan.setOrientationLocked(true);
        mQRScan.setCaptureActivity(QRCaptureActivity.class);

        validator = Validator.getInstance();

        sharedPref = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mQRCodeFirst = sharedPref.getString(PREF_QR1, STRING_BLANK);
        mQRCodeSecond = sharedPref.getString(PREF_QR2, STRING_BLANK);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null){
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
                showDialogFailQR();
            } else {
                String qrCode = result.getContents().trim();
                if(validator.isValidQRCode(qrCode)){
                    mQRDateStrings = Utility.getDateStrings(qrCode, false);
                    mQRValidityDate = mQRDateStrings[0]+SEPARATOR_DATE+mQRDateStrings[1]+SEPARATOR_DATE+mQRDateStrings[2];

                    if(mQRCodeFirst.equals(STRING_BLANK)){
                        mQRCodeFirst = qrCode;
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(PREF_QR1,mQRCodeFirst);
                        editor.apply();
                    }else if (mQRCodeSecond.equals(STRING_BLANK)) {
                        mQRCodeSecond = qrCode;
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(PREF_QR2,mQRCodeSecond);
                        editor.apply();
                    }else{
                        mQRCodeFirst = mQRCodeSecond;
                        mQRCodeSecond = qrCode;
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(PREF_QR2,mQRCodeSecond);
                        editor.apply();
                    }

                    showDialogSuccessQR();
                }else{
                    showDialogFailQR();
                }
                Toast.makeText(this, "QR Content: "+ result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_auto:
                Intent intent = new Intent(FeedActivity.this, SuccessActivity.class);
                intent.putExtra("isAuto",true);
                startActivity(intent);
                break;
            case R.id.btn_qr_scan:
                try {
                    mQRScan.initiateScan();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Popup Not Opened: ", e.getMessage());
                }
                break;
        }
    }

    private void showDialog(){
        new QRDialog(this).show();
    }

    private void showDialogScanQR(){
        LayoutInflater inflater = getLayoutInflater();
        View qrPopupView = inflater.inflate(R.layout.layout_qr_scan, null);
        final Button btnScan = (Button) qrPopupView.findViewById(R.id.btn_scan);

        final Dialog alert = new Dialog(this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.setContentView(qrPopupView);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, "Please scan QR code.", Toast.LENGTH_SHORT).show();
                mQRScan.initiateScan();
                alert.dismiss();
            }
        });
        //AlertDialog dialog = alert.create();
        alert.show();
    }

    private void showDialogSuccessQR(){
        LayoutInflater inflater = getLayoutInflater();
        View qrSuccessPopupView = inflater.inflate(R.layout.layout_qr_success, null);
        final ImageView imgClose = (ImageView) qrSuccessPopupView.findViewById(R.id.img_close);
        final TextView txtDate = (TextView) qrSuccessPopupView.findViewById(R.id.text_skipass_validity);
        final Button btnScan = (Button) qrSuccessPopupView.findViewById(R.id.btn_scan_again_success);

        final Dialog alert = new Dialog(this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.setContentView(qrSuccessPopupView);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        txtDate.setText(mQRValidityDate);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, "Please scan QR code.", Toast.LENGTH_SHORT).show();
                mQRScan.initiateScan();
                alert.dismiss();
            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, "Please scan QR code.", Toast.LENGTH_SHORT).show();
                //mQRScan.initiateScan();
                Intent otpIntent = new Intent(FeedActivity.this, SuccessActivity.class);
                FeedActivity.this.startActivity(otpIntent);
                alert.dismiss();
            }
        });
        //AlertDialog dialog = alert.create();
        alert.show();
    }

    private void showDialogFailQR(){
        LayoutInflater inflater = getLayoutInflater();
        View qrScanAgainPopupView = inflater.inflate(R.layout.layout_skipass_invalid, null);
        final Button btnScan = (Button) qrScanAgainPopupView.findViewById(R.id.btn_scan_again);
        final ImageView imgClose = (ImageView) qrScanAgainPopupView.findViewById(R.id.img_invalid_close);

        final Dialog alert = new Dialog(this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.setContentView(qrScanAgainPopupView);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, "Please scan QR code.", Toast.LENGTH_SHORT).show();
                mQRScan.initiateScan();
                alert.dismiss();
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, "Please scan QR code.", Toast.LENGTH_SHORT).show();
                //mQRScan.initiateScan();
                alert.dismiss();
            }
        });
        //AlertDialog dialog = alert.create();
        alert.show();
    }


}
