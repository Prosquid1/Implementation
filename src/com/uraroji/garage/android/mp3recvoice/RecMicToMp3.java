package com.uraroji.garage.android.mp3recvoice;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.uraroji.garage.android.lame.SimpleLame;
public class RecMicToMp3 extends Service{
    private final IBinder mBinder = new LocalBinder();

	private boolean mIsRecording = false;	
Thread recordingThread;
int rectime = 60;
int mSampleRate = 44100;
private short[] mBuffer;
private static final String ACTION_STRING_SERVICE = "ToService";
//STEP1: Create a broadcast receiver
private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {

    @Override
    public void onReceive(Context context, Intent intent) {
    	 savecombogetlastfive();
    }};
private AudioRecord audioRecord = null;
	CountDownTimer b = null;
	static {
		System.loadLibrary("mp3lame");
	}

	
	@Override
	public void onCreate() {
		Log.e("on Create", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//STEP2: register the receiver
	        if (serviceReceiver != null) {
	//Create an intent filter to listen to the broadcast sent with the action "ACTION_STRING_SERVICE"
	            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_SERVICE);
	//Map the intent filter to the receiver
	            registerReceiver(serviceReceiver, intentFilter);
	            checkFolders();
	            threadrec();}
	}
	private void checkFolders() {
		File folderAR = new File(Environment.getExternalStorageDirectory()+"/AudioRecorder/");
		File foldertempdata = new File(Environment.getExternalStorageDirectory()+"/AudioRecorder/tmp_data");
		if (!folderAR.exists()){
			folderAR.mkdirs();
			foldertempdata.mkdirs();
		}
	}
	protected void savecombogetlastfive() {
     Log.e("TAG", "Stopped");
     combinebothmp3s();
     Log.e("TAG", "mp3 combined");
     TrimAudio();
     Log.e("TAG", "Audio Trimmed");
     threadrec();
     deletecombinedFile();
     
     Log.e("TAG", "Service restarted");
	}
	public void TrimAudio(){
        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/AudioRecorder/tmp_data/combined.mp3");
		    String mName = Environment.getExternalStorageDirectory().getAbsolutePath();
		    mName += "/AudioRecorder/CapturedAudio" +System.currentTimeMillis()+".mp3";
		File f = new File(mName); // new file 
	    CheapSoundFile csf = null;
		try {
			csf = CheapSoundFile.create(mFileName, null );
		} catch (FileNotFoundException e) {
			Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		try{	File yourFile = new File(mFileName);
		MediaPlayer mp = new MediaPlayer();
		FileInputStream fs;
		FileDescriptor fd;
		fs = new FileInputStream(yourFile);
		fd = fs.getFD();
		mp.setDataSource(fd);
		mp.prepare(); // might be optional
		double lengtho = mp.getDuration()/1000;
		fs.close();
		mp.release();
   	int proms = rectime;// to be edited later when you want a longer file
int nof = csf.getNumFrames();
int SH = (int) ((lengtho - proms)*(nof/lengtho));
int crit2 = nof-SH;
		    				csf.WriteFile(f, SH, crit2);
					 sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED",Uri.parse("file://"  + Environment.getExternalStorageDirectory())));
					
				 sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse
				   ("file://" + Environment.getExternalStorageDirectory())));
					Toast.makeText(getApplicationContext(), "Recording Saved", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				Log.e("BAAAAAAAD", "BAAAAAAAAAAAAD");
				Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		;
		
	}
String Mlf;
String Mlf2;
	//the method below executes onuserstop=true
	private void combinebothmp3s(){
		//In Mlf and Mlf2 find the most recent
		
if (getrecint()==1){
	   Mlf = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/AudioRecorder/tmp_data/A2.mp3") ;
	  Mlf2 = Environment.getExternalStorageDirectory().getAbsolutePath();
	Mlf2 += "/AudioRecorder/tmp_data/A1.mp3";	
}else {
	  Mlf = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/AudioRecorder/tmp_data/A1.mp3") ;
	  Mlf2 = Environment.getExternalStorageDirectory().getAbsolutePath();
	Mlf2 += "/AudioRecorder/tmp_data/A2.mp3";
}

		String   Mlf3 = Environment.getExternalStorageDirectory().getAbsolutePath();
		Mlf3 += "/AudioRecorder/tmp_data/combined.mp3";
		try {

Log.e(Mlf, Mlf2);

	        InputStream in = new FileInputStream(Mlf);//firstmp3
	         byte[] buffer = new byte[1 << 20];  
	         OutputStream os = new FileOutputStream(new File(Mlf3));//output mp3
	         int count;
	         try {
				while ((count = in.read(buffer)) != -1) {
				     os.write(buffer, 0, count);
				     os.flush();
				 }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	         try {
				in.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	         in = new FileInputStream(Mlf2);//second mp3
	         try {
				while ((count = in.read(buffer)) != -1) {
				     os.write(buffer, 0, count);
				     os.flush();
				 }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	         try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	         try {
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    } catch (FileNotFoundException e) {
	     // TODO Auto-generated catch block
	     e.printStackTrace();
	 }
		
	deleteA();
	deleteB();

	}
	private void deletecombinedFile() {
	String   Mlf = Environment.getExternalStorageDirectory().getAbsolutePath();
	Mlf += "/AudioRecorder/tmp_data/combined.mp3";
		 File concatenatedfile = new File(Mlf);
	     
	        if(concatenatedfile.exists())
	                concatenatedfile.delete();
		
	}
	private void deleteA() {
		 File tempFile1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/AudioRecorder/tmp_data/A1.mp3"));
	     
	        if(tempFile1.exists())
	                tempFile1.delete();}
	        private void deleteB() {
		 File tempFile2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/AudioRecorder/tmp_data/A2.mp3"));
	     
	        if(tempFile2.exists())
	                tempFile2.delete();
		
	}
	public void threadrec(){

    b =new CountDownTimer((rectime+1)*1000, 1000) {
	     public void onTick(long millisUntilFinished) {
	     }
	     public void onFinish() {
	            mIsRecording = false;
	        	int i = audioRecord.getState();
	            if(i==1)
	            audioRecord.stop();
	            audioRecord = null;
	    		b.cancel();
 
 Log.e("", "End of Recording 1");
if (getrecint()==1){
	setrecint(2);
} else if (getrecint()==2){
	setrecint(1);
}
 
	  threadrec();   
	  ;}}
 .start();
 
	startRecording();
 }

	public void startRecording() {
		
		final int minBufferSize = AudioRecord.getMinBufferSize(
				mSampleRate, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		 audioRecord = new AudioRecord(
				MediaRecorder.AudioSource.MIC, mSampleRate,
				AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 2);
		if (mIsRecording) {
			
			return;
		}

	 recordingThread = new Thread
	            ( new Runnable() {

	                       @Override
	                       public void run() {
	                    	   mBuffer = new short[minBufferSize];
android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
			 	String mFilePath = Environment.getExternalStorageDirectory() + "/AudioRecorder/tmp_data/A" +getrecint()+".mp3";
   
				if (minBufferSize < 0) {}
				short[] buffer = new short[mSampleRate * (16 / 8) * 1 * 60]; // SampleRate[Hz] * 16bit * Mono * 5sec
				byte[] mp3buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];
				   
				FileOutputStream output = null;
				try {
					output = new FileOutputStream(new File(mFilePath));
				} catch (FileNotFoundException e) {}
				// Lame init
				SimpleLame.init(mSampleRate, 1, mSampleRate, 32);
                mIsRecording = true; 
				try {				try {
		        	audioRecord.startRecording(); 
						Log.e("TAG",  "Started A");
	
					} catch (IllegalStateException e) {
					
						return;
					}
					try {
						int readSize = 0;
						while (mIsRecording) {
							
							readSize = audioRecord.read(buffer, 0, minBufferSize);
							//reading into buffer
							//startBufferedWrite(mRecording);
				if (readSize == 0) {
							}
							//no errors
							else {
								int encResult = SimpleLame.encode(buffer,
										buffer, readSize, mp3buffer);
								if (encResult < 0) {}
								if (encResult != 0) {
									try {
										output.write(mp3buffer, 0, encResult);
									
									} catch (IOException e) {}
								}
							}
						}

						int flushResult = SimpleLame.flush(mp3buffer);
						if (flushResult < 0) {}
						if (flushResult != 0) {
							try {
								output.write(mp3buffer, 0, flushResult);
							} catch (IOException e) {}
						}

						try {
							output.close();
						} catch (IOException e) {}
					} finally {
						
					}
				} finally {
					SimpleLame.close();
					mIsRecording = false; 
				}}});
	               recordingThread.start();}

	public boolean isRecording() {
		return mIsRecording;
	}
 public class LocalBinder extends Binder {
            RecMicToMp3 getService() {
                // Return this instance of LocalService so clients can call public methods
                return RecMicToMp3.this;
            }
        }

        @Override
        public IBinder onBind(Intent intent) {
            return mBinder;
        }
        
        
        public int bat = 1;
        public void setme(int o) 
        {bat= o;}
        public int  getme(){
        	return bat;}
        public int recint =1; 
        public void setrecint(int o) {
        	
            recint= o;
            }
          public int  getrecint(){
          	return recint;
          }
          public void setTime(int o) {
          	
              rectime= o;
              }
            public int  getTime(){
            	return rectime;
            }
            public void setSampleRate(int o) {
              	
                mSampleRate= o;
                }
              public int  getSampleRate(){
              	return mSampleRate;
              }

}