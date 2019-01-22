package com.nextopen.common.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.nextopen.R;

public class QRDialog extends Dialog implements DialogInterface.OnClickListener{
    private Button btnScan;
    private Context mContext;

    public QRDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.layout_qr_scan);
         btnScan = findViewById(R.id.btn_scan_again);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Please scan QR code.", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
