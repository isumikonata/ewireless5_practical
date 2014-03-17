package com.example.helloemfsensor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener{

	private SensorManager mSensorManager;
    //get access to sensors
    private Sensor mMagneticField;
    //represent a sensor
    
    //Set TextView for sensor value
    private TextView mag_x;
    private TextView mag_y;
    private TextView mag_z;
    private TextView mag_h;
	
   
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Find view from layout file
        mag_x = (TextView)findViewById(R.id.Xaxis);
        mag_y = (TextView)findViewById(R.id.Yaxis);
        mag_z = (TextView)findViewById(R.id.Zaxis);
        mag_h = (TextView)findViewById(R.id.magnetic_field);
        
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //Get an instance of SensorManager for accessing sensors
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //Determine a default sensor type, in this case is magnetometer
    }
    
    private Uri fileUri;
    
    //Define a Uri
    public static final int MEDIA_TYPE_IMAGE = 1;
    //define the state of image
    public void TakePhoto(View view){
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	//intent the existing camera application and return control to the calling application
    	fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
    	//get the uri of a file to save the image
    	intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
    	//specifying the path and file name for the received image
    	startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    	//start to intent the image capture activity
    }
    
    //create the file uri
    private static Uri getOutputMediaFileUri(int type){
    	return Uri.fromFile(getOutputMediaFile(type));
    	//uri is defined by media file
    }
    
    //create a file for saving an image or video
    @SuppressLint("SimpleDateFormat")
    private static File getOutputMediaFile(int type){
    	final File mediaStorageDir;
    	if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
    		mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "EMFdetectingApp");
    	}else{
    		mediaStorageDir = new File("/storage/sdcard0/EMFdetectingApp/");
    	}
    	//create the storage directory. if sd cad exists, create a directory of standard, shared
    	//and recommended location for saving pictures. if not, create a directory in the device
    	if (!mediaStorageDir.exists()){
    		if (!mediaStorageDir.mkdirs()){
    			//creates the directory named by this file, creating missign parent directoreis if necessary
    			Log.d("EMFdetectingApp","failed to create directory");
    			return null;
    		}
    	}
    	//create a media file name
    	String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    	File mediaFile = null;
    	if (type == MEDIA_TYPE_IMAGE){
    		mediaFile = new File(mediaStorageDir.getPath()+File.separator+"IMG_"+timeStamp+".jpg");
    	}else{
    		return null;
    	}
    	return mediaFile;
    }
    
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE=100;
    //present the state of saved image
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK){
				//Image captured and saved to fileUri specified in the Intent
				Toast.makeText(this, "Image successfully saved", Toast.LENGTH_SHORT).show();
			} else if (resultCode == RESULT_CANCELED) {
				//User cancelled the image capture
			} else {
				//Image capture failed, advise user
			}
		}
    }
    


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    private float x,y,z;
    private double h;
    @Override
    public void onSensorChanged(SensorEvent event){
    	//read sensor value from SensorEvent
    	x = event.values[0];
    	y = event.values[1];
    	z = event.values[2];
    	//calculate the total magnetic field
    	h = Math.sqrt(event.values[0]*event.values[0]+event.values[1]*event.values[1]+event.values[2]*event.values[2]);
    	
    	//set the string value from sensor to text
    	if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
    		mag_x.setText("Xaxis:"+ x);
    		mag_y.setText("Yaxis:"+ y);
    		mag_z.setText("Zaxis:"+ z);
    		mag_h.setText("magnetic_field:"+ h);
    	}
    }
    
    public void onAccuracyChanged(Sensor sensor, int accuracy){
    	
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
    	mSensorManager.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
    	//register the sensor when user returns to the activity
    }
    
    protected void onPause(){
    	super.onPause();
    	mSensorManager.unregisterListener(this);
    	//disable the sensor
    }
    
    
}
