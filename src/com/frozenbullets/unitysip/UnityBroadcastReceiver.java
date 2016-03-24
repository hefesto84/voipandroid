package com.frozenbullets.unitysip;

import org.abtollc.utils.Log;

import com.unity3d.player.UnityPlayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class UnityBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
		final String batteryLevel = String.valueOf(level);
		
		UnityPlayer.currentActivity.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				UnityPlayer.UnitySendMessage("UI", "OnBatteryLevel",batteryLevel);
			}
		});
		
	}

}
