package com.vasko.pong;

import android.app.NativeActivity;
import android.Manifest;
import android.bluetooth.*;
import android.bluetooth.le.*;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.content.Context;

import java.util.UUID;
import java.nio.charset.StandardCharsets;

public class MainActivity extends NativeActivity
{
    private static final String TAG = "vpong";
    private static final String SERVICE_UUID = "6e053c45-71bb-4c40-9d82-cc3be83952da";
    private static final String CHARACTERISTIC_UUID = "b8af02f9-e29b-4576-9c5a-fd80b7eda684";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private BluetoothGatt bluetoothGatt;

	private byte[] currentValue = null;

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result)
		{
            BluetoothDevice device = result.getDevice();
			if(device.getName() == null) return;

            Log.d(TAG, "Found device: " + device.getName());
            if (device.getName() != null && device.getName().equals("ESP32_BLE_Smart_Device")) {
                scanner.stopScan(this);
                connectToDevice(device);
            }
        }
    };

	@Override
	public void onCreate(Bundle savedInstance)
	{
		//bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();
        scanner = bluetoothAdapter.getBluetoothLeScanner();
		super.onCreate(savedInstance);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { 

            java.util.List<String> permissionsToRequest = new java.util.ArrayList<>();

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
			{
				permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
                if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN);
                }
                if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT);
                }
                if (checkSelfPermission(Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(Manifest.permission.BLUETOOTH_ADVERTISE);
                }
            }

			if (permissionsToRequest.isEmpty()) {
                startScan();
            }
			else {
				requestPermissions(permissionsToRequest.toArray(new String[0]), 1);
			}

        } else {
            startScan();
        }
		startScan();
	}

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == 1) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();
            } else {
                // Permission denied. Disable BLE or notify user.
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void startScan() {
        scanner.startScan(scanCallback);
        Log.d(TAG, "Scanning for BLE devices...");
    }

    private void connectToDevice(BluetoothDevice device)
	{
        Log.d(TAG, "Connecting to device...");
        bluetoothGatt = device.connectGatt(this, false, gattCallback);
    }

	public int getBluetoothStatus()
	{
		if (bluetoothAdapter == null) return 0; // not bluetooth support
		if (!bluetoothAdapter.isEnabled()) return 1; // not enabled
		return 2; // enabled
	}

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Connected to GATT server");
                gatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.d(TAG, "Found service");
                BluetoothGattService service = gatt.getService(UUID.fromString(SERVICE_UUID));
                if (service != null) {
					Log.d(TAG, "Valid Service");
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(CHARACTERISTIC_UUID));
                    if (characteristic != null) {
                        boolean valid = gatt.readCharacteristic(characteristic);
						Log.d(TAG, "Reading?: " + (valid ? "True" : "False"));
                    }
                }
            }
        }


		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic,
				int status) {

			Log.d(TAG, "onCharacteristicRead() triggered");

			if (status == BluetoothGatt.GATT_SUCCESS) {
				byte[] value = characteristic.getValue();
				currentValue = value.clone();
				String str = new String(value, StandardCharsets.UTF_8);
				Log.d(TAG, "Read value: " + str);
			} else {
				Log.d(TAG, "Characteristic read failed, status: " + status);
			}
		}
        //@Override
		//public void onCharacteristicRead (BluetoothGatt gatt, 
		//		BluetoothGattCharacteristic characteristic, 
		//		byte[] value, 
		//		int status) {
		//	Log.d(TAG, "Read something");
		//	if (status == BluetoothGatt.GATT_SUCCESS) {
		//		Log.d(TAG, "Successfully");

        //        String str = new String(value, StandardCharsets.UTF_8); // for UTF-8 encoding
		//		Log.d(TAG, "Read characteristic: " + str);
		//	}

		//}
    };

	public byte[] getData() {

		return currentValue;
	}

}

// <meta-data android:name="android.app.lib_name" android:value="main" />

