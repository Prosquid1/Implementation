package com.uraroji.garage.android.mp3recvoice;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.uraroji.garage.android.mp3recvoice.RecMicToMp3.LocalBinder;

public class MainActivity extends Activity {
	  private static final String ACTION_STRING_SERVICE = "ToService";
	  private static final String ACTION_STRING_ACTIVITY = "ToActivity";
	    RecMicToMp3 mService;
	    boolean mBound = false;
	    int num;
	  private BroadcastReceiver activityReceiver = new BroadcastReceiver() {

	      @Override
	      public void onReceive(Context context, Intent intent) {
	    	  String g= intent.getAction();
	    	  Log.e(g,g);
	      }
	  };
	  ProgressBar  mProgressBar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);		
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();
		 //STEP2: register the receiver
		         if (activityReceiver != null) {
		 //Create an intent filter to listen to the broadcast sent with the action "ACTION_STRING_ACTIVITY"
		            
		 //Map the intent filter to the receiver

		       	  IntentFilter intentFilter = new IntentFilter(ACTION_STRING_ACTIVITY); 
		             registerReceiver(activityReceiver, intentFilter);
		         }
		         ImageView buttonStop = (ImageView) findViewById(R.id.buttonStop);
		         ImageView settings = (ImageView) findViewById(R.id.settingsact);
                 ImageView fileact = (ImageView) findViewById(R.id.fileact);
		         ImageView blinker = (ImageView) findViewById(R.id.blinker);    
		         AlphaAnimation blinkAnimation = new AlphaAnimation(1,0);    
		         blinkAnimation.setDuration(1000);
		         blinkAnimation.setInterpolator(new LinearInterpolator());
		         blinkAnimation.setRepeatCount(-1);    
		         blinkAnimation.setRepeatMode(Animation.REVERSE);
		         blinker.startAnimation(blinkAnimation);
		         
		         fileact.setOnClickListener(new OnClickListener() {
		        	 
		 	@Override
		 	public void onClick(View v) {
		 		Toast.makeText(getApplicationContext(), "Please navigate to SD card/AudioRecorder directory to view Audio files", Toast.LENGTH_LONG).show();
     
		 	}
		 });
		         buttonStop.setOnClickListener(new OnClickListener() {
		        	 
			@Override
			public void onClick(View v) {
			sendBroadcast();
			}
		});
	    startService(new Intent(this, RecMicToMp3.class));

        settings.setOnClickListener(new OnClickListener() {
       	 
	@Override
	public void onClick(View v) {
		Intent u = new Intent(getApplicationContext(), SettingsActivity.class);
		startActivity(u);
	}
});
        
        
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		 unregisterReceiver(activityReceiver);
	}

	
	  private void sendBroadcast() {
	      Intent new_intent = new Intent();
	      new_intent.setAction(ACTION_STRING_SERVICE);
	      sendBroadcast(new_intent);
	  }
	
}
