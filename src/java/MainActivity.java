package com.vasko.pong;

import android.app.NativeActivity;
import android.os.Bundle;
import android.util.Log;

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
}

// <meta-data android:name="android.app.lib_name" android:value="main" />

