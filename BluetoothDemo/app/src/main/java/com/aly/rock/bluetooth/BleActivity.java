package com.aly.rock.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aly.rock.bluetooth.adapter.DeviceAdapter;


public class BleActivity extends BaseActivity {
    private final String TAG = BleActivity.class.getName();
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    private DeviceAdapter deviceAdapter;
    private BluetoothAdapter bluetoothAdapter;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private boolean mScanning = false;
    private BluetoothLeScanner bleScanner = null;
    private BluetoothGatt bluetoothGatt;
    private int connectionState = 0;
    private Handler handler = new Handler();
    public Context mContext = this;
    // Various callback methods defined by the BLE API.
    private final BluetoothGattCallback gattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    String intentAction;
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        intentAction = ACTION_GATT_CONNECTED;
                        connectionState = STATE_CONNECTED;
                        //broadcastUpdate(intentAction);
                        Log.i(TAG, "Connected to GATT server.");
                        Log.i(TAG, "Attempting to start service discovery:" +
                                bluetoothGatt.discoverServices());

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        intentAction = ACTION_GATT_DISCONNECTED;
                        connectionState = STATE_DISCONNECTED;
                        Log.i(TAG, "Disconnected from GATT server.");
                        //broadcastUpdate(intentAction);
                    }

                }
                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        //broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                    } else {
                        Log.w(TAG, "onServicesDiscovered received: " + status);
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic,
                                                 int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                    }
                }
            };

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile. Data
        // parsing is carried out as per profile specifications.
        if ("".equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
                        stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
        }
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            showToast("当前设备不支持蓝牙");
            finish();
            return;
        }else{
            bleScanner = bluetoothAdapter.getBluetoothLeScanner();
            Log.d(TAG,"getBluetoothLeScanner");
        }
        initView();
        startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BluetoothDevice device = deviceAdapter.getDevice(position);
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                bluetoothGatt = device.connectGatt(mContext, false, gattCallback);
            }
        }
    };
    private void initView() {
        ListView lv_deviceList = findViewById(R.id.lv_deviceList);
        deviceAdapter = new DeviceAdapter(this);
        lv_deviceList.setAdapter(deviceAdapter);
        lv_deviceList.setOnItemClickListener(itemClickListener);
    }
    private void startScan() {
        if (bluetoothAdapter.isEnabled()) {
            scanLeDevice(true);
        } else {
            Log.i(TAG,"蓝牙当前没有打开 ");
            if (bluetoothAdapter.enable()) {
                Log.i(TAG,"主动打开蓝牙并开始扫描 ");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scanLeDevice(true);
                    }
                }, 1500);
            } else {
                showToast("蓝牙打开失败");
            }
        }
    }
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.d(TAG,"[ble][ScanCallback] callbackType = " + callbackType);
            if(result !=null && result.getDevice() != null){

                BluetoothDevice device = result.getDevice();
                int deviceClass = device.getBluetoothClass().getDeviceClass();
                deviceAdapter.addDevice(device);
                deviceAdapter.notifyDataSetChanged();
                Log.d(TAG,"[ble][ScanCallback] onScanResult  name = " + device.getName());
                Log.d(TAG,"[ble][ScanCallback] onScanResult  mac = "+device.getAddress());
                Log.d(TAG, "[ble][ScanCallback] onScanResult  mac = " + Integer.toHexString(deviceClass));
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d(TAG,"[ble][ScanCallback] onScanResult  errorCode = " + errorCode);
        }
    };
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bleScanner.stopScan(scanCallback);
                }
            }, SCAN_PERIOD);
            mScanning = true;
            Log.d(TAG,"bleScanner.startScan");
            bleScanner.startScan(scanCallback);
        } else {
            mScanning = false;
            bleScanner.stopScan(scanCallback);
        }
    }

    //允许蓝牙被搜索
    private void disCoveredEnable() {
        if(bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent (BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300); //设置本机蓝牙在 300 秒之内可以被 搜索
            startActivity(discoverableIntent);
            Log.i(TAG,"设置蓝牙被搜索模式");
        } else {
            Log.i(TAG,"蓝牙模式是： " + bluetoothAdapter.getScanMode());
        }
    }
    public void close() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
    }
}