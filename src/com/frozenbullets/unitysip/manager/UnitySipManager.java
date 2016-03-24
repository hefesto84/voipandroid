package com.frozenbullets.unitysip.manager;

import java.nio.MappedByteBuffer;

import org.abtollc.sdk.AbtoApplication;
import org.abtollc.sdk.AbtoPhone;
import org.abtollc.sdk.OnCallConnectedListener;
import org.abtollc.sdk.OnCallDisconnectedListener;
import org.abtollc.sdk.OnIncomingCallListener;
import org.abtollc.sdk.OnInitializeListener;
import org.abtollc.sdk.OnRegistrationListener;
import org.abtollc.utils.codec.Codec;

import com.frozenbullets.unitysip.MainActivity;
import com.unity3d.player.UnityPlayer;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class UnitySipManager implements OnInitializeListener, OnIncomingCallListener, OnRegistrationListener, OnCallConnectedListener, OnCallDisconnectedListener{
	
	private static UnitySipManager INSTANCE = null;
	private static Context mContext = null;
	private static AbtoApplication mApplication;
	private AbtoPhone mAbtoPhone = null;
	private long accId = 0L;
	private long callId = 0L;
	private String domain = "";
	private boolean muted = false;
	private UnityPlayer mUnityPlayer;
	
	private UnitySipManager(){
		
		mAbtoPhone = mApplication.getAbtoPhone();
		mAbtoPhone.setInitializeListener(this);
		
		mAbtoPhone.getConfig().setCodecPriority(Codec.G729, (short)250);
		mAbtoPhone.getConfig().setCodecPriority(Codec.PCMU, (short)200);
		mAbtoPhone.getConfig().setCodecPriority(Codec.GSM, (short)150);
		mAbtoPhone.getConfig().setCodecPriority(Codec.PCMA, (short)100);
		mAbtoPhone.getConfig().setCodecPriority(Codec.speex_16000, (short)50);
		
		mAbtoPhone.initialize();
		
		mAbtoPhone.setRegistrationStateListener(this);
		mAbtoPhone.setIncomingCallListener(this);
		mAbtoPhone.setCallConnectedListener(this);
		mAbtoPhone.setCallDisconnectedListener(this);
		
		
	}

	public void register(){
		try {
			mAbtoPhone.register();
			Toast t = Toast.makeText(mContext, "Registering", Toast.LENGTH_SHORT);
			t.show();
		} catch (RemoteException e) {
			Toast t = Toast.makeText(mContext, "Error: "+e.getMessage(), Toast.LENGTH_SHORT);
			t.show();
		}
	}
	
	public void acceptCall(){
		try {
			mAbtoPhone.answerCall(200, false);
		} catch (RemoteException e) {
			Toast t = Toast.makeText(mContext, "Error: "+e.getMessage(), Toast.LENGTH_SHORT);
			t.show();
		}
	}
	
	public void cancelCall(){
		try {
			mAbtoPhone.hangUp();
			Toast.makeText(mContext, "Call terminated", Toast.LENGTH_SHORT).show();
		} catch (RemoteException e) {
			Toast.makeText(mContext,  "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void toggleAudio(){
		if(muted){
			muted = false;
		}else{
			muted = true;
		}
		try {
			mAbtoPhone.setMicrophoneMute(muted);
			Toast t = Toast.makeText(mContext, "Muted: "+muted, Toast.LENGTH_SHORT);
			t.show();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void makeCall(String number, boolean internal){
		String destination = String.format("<sip:%1$s@%2$s>", number, domain);
		try {
			mAbtoPhone.startCall(destination, accId);
			callId = mAbtoPhone.getActiveCallId();
		} catch (RemoteException e) {
			Toast.makeText(mContext, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void createSipProfile(String domain, String username, String password){
		
		this.domain = domain;
		accId = mAbtoPhone.getConfig().addAccount(this.domain,username,password, null,username, 300, false);
		Log.d("Unity", "Creating profile: "+accId);
	}
	
	public void registerSipServer(PendingIntent pi){
		
	}
	
	public static void configure(Context context, AbtoApplication application){
		mContext = context;
		mApplication = application;
		Log.d("Unity", "Configuring VoIP");
	}
	
	public static UnitySipManager  getInstance(){
		
		if(mContext == null || mApplication == null){
			Log.e("UnitySIP", "Context not configured");
			return null;
		}
		
		if(INSTANCE == null){
			synchronized(UnitySipManager.class){
				if(INSTANCE==null){
					INSTANCE = new UnitySipManager();
				} 
			}
		}
		
		return INSTANCE;
	}
	
	@Override
	public UnitySipManager clone() throws CloneNotSupportedException {
    	throw new CloneNotSupportedException(); 
	}

	@Override
	public void OnIncomingCall(String remoteContact, long accountId) {
		UnityPlayer.UnitySendMessage("UI", "OnIncomingCall", remoteContact);
	}

	@Override
	public void onInitializeState(InitializeState arg0, String arg1) {
		
	}

	@Override
	public void onRegistered(long arg0) {
		//Toast.makeText(mContext,"OnRegistered: "+arg0, Toast.LENGTH_SHORT).show();
		UnityPlayer.UnitySendMessage("UI", "OnRegistered",String.valueOf(arg0));
	}

	@Override
	public void onRegistrationFailed(long arg0, int arg1, String arg2) {
		//Toast.makeText(mContext,"OnRegistration: "+arg2, Toast.LENGTH_SHORT).show();
		UnityPlayer.UnitySendMessage("UI","OnInitialized","");
	}

	@Override
	public void onUnRegistered(long arg0) {
		Toast.makeText(mContext,"OnUnregistered: "+arg0, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCallDisconnected(String remoteContact, int arg1, int arg2) {
		UnityPlayer.UnitySendMessage("UI", "OnCancelledCall", remoteContact);
	}

	@Override
	public void onCallConnected(String remoteContact) {
		UnityPlayer.UnitySendMessage("UI", "OnAcceptedCall", remoteContact);
	}

}
