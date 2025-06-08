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
import android.content.Intent;

import java.util.UUID;
import java.nio.charset.StandardCharsets;

public class MainActivity extends NativeActivity
{
	private final static int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "vpong";
    private static final String SERVICE_UUID = "6e053c45-71bb-4c40-9d82-cc3be83952da";
    private static final String CHARACTERISTIC_UUID = "b8af02f9-e29b-4576-9c5a-fd80b7eda684";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private BluetoothGatt bluetoothGatt;
	private BluetoothDevice connectedDevice = null;

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
				connectedDevice = device;
            }
        }
    };
	
	public void doPermissionsAndStartScan() {
		scanner = bluetoothAdapter.getBluetoothLeScanner();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { 

			java.util.List<String> permissionsToRequest = new java.util.ArrayList<>();

			if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
			{
				permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
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
	}

	@Override
	public void onCreate(Bundle savedInstance) {
		//bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();
		if (!bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		else {
			doPermissionsAndStartScan();
		}

		super.onCreate(savedInstance);
	}

	@Override
	public void onActivityResult (int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode != RESULT_OK) {
				Log.d(TAG, "Bluetooth not enabled!");
				finish();
				return;
			}

			doPermissionsAndStartScan();
		}
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

    private void connectToDevice(BluetoothDevice device) {
        Log.d(TAG, "Connecting to device...");
        bluetoothGatt = device.connectGatt(this, false, gattCallback);
    }

	public int getBluetoothStatus() {
		if (bluetoothAdapter == null) return 0; // not bluetooth support
		if (!bluetoothAdapter.isEnabled()) return 1; // not enabled
		if (connectedDevice == null) return 2; // enabled
		return 3; // connected
	}

	public void sendRefreshRequest() {
		if (bluetoothGatt == null) return;
		BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(SERVICE_UUID));
		if (service != null) {
			BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(CHARACTERISTIC_UUID));
			if (characteristic != null) {
				byte[] dataToSend = "update".getBytes(StandardCharsets.UTF_8);
				characteristic.setValue(dataToSend);
				boolean success = bluetoothGatt.writeCharacteristic(characteristic);
				Log.d(TAG, "Writing?: " + (success ? "True" : "False"));
			}
		}
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

