package com.uraroji.garage.android.mp3recvoice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uraroji.garage.android.mp3recvoice.RecMicToMp3.LocalBinder;

public class SettingsActivity extends Activity {
	AlertDialog levelDialog ;
	RecMicToMp3 mService;
    boolean mBound = false;
    TextView rq;
    TextView rd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		rq = (TextView)findViewById(R.id.textView3);
		 rd = (TextView)findViewById(R.id.textViewu);
	
RelativeLayout i = (RelativeLayout)findViewById(R.id.Layout1);
	// Strings to Show In Dialog with Radio Buttons
	final CharSequence[] items = {" Low "," Medium "," Good "," Very Good "};
	       i.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
	                builder.setTitle("Select Recording Quality");
	                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int item) {
	                  
	                   
	                    switch(item)
	                    {
	                        case 0:
	      	   					  mService.setSampleRate(16000);
	      	   					mService.threadrec();
	      	   				rq.setText("Low, "+ String.valueOf(mService.getSampleRate()));
	      	   				
	                         break;
	                        case 1: 
	                        	mService.setSampleRate(22050);
	                        	mService.threadrec();
	                        	rq.setText("Medium, "+ String.valueOf(mService.getSampleRate()));
	                    		
	                                break;
	                        case 2: 
	                        	mService.setSampleRate(32000);
	                        	mService.threadrec();
	                        	rq.setText("Good, "+ String.valueOf(mService.getSampleRate()));
	                    		
	                        	 break;
	                        case 3:
	                        	mService.setSampleRate(44100);
	                        	mService.threadrec();

	                    		rq.setText("Very Good, "+ String.valueOf(mService.getSampleRate()));
	                    		
	                        	 break;
	                    }
	                    levelDialog.dismiss();   
	                    }

					
	                });
	                levelDialog = builder.create();
	                levelDialog.show();
	
			}
		})    ;
	       RelativeLayout ik = (RelativeLayout)findViewById(R.id.Layout2);
	   	       ik.setOnClickListener(new View.OnClickListener() {
	   			
	   			@Override
	   			public void onClick(View v) {
	       
	       
	       AlertDialog.Builder alert = new AlertDialog.Builder(SettingsActivity.this);

	       alert.setTitle("Set Recording Time");
	       alert.setMessage("Please enter new value (in seconds)");

	       // Set an EditText view to get user input 
	       final EditText input = new EditText(SettingsActivity.this);
	       input.setInputType(InputType.TYPE_CLASS_NUMBER);
	       alert.setView(input);

	       alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	       public void onClick(DialogInterface dialog, int whichButton) {
	        String value = input.getText().toString();
	        mService.setTime(Integer.valueOf(value));
	        rd.setText(String.valueOf(mService.getTime()));
    		
	        mService.threadrec();
	         }
	       });

	       alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	           // Canceled.
	         }
	       });

	       alert.show();
	       
	   			}
	   		})    ;
	       
	       
	       
	       
	       
	}
	  @Override
	  protected void onStart() {
	      super.onStart();
	      // Bind to LocalService
	      Intent intent = new Intent(this, RecMicToMp3.class);
	      bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	  }

		@Override
		protected void onDestroy() {
			super.onDestroy();
		}

		  @Override
		  protected void onStop() {
		      super.onStop();
		      unbindService(mConnection);
		  }

	  private ServiceConnection mConnection = new ServiceConnection() {

	      @Override
	      public void onServiceConnected(ComponentName className,
	              IBinder service) {
	          // We've bound to LocalService, cast the IBinder and get LocalService instance
	          LocalBinder binder = (LocalBinder) service;
	          mService = binder.getService();
	          mBound = true;
	          type();
	 
      		
	          rd.setText(String.valueOf(mService.getTime()));
      		
	        
				
	      }

	      private void type() {    
	    	  String p =String.valueOf(mService.getSampleRate());
			if (p.equals("44100")){
				rq.setText("Very Good, "+p);
			} else if
			(p.equals("32000")){
				rq.setText("Good, "+p);
			} else if
			(p.equals("22050")){
				rq.setText("Medium, "+p);
			} else if
			(p.equals("16000")){
				rq.setText("Low, "+p);
			}
		}

		@Override
	      public void onServiceDisconnected(ComponentName arg0) {
	          mBound = false;
	      }
	  };
}