package com.mediarecoder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SurfaceHolder.Callback, OnTouchListener{

	private static final String TAG = "MediaRecorderExample";

	Camera mCamera;
	MediaRecorder mMediaRecorder;

	SurfaceView mSurface;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button btnRec = (Button)findViewById(R.id.btnRec);
		btnRec.setOnTouchListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		mSurface = (SurfaceView) findViewById(R.id.surfaceViewCamera);
		mSurface.getHolder().addCallback(this);

		mCamera = Camera.open();
	}

	@Override
	public void onPause() {
		super.onPause();

		stopRecord();

		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.media_record_test, menu);
		return true;
	}
*/
	/*
	 *
	/////////////////////////////////////////////////////////////////////////
	public void onRecordClick(View v) {
		boolean on = ((ToggleButton) v).isChecked();

		if (on) {
			try {
				startRecord();
			} catch (IllegalStateException e) {
				e.printStackTrace();
				Toast.makeText(this, "Fail to start recording/illegal state", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(this, "Fail to start recording/io exception", Toast.LENGTH_SHORT).show();
			}
		}
		else {
			stopRecord();
		}
	}
	**************************************************************************/
	
	private int nFrameCount = 0;

	
	/////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////START RECORD////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	private void startRecord() throws IllegalStateException, IOException {
		mCamera.unlock();

		mMediaRecorder = new MediaRecorder();
		mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setPreviewDisplay(mSurface.getHolder().getSurface());
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		//Error native method
		//mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

		if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P)) {
			mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_1080P));
		}
		else {
			mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
		}
		mMediaRecorder.setMaxDuration(15000);
		mMediaRecorder.setVideoSize(480, 480);
		
		
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    	String timeStamp = dateFormat.format(new Date());
    	
		File outputFile = new File(Environment.getExternalStorageDirectory(), "video" +timeStamp+ ".mp4");
		mMediaRecorder.setOutputFile(outputFile.getCanonicalPath());
		mMediaRecorder.prepare();
		mMediaRecorder.start();
		
		//final TextView frameCountView = (TextView) findViewById(R.id.textViewFrameCount); 
		mCamera.setPreviewCallback(new PreviewCallback() {

			public void onPreviewFrame(byte[] data, Camera camera) {
				Log.d(TAG, "Receive data size: " + data.length);
				//frameCountView.setText(String.valueOf(++nFrameCount));
			}

		});
		
	}
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////

	
	/////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////STOP RECORD////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	private void stopRecord() {
		if (mMediaRecorder != null) {		
			mMediaRecorder.stop();
			mMediaRecorder.reset();
			mMediaRecorder.release();
			mMediaRecorder = null;

			mCamera.setPreviewCallback(null);
			mCamera.lock();			
		}
	}
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}

		mCamera.startPreview();	
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	/////////////EVENT LISTENER WHEN HOLD AND RELEASE BUTTON RECORD//////////////////
	/////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onTouch(View v, MotionEvent e) {
		// TODO Auto-generated method stub
		
		switch(e.getAction()) {
        case MotionEvent.ACTION_DOWN:
            // Do something
        	try {
				startRecord();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
				Toast.makeText(this, "Fail to start recording/illegal state", Toast.LENGTH_SHORT).show();
			} catch (IOException e2) {
				e2.printStackTrace();
				Toast.makeText(this, "Fail to start recording/io exception", Toast.LENGTH_SHORT).show();
			}
            return true;
        case MotionEvent.ACTION_UP:
            // No longer down
        	stopRecord();
			Toast.makeText(getApplication(), "Release, now start to save video",  Toast.LENGTH_LONG).show();
			// Convert to video
			
            return true;
        }
		
		return false;
	}
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
}
