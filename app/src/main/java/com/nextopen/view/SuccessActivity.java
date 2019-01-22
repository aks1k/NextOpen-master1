package com.nextopen.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.nextopen.R;
import com.nextopen.business.KeyOperationClient;
import com.nextopen.common.util.BluetoothLeDeviceStore;
import com.nextopen.common.util.BluetoothLeScanner;
import com.nextopen.common.util.BluetoothUtils;
import com.nextopen.common.util.IContants;
import com.nextopen.common.util.StringUtils;
import com.nextopen.common.util.Utility;
import com.nextopen.vo.IBeaconItem;
import com.nextopen.vo.KeyOperationRequestVO;
import com.nextopen.vo.KeyOperationResponseVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconType;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconUtils;
import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconDevice;

public class SuccessActivity extends Activity implements IContants {
    private Context mContext;
    private ProgressBar mProgressBar;
    private String mQRValidityDate = "Not Found!";
    private String mQRCodeFirst;
    private String mQRCodeSecond;
    private long mUserId;

    private double mLatitude;
    private double mLongitude;
    private double mAltitude;
    private FirebaseAnalytics mFirebaseAnalytics;


    private KeyOperationClient client;

    private FusedLocationProviderClient mFusedLocationClient;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private BluetoothUtils mBluetoothUtils;
    private com.nextopen.common.util.BluetoothLeScanner mScanner;
    private BluetoothLeDeviceStore mDeviceStore;
    private BluetoothLeAdvertiser advertiser;
    private ScanCallback mScanCallback;
    private android.bluetooth.le.BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private String uuid;
    private String major, minor;
    private String dateTime;

    private BluetoothGatt mGatt;
    private boolean mConnected;
    private boolean mEchoInitialized;
    private Map<String, BluetoothDevice> mScanResults;
    private BluetoothDevice mDevice;
    private Handler mHandler;
    private int mRetry;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        mDeviceStore = new BluetoothLeDeviceStore();
        mBluetoothUtils = new BluetoothUtils(this);
        mScanner = new BluetoothLeScanner(mLeScanCallback, mBluetoothUtils);
        mContext = this;

        mProgressBar = findViewById(R.id.success_progress);
        TextView txtMsg = findViewById(R.id.txt_sucess_wait);
        final SharedPreferences sharedPref = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mQRCodeFirst = sharedPref.getString(PREF_QR1, STRING_BLANK);
        mQRCodeSecond = sharedPref.getString(PREF_QR2, STRING_BLANK);
        mUserId = sharedPref.getLong(PREF_USERID, 0);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

        Intent mIntent = getIntent();
        if (mIntent != null && mIntent.hasExtra("isAuto")) {
            txtMsg.setText(getResources().getString(R.string.txt_auto_nav));
        }

