package com.frozenbullets.unitysip;

import org.abtollc.sdk.AbtoApplication;
import org.abtollc.sdk.AbtoPhone;
import org.abtollc.sdk.OnInitializeListener;
import org.abtollc.sdk.OnRegistrationListener;
import org.abtollc.utils.codec.Codec;

import com.frozenbullets.unitysip.manager.UnitySipManager;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends UnityPlayerActivity{

	private static Context mContext;
	private RelativeLayout layout = null;
	private WebView m_webView = null;
	
	private Button mButtonCall = null;
	private Button mButtonRegister = null;
	private Button mButtonAccept = null;
	private Button mButtonHangUp = null;
	private Button mButtonMute = null;
	private TextView mTextViewNumber = null;
	
	private UnityBroadcastReceiver broadcastReceiver;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mContext = this;
	
	}
	
	@Override
	protected void onStart(){
		broadcastReceiver = new UnityBroadcastReceiver();
		this.registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		super.onStart();
	}
	
	@Override
	protected void onStop(){
		if(broadcastReceiver!=null){
			this.unregisterReceiver(broadcastReceiver);
		}
		super.onStop();
	}
	
	public void GetIMEI(){
		
		final TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		UnityPlayer.currentActivity.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				UnityPlayer.UnitySendMessage("UI", "OnIMEI",tm.getDeviceId());
			}
		});
		Log.d("Unity", "IMEI: "+tm.getDeviceId());
	}
	
	public void InitSipManager(String domain, String username, String password){
		
		final Context mContext = this;
		final AbtoApplication mApplication = (AbtoApplication)getApplicationContext();
		final String dm = domain;
		final String us = username;
		final String pw = password;
		
		UnityPlayer.currentActivity.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				UnitySipManager.configure(mContext, mApplication);
				UnitySipManager.getInstance().createSipProfile(dm, us, pw);
			}
			
		});
	}

	public void Register(){
		UnityPlayer.currentActivity.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				Log.d("Unity", "Registering...");;
				UnitySipManager.getInstance().register();
			}
		});
		
	}
	
	public void ToggleMute(){
		UnityPlayer.currentActivity.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				UnitySipManager.getInstance().toggleAudio();
			}
			
		});
		
	}
	
	public void MakeCall(String number){
		final String n = number; 
		UnityPlayer.currentActivity.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				UnitySipManager.getInstance().makeCall(n,false);
			}
			
		});
		
	}
	
	public void AcceptCall(){
		UnityPlayer.currentActivity.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				UnitySipManager.getInstance().acceptCall();
			}
			
		});
		
	}
	
	public void CancelCall(){
		UnityPlayer.currentActivity.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				UnitySipManager.getInstance().cancelCall();
			}
			
		});
		
	}

	public void LoadPdf(String url){
		LoadWebview("https://docs.google.com/gview?embedded=true&url=https://www.adobe.com/devnet/acrobat/pdfs/pdf_open_parameters.pdf");
	}
	
	public void LoadNativePdf(){
		
	}
	
	public void LoadWebview(String url){
		
		final String m_url = url;
		
		Log.d("UnitySIP", "Entering into webview");
		
		UnityPlayer.currentActivity.runOnUiThread(new Runnable() { 
	            public void run() { 
					
			        layout = new RelativeLayout(UnityPlayer.currentActivity.getApplicationContext()); 
			        
			        layout.setGravity(Gravity.CENTER); 
			        layout.setPadding(0, 200, 0, 0);
			        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			        
			        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			        initWebView(m_url);
			       
	                layout.addView(m_webView); 
	                
			        UnityPlayer.currentActivity.addContentView(layout, lp); 
			  
					
	            }
		 });
		 Log.d("UnitySIP", "Quitting from webview");
     
	}

	private void initWebView(String url){
		WebChromeClient mwebview_c = new WebChromeClient();
		m_webView = new WebView(UnityPlayer.currentActivity);
		m_webView.setWebChromeClient(mwebview_c);
		m_webView.getSettings().setJavaScriptEnabled(true);
		 m_webView.getSettings().setPluginState(PluginState.ON);
		 m_webView.getSettings().setSupportZoom(true);             
		 m_webView.getSettings().setBuiltInZoomControls(true); 
		 m_webView.getSettings().setLoadWithOverviewMode(true); 
		 m_webView.getSettings().setUseWideViewPort(true); 
		 m_webView.loadUrl(url);
		/*
		 m_webView = new WebView(UnityPlayer.currentActivity); 
		 m_webView.setWebViewClient(new WebViewClient());
		 
		 m_webView.getSettings().setJavaScriptEnabled(true);
		 m_webView.getSettings().setPluginState(PluginState.ON);
		 m_webView.getSettings().setSupportZoom(true);             
		 m_webView.getSettings().setBuiltInZoomControls(true); 
		 m_webView.getSettings().setLoadWithOverviewMode(true); 
		 m_webView.getSettings().setUseWideViewPort(true); 
		*/
		 m_webView.loadUrl(url);
	}
	
	public void Back(){
		Log.d("UnitySIP", "Back button");
		
		UnityPlayer.currentActivity.runOnUiThread(new Runnable() { 
            public void run() { 
            	try{
            	layout.removeAllViews();
            	}catch(Exception e){
            		Log.e("UnitySIP", "No views available to be removed");
            	}
            }
		});
		
	}
	
}
