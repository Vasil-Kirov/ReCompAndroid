package com.vasko.pong;

import android.app.NativeActivity;
import android.os.Bundle;
import android.util.Log;
import android.bluetooth.BluetoothAdapter;

public class MainActivity extends NativeActivity
{
	public void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
	}

	public void printMessage()
	{
		Log.println(Log.INFO, "vpong", "Hellooooo from java");
	}

	public int getBluetoothStatus()
	{
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) return 0; // not bluetooth support
		if (!mBluetoothAdapter.isEnabled()) return 1; // not enabled
		return 2; // enabled
	}
}

// <meta-data android:name="android.app.lib_name" android:value="main" />