        if (!Utility.isGPSEnabled(this)) {
            Utility.alertDialog(this, mPositiveListener, new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    showPermissionAlert();
                }
            }, "Please enable GPS to get your location.");
        } else {
            showPermissionAlert();
        }
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mDeviceStore = new BluetoothLeDeviceStore();
        mBluetoothUtils = new BluetoothUtils(this);
        mScanner = new BluetoothLeScanner(mLeScanCallback, mBluetoothUtils);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        eventLog("onCreate");
        mHandler = new Handler();
    }

    private void eventLog(String type) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "SuccessActivity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void getLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                mLatitude = location.getLatitude();
                                mLongitude = location.getLongitude();
                                mAltitude = location.getAltitude();
                            }
                        }
                    });
        } catch (SecurityException se) {
            Utility.showProgress(false, mProgressBar, mContext);
            Toast.makeText(mContext, getString(R.string.error_locatoin_permission), Toast.LENGTH_SHORT).show();
        } catch (Exception se) {
            Utility.showProgress(false, mProgressBar, mContext);
            Toast.makeText(mContext, getString(R.string.error_internal), Toast.LENGTH_SHORT).show();
        }
    }

    private DialogInterface.OnClickListener mPositiveListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(callGPSSettingIntent, 101);
            dialog.dismiss();
        }
    };

    private void showDialogSuccess() {
        LayoutInflater inflater = getLayoutInflater();
        View qrScanAgainPopupView = inflater.inflate(R.layout.layout_location_success, null);
        final Button btnScan = qrScanAgainPopupView.findViewById(R.id.btn_location_success);
        final ImageView imgClose = qrScanAgainPopupView.findViewById(R.id.img_location_success_close);

        final Dialog alert = new Dialog(this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.setContentView(qrScanAgainPopupView);

        alert.setCancelable(false);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent otpIntent = new Intent(SuccessActivity.this, FeedActivity.class);
                SuccessActivity.this.startActivity(otpIntent);
                SuccessActivity.this.finish();
                alert.dismiss();
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent otpIntent = new Intent(SuccessActivity.this, FeedActivity.class);
                SuccessActivity.this.startActivity(otpIntent);
                alert.dismiss();
            }
        });
        //AlertDialog dialog = alert.create();
        alert.show();
    }

    public class KeyOperationTask extends AsyncTask<KeyOperationRequestVO, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(KeyOperationRequestVO... requestVOS) {
            client = new KeyOperationClient();

            /*
              "userID":12345,
              "gateMajor":"66645",
              "gateMinor":"43432",
              "gateTime":"2017-11-15T11:56:01+02:00",
              "userAltitude":65.61505098606652,
              "userLatitude":32.09379536846189,
              "longitude":34.7789439297835,
              "userQRcode1":"AB123456220518",
              "userQRcode2":"AB123455350518"
             */

            client.manageRequest(requestVOS[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            KeyOperationResponseVO keyOperationResponseVO = client.getKeyOperationResponseVO();

            if (keyOperationResponseVO != null && keyOperationResponseVO.getResponseCode() == 200) {
                showDialogSuccess();
            } else {
                Toast.makeText(mContext, getString(R.string.error_internal), Toast.LENGTH_SHORT).show();
            }
            Utility.showProgress(false, mProgressBar, mContext);

            //System.out.print("Response: "+ responseVO.getMobileCountry());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getLocation();
        showPermissionAlert();
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private void showPermissionAlert() {

        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(SuccessActivity.this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect beacons in the background.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @RequiresApi(Build.VERSION_CODES.M)
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_REQUEST_COARSE_LOCATION);
                }

            });
            builder.show();

        } else {

            // Trigger ble scanning
            startScanPrepare();
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "coarse location permission granted");
//                    beaconManager.bind(this);
                    // trigger ble scanning
                    startScanPrepare();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            dialog.dismiss();
                        }

                    });
                    builder.show();
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mScanner.isScanning())
            mScanner.scanLeDevice(-1, false);
        disconnectGattServer();
        eventLog("onPause");
    }


    ///// another library
    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            TextView textView=findViewById(R.id.textView);
            TextView textView2=findViewById(R.id.textView2);
            TextView textView3=findViewById(R.id.textView3);
            TextView textView4;
            final BluetoothLeDevice deviceLe = new BluetoothLeDevice(device, rssi, scanRecord, System.currentTimeMillis());
            mDeviceStore.addDevice(deviceLe);
            IBeaconItem detectedBeacon = null;
            String UUID = "BDC61939-7124-47EE-9051-0E64A171DB35".toLowerCase();
            Toast.makeText(mContext,"Scanning", Toast.LENGTH_LONG).show();
            for (final BluetoothLeDevice leDevice : mDeviceStore.getDeviceList()) {
                if (BeaconUtils.getBeaconType(leDevice) == BeaconType.IBEACON) {

                    // take the first beacon scanned and break
                    detectedBeacon = new IBeaconItem(new IBeaconDevice(leDevice));
                    Log.d("Beacon Reading", detectedBeacon.getDevice().getMajor() + " rssi: " + detectedBeacon.getDevice().getRunningAverageRssi());
                    Toast.makeText(mContext, detectedBeacon.getDevice().getUUID(), Toast.LENGTH_LONG).show();
                    if( detectedBeacon.getDevice().getUUID().equals(UUID) ||detectedBeacon.getDevice().getUUID().equals(UUID.toUpperCase()) ){
                        String temp = "Major: " + detectedBeacon.getDevice().getMajor() + " Minor: " + detectedBeacon.getDevice().getMinor()
                                + " UUID: " + detectedBeacon.getDevice().getUUID();
                        uuid = detectedBeacon.getDevice().getUUID();
                        major = String.valueOf(detectedBeacon.getDevice().getMajor());
                        minor = String.valueOf(detectedBeacon.getDevice().getMinor());
                        textView.setText("Uuid "+uuid);
                        textView2.setText("Major "+major);
                        textView3.setText("Minor " + minor);
                    }

                 //    break;
                    //itemList.add(new IBeaconItem(new IBeaconDevice(leDevice)));
                }
            }

         /*  if (detectedBeacon != null) {
                // on first detection stop scanning

               // mScanner.scanLeDevice(-1, false);

                // send the beacon major and minor to server
                //sendKeyOperatorRequest(detectedBeacon.getDevice().getMajor(), detectedBeacon.getDevice().getMinor());

                String temp = "Major: " + detectedBeacon.getDevice().getMajor() + " Minor: " + detectedBeacon.getDevice().getMinor()
                        + " UUID: " + detectedBeacon.getDevice().getUUID();
                Toast.makeText(mContext, temp, Toast.LENGTH_SHORT).show();
                eventLog("iBeacon Detected");
                eventLog(temp);
                if (mScanner.isScanning())
                    mScanner.scanLeDevice(-1, false);
                scanTransmitter();
            } else {
                //Toast.makeText(mContext, "No Device Found", Toast.LENGTH_LONG).show();
            }
             */
        }

    };

    private void prepareTransmit() {
        String payload = "NOTICKET";
        Date date = new Date();
        Log.d("date", date.toString());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        Log.d("date", " New Date: " + sdf.format(date));
        dateTime = sdf.format(date);
        if (!TextUtils.isEmpty(mQRCodeFirst)) {
            payload = mUserId+major+minor+dateTime+mQRCodeFirst; // Create transmit payload using QR Code 1
            if (!TextUtils.isEmpty(mQRCodeSecond)) {
                payload +=mQRCodeSecond;
            }
        } else if (!TextUtils.isEmpty(mQRCodeSecond)) {
            payload = mUserId+major+minor+dateTime+mQRCodeSecond; // Create transmit payload using QR Code 2
        }
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode( AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY )
                .setTxPowerLevel( AdvertiseSettings.ADVERTISE_TX_POWER_HIGH )
                .setConnectable( true )
                .build();

        ParcelUuid pUuid = new ParcelUuid( UUID.fromString(getResources().getString(R.string.ble_uuid)) );

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName( false )
                .addServiceData( pUuid, payload.getBytes() )
                .build();

        AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                sendKeyOperatorRequest();
                Log.d( "BLE", "Advertising onStart " );

            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.d( "BLE", "Advertising onStartFailure: " + errorCode );
                Toast.makeText(SuccessActivity.this,"Data too large",Toast.LENGTH_LONG).show();
                super.onStartFailure(errorCode);
            }
        };

        advertiser.startAdvertising( settings, data, advertisingCallback );
    }

    private void scanTransmitter() {
        eventLog("Scan Transmitter");
        mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                mDevice = result.getDevice();

            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
//                for (ScanResult result : results) {
//                    addScanResult(result);
//                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        ScanFilter scanFilter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(BluetoothUtils.SERVICE_UUID))
                .build();
        List<ScanFilter> filters = new ArrayList<>();
        filters.add(scanFilter);

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();

        mBluetoothLeScanner.startScan(filters,settings,mScanCallback);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
             //   mBluetoothLeScanner.stopScan(mScanCallback);
                connectDevice(mDevice);
            }
        },5000);
    }

    private void connectDevice(BluetoothDevice device) {
        if (device != null) {
            log("Connecting to " + device.getAddress());
            eventLog("Connecting to ad server");
            GattClientCallback gattClientCallback = new GattClientCallback();
            mGatt = device.connectGatt(this, false, gattClientCallback);
        } else {
            if (mRetry <3) {
                mRetry++;
                eventLog("Scan Transmitter retry");
                scanTransmitter();
            } else {
                eventLog("Advertise transmitter connection failed");
                Toast.makeText(this,"Enable to find device to transmit data",Toast.LENGTH_SHORT).show();
                sendKeyOperatorRequest();
            }
        }
    }

    public void disconnectGattServer() {
        mConnected = false;
        mEchoInitialized = false;
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
        }
    }
    private void sendKeyOperatorRequest() {
        KeyOperationRequestVO requestVO = new KeyOperationRequestVO();
        requestVO.setUserID(mUserId);
        requestVO.setGateMajor(major);
        requestVO.setGateMinor(minor);

        requestVO.setGateTime(dateTime);
        requestVO.setUserAltitude(mAltitude);
        requestVO.setUserLatitude(mLatitude);
        requestVO.setLongitude(mLongitude);
        requestVO.setUserQRcode1(mQRCodeFirst);
        requestVO.setUserQRcode2(mQRCodeSecond);
        new KeyOperationTask().execute(requestVO);
    }

    private void startScanPrepare() {
        startScan();
      //  Utility.showProgress(true, mProgressBar, mContext);
        // }
    }

    private void startScan() {
        disconnectGattServer();
        mScanResults = new HashMap<>();
        final boolean isBluetoothOn = mBluetoothUtils.isBluetoothOn();
        final boolean isBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();
        mDeviceStore.clear();

        mBluetoothUtils.askUserToEnableBluetoothIfNeeded();
        if (isBluetoothOn && isBluetoothLePresent) {
            mScanner.scanLeDevice(-1, true);
            invalidateOptionsMenu();
        }
    }

    private class GattClientCallback extends BluetoothGattCallback {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            log("onConnectionStateChange newState: " + newState);

            if (status == BluetoothGatt.GATT_FAILURE) {
                log("Connection Gatt failure status " + status);
                eventLog("Connection Gatt failed");
                disconnectGattServer();
//                reconnect();
                return;
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                // handle anything not SUCCESS as failure
                log("Connection not GATT sucess status " + status);
                disconnectGattServer();
//                reconnect();
                return;
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                eventLog("Connection Gatt Connected");
                log("Connected to device " + gatt.getDevice().getAddress());
                setConnected(true);
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                log("Disconnected from device");
                disconnectGattServer();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status != BluetoothGatt.GATT_SUCCESS) {
                log("Device service discovery unsuccessful, status " + status);
                return;
            }

            List<BluetoothGattCharacteristic> matchingCharacteristics = BluetoothUtils.findCharacteristics(gatt);
            if (matchingCharacteristics.isEmpty()) {
                eventLog("UUID not matched");
                log("Unable to find characteristics.");
                return;
            }

            log("Initializing: setting write type and enabling notification");
            for (BluetoothGattCharacteristic characteristic : matchingCharacteristics) {
                characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                enableCharacteristicNotification(gatt, characteristic);
            }

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendMessage();
                }
            },500);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("Characteristic written successfully");
            } else {
                log("Characteristic write unsuccessful, status: " + status);
                disconnectGattServer();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("Characteristic read successfully");
                readCharacteristic(characteristic);
            } else {
                log("Characteristic read unsuccessful, status: " + status);
                // Trying to read from the Time Characteristic? It doesnt have the property or permissions
                // set to allow this. Normally this would be an error and you would want to:
                // disconnectGattServer();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            log("Characteristic changed, " + characteristic.getUuid().toString());
            readCharacteristic(characteristic);
        }

        private void enableCharacteristicNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            boolean characteristicWriteSuccess = gatt.setCharacteristicNotification(characteristic, true);
            if (characteristicWriteSuccess) {
                log("Characteristic notification set successfully for " + characteristic.getUuid().toString());
                if (BluetoothUtils.isEchoCharacteristic(characteristic)) {
                    initializeEcho();
                }
            } else {
                log("Characteristic notification set failure for " + characteristic.getUuid().toString());
            }
        }

        private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
            byte[] messageBytes = characteristic.getValue();
            log("Read: " + StringUtils.byteArrayInHexFormat(messageBytes));
            String message = StringUtils.stringFromBytes(messageBytes);
            if (message == null) {
                log("Unable to convert bytes to string");
                return;
            }

            log("Received message: " + message);
        }
    }

    private void sendMessage() {
        if (!mConnected || !mEchoInitialized) {
            return;
        }
        eventLog("Send Advertise");
        BluetoothGattCharacteristic characteristic = BluetoothUtils.findEchoCharacteristic(mGatt);
        if (characteristic == null) {
            log("Unable to find echo characteristic.");
            disconnectGattServer();
            return;
        }

        String payload = "NOTICKET";
        Date date = new Date();
        Log.d("date", date.toString());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        Log.d("date", " New Date: " + sdf.format(date));
        dateTime = sdf.format(date);
        if (!TextUtils.isEmpty(mQRCodeFirst)) {
            payload = mUserId+","+major+","+minor+","+dateTime+","+mQRCodeFirst; // Create transmit payload using QR Code 1
            if (!TextUtils.isEmpty(mQRCodeSecond)) {
                payload +=","+mQRCodeSecond;
            }
        } else if (!TextUtils.isEmpty(mQRCodeSecond)) {
            payload = mUserId+major+minor+dateTime+mQRCodeSecond; // Create transmit payload using QR Code 2
        }
        log("Sending message: " + payload);
        Toast.makeText(SuccessActivity.this, payload,Toast.LENGTH_LONG).show();

        byte[] messageBytes = StringUtils.bytesFromString(payload);
        if (messageBytes.length == 0) {
            log("Unable to convert message to bytes");
            return;
        }

        characteristic.setValue(messageBytes);
        boolean success = mGatt.writeCharacteristic(characteristic);
        if (success) {
            log("Wrote: " + StringUtils.byteArrayInHexFormat(messageBytes));
            eventLog("Advertise send successfully");
            sendKeyOperatorRequest();
        } else {
            eventLog("Failed to send Advertise");
            log("Failed to write data");
        }
    }

    private void reconnect() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connectDevice(mDevice);
            }
        },500);
    }

    public void setConnected(boolean connected) {
        mConnected = connected;
    }

    public void initializeEcho() {
        mEchoInitialized = true;
    }

    public void log(String msg) {
        Log.d("TAG", msg);
    }
}
