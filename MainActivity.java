package com.prototype.sensors;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InlinedApi")
public class MainActivity extends ActionBarActivity implements SensorEventListener,LocationListener {
	float latField=0;
	float lngField=0;
	float altField=0;
	int batLevel=0;
	String ChargingStatus="unplugged";
	String IP_add="";
	String port="";
	static private TextView text1;
	static private TextView text2_x;
	static private TextView text2_y;
	static private TextView text2_z;
	static private TextView text3_x;
	static private TextView text3_y;
	static private TextView text3_z;
	static private TextView text4_x;
	static private TextView text4_y;
	static private TextView text4_z;
	static private TextView text5;
	static private TextView text6;
	static private TextView text7;
	static private TextView text8;
	static private TextView text9;
	static private TextView text10;
	static private TextView text11_x;
	static private TextView text11_y;
	static private TextView text11_z;
	static private TextView text12;
	final Context context = this;
	static private SensorManager mSensorManager;
	static private Sensor mAcc;
	static private Sensor mGyro;
	static private Sensor mTemp;
	static private Sensor mLight;
	static private Sensor mProximity;
	static private Sensor mMagnetic;
	static private LocationManager locationManager;
	static private String provider;
	WifiManager mainWifi;
	private static final String PATTERN = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	Intent intentService;
	Intent intentService2;
	String streamingString;
	dataClient senderSock=null;
	boolean Streaming=false;
	int timeInMeasures=10000;
	private static final int REQUEST_ENABLE_BT = 1;
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	private OutputStream outStream = null;
	private InputStream inStream = null;
	// Well known SPP UUID
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	// MAC address
	//private static String address = "C8:E0:EB:54:7E:7E"; // This is the iMac's address
	private static String address = "84:38:35:62:8B:D1"; // This is the Macbook address

	private CheckBox textView1,textView2,textView3,textView4,textView5,textView6,textView7,textView8,textView9,textView10,textView11,textView12;
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		menuIpPort();

		textView1 = (CheckBox) findViewById(R.id.textView1);
		textView1.setEnabled(true);
		textView1.setChecked(true);
		text1 = (TextView) findViewById(R.id.text1);
		text1.setText("Algo time");
		
		textView2 = (CheckBox) findViewById(R.id.textView2);
		textView2.setEnabled(true);
		textView2.setChecked(true);
		text2_x = (TextView) findViewById(R.id.text2_X);
		text2_x.setText("GPS not found X");
		text2_y = (TextView) findViewById(R.id.text2_Y);
		text2_y.setText("GPS not found Y");
		text2_z = (TextView) findViewById(R.id.text2_Z);
		text2_z.setText("GPS not found Z");
		
		textView3 = (CheckBox) findViewById(R.id.textView3);
		textView3.setEnabled(true);
		textView3.setChecked(true);
		text3_x = (TextView) findViewById(R.id.text3_X);
		text3_x.setText("Accelerometer not found X");
		text3_y = (TextView) findViewById(R.id.text3_Y);
		text3_y.setText("Accelerometer not found Y");
		text3_z = (TextView) findViewById(R.id.text3_Z);
		text3_z.setText("Accelerometer not found Z");
		
		textView4 = (CheckBox) findViewById(R.id.textView4);
		textView4.setEnabled(true);
		textView4.setChecked(true);
		text4_x = (TextView) findViewById(R.id.text4_X);
		text4_x.setText("Gyroscope not found X");
		text4_y = (TextView) findViewById(R.id.text4_Y);
		text4_y.setText("Gyroscope not found Y");
		text4_z = (TextView) findViewById(R.id.text4_Z);
		text4_z.setText("Gyroscope not found Z");
		
		textView5 = (CheckBox) findViewById(R.id.textView5);
		textView5.setEnabled(true);
		textView5.setChecked(true);
		text5 = (TextView) findViewById(R.id.text5);
		text5.setText("Temperature not found");
		
		textView6 = (CheckBox) findViewById(R.id.textView6);
		textView6.setEnabled(true);
		textView6.setChecked(true);
		text6 = (TextView) findViewById(R.id.text6);
		text6.setText("Light not found");
		
		textView7 = (CheckBox) findViewById(R.id.textView7);
		textView7.setEnabled(true);
		textView7.setChecked(true);
		text7 = (TextView) findViewById(R.id.text7);
		text7.setText("Proximity not found");
		
		textView8 = (CheckBox) findViewById(R.id.textView8);
		textView8.setEnabled(true);
		textView8.setChecked(true);
		text8 = (TextView) findViewById(R.id.text8);
		text8.setText("Battery not found");
		
		textView9 = (CheckBox) findViewById(R.id.textView9);
		textView9.setEnabled(true);
		textView9.setChecked(true);
		text9 = (TextView) findViewById(R.id.text9);
		text9.setText("Processors not found");
		
		textView10 = (CheckBox) findViewById(R.id.textView10);
		textView10.setEnabled(true);
		textView10.setChecked(true);
		text10 = (TextView) findViewById(R.id.text10);
		text10.setText("Charge Status");
		
		textView11 = (CheckBox) findViewById(R.id.textView11);
		textView11.setEnabled(true);
		textView11.setChecked(true);
		text11_x = (TextView) findViewById(R.id.text11_X);
		text11_x.setText("Magnetic not found X");
		text11_y = (TextView) findViewById(R.id.text11_Y);
		text11_y.setText("Magnetic not found Y");
		text11_z = (TextView) findViewById(R.id.text11_Z);
		text11_z.setText("Magnetic not found Z");
		
		textView12 = (CheckBox) findViewById(R.id.textView12);
		textView12.setEnabled(true);
		textView12.setChecked(true);
		text12 = (TextView) findViewById(R.id.text12);
		text12.setText("WiFi not found");
		
		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the location
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		// Initialize the location fields
		if (location != null) {
			onLocationChanged(location);
		}
		
		final Button send = (Button) this.findViewById(R.id.button_StartRecordData);
		//check whether service is running
		if(isMyServiceRunning()){
			send.setText("Stop Recording Data (running...)");
		} else {
			send.setText("Start Recording Data");
		}
		
		send.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(send.getText().equals("Start Recording Data")){
					send.setText("Stop Recording Data (running...)");						
					startRecording("start");
				} else if(send.getText().equals("Stop Recording Data (running...)")){
					send.setText("Start Recording Data");
					startRecording("stop");
				}
			}
		});
		
		textView1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(textView1.isChecked()){
					System.out.println("ONE");
					textView1.setChecked(true);
				} else {
					textView1.setChecked(false);
					System.out.println("TWO");
				}
			}
		});
		
		textView2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(textView2.isChecked()){
					textView2.setChecked(true);
				} else {
					textView2.setChecked(false);
				}
			}
		});
		
		textView3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(textView3.isChecked()){
					textView3.setChecked(true);
				} else {
					textView3.setChecked(false);
				}
			}
		});
		
		textView4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(textView4.isChecked()){
					textView4.setChecked(true);
				} else {
					textView4.setChecked(false);
				}
			}
		});
		
		textView5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(textView5.isChecked()){
					textView5.setChecked(true);
				} else {
					textView5.setChecked(false);
				}
			}
		});
		
		textView6.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(textView6.isChecked()){
					textView6.setChecked(true);
				} else {
					textView6.setChecked(false);
				}
			}
		});
		
		textView7.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(textView7.isChecked()){
					textView7.setChecked(true);
				} else {
					textView7.setChecked(false);
				}
			}
		});
		
		textView8.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(textView8.isChecked()){
					textView8.setChecked(true);
				} else {
					textView8.setChecked(false);
				}
			}
		});
		
		textView9.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(textView9.isChecked()){
					textView9.setChecked(true);
				} else {
					textView9.setChecked(false);
				}
			}
		});
		
		textView10.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(textView10.isChecked()){
					textView10.setChecked(true);
				} else {
					textView10.setChecked(false);
				}
			}
		});
		
		textView11.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(textView11.isChecked()){
					textView11.setChecked(true);
				} else {
					textView11.setChecked(false);
				}
			}
		});
		
		textView12.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(textView12.isChecked()){
					textView12.setChecked(true);
				} else {
					textView12.setChecked(false);
				}
			}
		});
		
		final Button streaming = (Button) this.findViewById(R.id.button_streaming);
		
		streaming.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(streaming.getText().equals("Start Streaming")){
					streaming.setText("Stop Streaming");						
					startStreaming("start");
					Toast toast = Toast.makeText(context,"WARNING: high resources consumption (may freeze after several minutes)",Toast.LENGTH_SHORT);
					toast.show();
					getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// screen remain on while streaming
					Streaming=true;
				} else if(streaming.getText().equals("Stop Streaming")){
					streaming.setText("Start Streaming");
					startStreaming("stop");
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// screen back to normal
					Streaming=false;
				}
			}
		});
		
		Button close = (Button) this.findViewById(R.id.button_exit);
		close.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
			}
		});
		
//		//NEW Bluetooth
		btAdapter = BluetoothAdapter.getDefaultAdapter();
	    CheckBTState();
//		//END Bluetooth
	}
	

//	//NEW Bluetooth
	 private void CheckBTState() {
		    // Check for Bluetooth support and then check to make sure it is turned on
		 
		    // Emulator doesn't support Bluetooth and will return null
		    if(btAdapter==null) { 
		      Log.i("Fatal Error", "Bluetooth Not supported. Aborting.");
		    } else {
		      if (btAdapter.isEnabled()) {
		        Log.i("\n...Bluetooth is enabled...","");
		        Toast toast = Toast.makeText(context,"Bluetooth enabled!",Toast.LENGTH_LONG);
				toast.show();
		      } else {
		        //Prompt user to turn on Bluetooth (Lines below enable bluetooth but freezes for a while)
		        //Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
		        //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		        Toast toast = Toast.makeText(context,"Bluetooth disabled!",Toast.LENGTH_LONG);
				toast.show();
		      }
		    }
		  }
//		//END Bluetooth	
	
	
	@Override
	public void onStart() {
		super.onStart();
		if(!isStreamingRunning()){
		    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
			mTemp = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
			mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
			mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
			mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
				
			mSensorManager.registerListener(this, mAcc,mSensorManager.SENSOR_DELAY_FASTEST); //SENSOR_DELAY_FASTEST: 18-20 ms
			mSensorManager.registerListener(this, mGyro,mSensorManager.SENSOR_DELAY_FASTEST); //SENSOR_DELAY_FASTEST: 18-20 ms
			mSensorManager.registerListener(this, mTemp,mSensorManager.SENSOR_DELAY_NORMAL); //SENSOR_DELAY_NORMAL: 215-230 ms
			mSensorManager.registerListener(this, mLight,mSensorManager.SENSOR_DELAY_NORMAL); //SENSOR_DELAY_NORMAL: 215-230 ms
			mSensorManager.registerListener(this, mProximity,mSensorManager.SENSOR_DELAY_NORMAL); //SENSOR_DELAY_NORMAL: 215-230 ms
			mSensorManager.registerListener(this, mMagnetic,mSensorManager.SENSOR_DELAY_NORMAL); //SENSOR_DELAY_NORMAL: 215-230 ms
			locationManager.requestLocationUpdates(provider, 10000, 1, this);
			
			this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		} 
	}
	
	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
	    @Override
	    public void onReceive(Context ctxt, Intent intent) {
	    	batLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
	    	int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
	    	boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
	    	int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
	    	boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
	    	if(isCharging){
	    		if(usbCharge){
	    			ChargingStatus="charging (usb)";
	    		} else {
	    			ChargingStatus="charging (AC)";
	    		}
	    	} else {
	    		ChargingStatus="unplugged";
	    	}
	    }
	};
	
	private void menuIpPort() {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View promptView = layoutInflater.inflate(R.layout.prompts,null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		// set prompts.xml to be the layout file of the alert dialog builder
		alertDialogBuilder.setView(promptView);
		final EditText input_1 = (EditText) promptView.findViewById(R.id.userInput_01);
		final EditText input_2 = (EditText) promptView.findViewById(R.id.userInput_02);	
		final EditText input_3 = (EditText) promptView.findViewById(R.id.userInput_03);	
		// setup a dialog window (IP, port)
		alertDialogBuilder.setCancelable(false).setPositiveButton("Update", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Pattern pattern = Pattern.compile(PATTERN);
				Matcher matcher = pattern.matcher(input_1.getText().toString());
				boolean isIntegerAdHoc = isInteger(input_2.getText().toString());
				if(isIntegerAdHoc){
					if(Integer.parseInt(input_2.getText().toString())>65535){
						isIntegerAdHoc=false;
					}
				}
				if(isInteger(input_3.getText().toString())==true){
					timeInMeasures=Integer.parseInt(input_3.getText().toString());
				}
				if(matcher.matches() & isIntegerAdHoc){
					IP_add = input_1.getText().toString();
					port = input_2.getText().toString();
					Toast toast = Toast.makeText(context,"IP & Port updated",Toast.LENGTH_SHORT);
					toast.show();
					generateNoteOnSD("configuration.txt", IP_add + ":" + port);
				} else {
					getIPPortFromSD();
					Toast toast = Toast.makeText(context,"Using: " + IP_add +":"+ port,Toast.LENGTH_LONG);
					toast.show();	
				}
				Toast toast = Toast.makeText(context,"Time rate: " + timeInMeasures,Toast.LENGTH_LONG);
				toast.show();
			}		
		}).setNegativeButton("Ignore",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,	int id) {
				getIPPortFromSD();
				Toast toast = Toast.makeText(context,"Ignored\nUsing: " + IP_add +":"+ port,Toast.LENGTH_SHORT);
				toast.show();
				Toast toast2 = Toast.makeText(context,"Time rate: " + timeInMeasures,Toast.LENGTH_LONG);
				toast2.show();
				dialog.cancel();
			}
		});
		AlertDialog alertD = alertDialogBuilder.create();
		alertD.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		latField=(float) (location.getLatitude());
		lngField=(float) (location.getLongitude());
		altField=(float) (location.getAltitude());
		location=null;
		Thread.currentThread().interrupt();
		return;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}
	float[] mGeomagnetic = null;
	float[] mAccelerometer = null;
	float[] mGyroscope = null;
	float mDegrees[] = new float[3];
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		java.util.Date date= new java.util.Date();
		text1.setText(String.valueOf(new Timestamp(date.getTime())));
		text2_x.setText(String.valueOf(latField));
		text2_y.setText(String.valueOf(lngField));
		text2_z.setText(String.valueOf(altField));
		text8.setText(String.valueOf(batLevel) + "%");
		text9.setText(getUsedProcessors());
		text10.setText(ChargingStatus);
		
		
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			text3_x.setText(Float.toString(event.values[0]));
			text3_y.setText(Float.toString(event.values[1]));
			text3_z.setText(Float.toString(event.values[2]));
			mAccelerometer = event.values;
		}
		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
		//if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			text4_x.setText(Float.toString(event.values[0]));
			text4_y.setText(Float.toString(event.values[1]));
			text4_z.setText(Float.toString(event.values[2]));
			mGyroscope = event.values;
		}
		
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			text11_x.setText(Float.toString(event.values[0]));
			text11_y.setText(Float.toString(event.values[1]));
			text11_z.setText(Float.toString(event.values[2]));
			mGeomagnetic = event.values;
		}
		if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
			text5.setText(Float.toString(event.values[0]));
		}
		if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
			text6.setText(Float.toString(event.values[0]));
		}
		if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
			text7.setText(Float.toString(event.values[0]));
		}
		
		//This part of the code should be performed only a few times per minute....
		if(mainWifi.isWifiEnabled()==true){
			int aux = WifiManager.calculateSignalLevel(mainWifi.getConnectionInfo().getRssi(),100);
			text12.setText("WiFi Enabled: " + Integer.toString(aux) + " (stregnth 1-99)");
			//text12.setText(mainWifi.getConnectionInfo().getSSID());
		} else {
			text12.setText("WiFi Disabled");
		}
		
		final Button streaming = (Button) this.findViewById(R.id.button_streaming);
		if(Streaming==false){
			streaming.setText("Start Streaming");
		}
		
		//Calculating degrees 		
	    float azimut=0;
		float pitch=0;
		float roll=0;
		if (mAccelerometer != null && mGeomagnetic != null) {
	       float R[] = new float[9];
	       float I[] = new float[9];
	       boolean success = SensorManager.getRotationMatrix(R, I, mAccelerometer, mGeomagnetic);
	       if (success) {  
	    	   float orientation[] = new float[3];
	    	   SensorManager.getOrientation(R, orientation);
	    	   SensorManager.getOrientation(R, orientation);
	    	   azimut = (float) Math.toDegrees(orientation[0]); // orientation contains: azimut, pitch and roll
	    	   pitch = (float) Math.toDegrees(orientation[1]);
	    	   roll = (float) Math.toDegrees(orientation[2]);
	    	   mDegrees[0]=azimut;
	    	   mDegrees[1]=pitch;
	    	   mDegrees[2]=roll;
	       }
	    }
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onPause(){
		super.onPause();
		mSensorManager.unregisterListener(this);
		locationManager.removeUpdates(this);
		Streaming=false;
//		//NEW Bluetooth
	    Log.i("\n...In onPause()...","");
	    
	    if (outStream != null) {
	      try {
	        outStream.flush();
	      } catch (IOException e) {
	        Log.i("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
	      }
	    }
	 
	    try     {
	      btSocket.close();
	    } catch (IOException e2) {
	      Log.i("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
	    }
//		//END Bluetooth
	}
	
	
//	//NEW Bluetooth
	  public void onResume() {
	    super.onResume();
	 
	    
	    // Set up a pointer to the remote node using it's address.
	    BluetoothDevice device = btAdapter.getRemoteDevice(address);
	 
	    // Two things are needed to make a connection:
	    //   A MAC address, which we got above.
	    //   A Service ID or UUID.  In this case we are using the
	    //     UUID for SPP.
	    try {
	      btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
	    } catch (IOException e) {
	      Log.i("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
	    }
	 
	    // Discovery is resource intensive.  Make sure it isn't going on
	    // when you attempt to connect and pass your message.
	    btAdapter.cancelDiscovery();
	 
	    // Establish the connection.  This will block until it connects.
	    try {
	      btSocket.connect();
	      Log.i("Connection Stat","\n...Connection established and data link opened...");
	    } catch (IOException e) {
	      try {
	        btSocket.close();
	      } catch (IOException e2) {
	        Log.i("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
	      }
	    }
	 
	    // Create a data stream so we can talk to server.
	    Log.i("Connection Stat","\n...Sending message to server...");
	 
	    try {
	      outStream = btSocket.getOutputStream();
	      Log.i("Outstream","\n...outstream...");
	    } catch (IOException e) {
	      Log.i("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
	    }
	 
	    String message = "Hello from Android Amazing Mobile!!!!!!!!!!!!!!!! Ja ja ja!!!\n";
	    byte[] msgBuffer = message.getBytes();
	    try {
	      outStream.write(msgBuffer);
	      //outStream.flush();
	      Log.i("Outstream:"," writing");
	    } catch (IOException e) {
	      String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
	      if (address.equals("00:00:00:00:00:00")) 
	        msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 37 in the java code";
	      msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";
	       
	      Log.i("Fatal Error", msg);       
	    }
	    
	    //Receiving "the stuff" :)!!!
	    byte[] buffer = new byte[256];  // buffer store for the stream
        int bytes; // bytes returned from read()
	    try {
		      inStream = btSocket.getInputStream();
		      Log.i("Instream","\n...Instream...");
		} catch (IOException e) {
		      Log.i("Fatal Error", "In onResume() and input stream creation failed:" + e.getMessage() + ".");
		}
	    InputStream tmpIn = null;
	    try {
			tmpIn = btSocket.getInputStream();
			DataInputStream mmInStream = new DataInputStream(tmpIn);
			 // Read from the InputStream
	        bytes = mmInStream.read(buffer);
	        String readMessage = new String(buffer, 0, bytes);
	        Log.i("Some shit!",readMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
//	//END Bluetooth

	@Override
	public void onStop(){
		super.onStop();
		mSensorManager.unregisterListener(this);
		locationManager.removeUpdates(this);
		Streaming=false;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mSensorManager.unregisterListener(this);
		locationManager.removeUpdates(this);
	}
	
	public static String getUsedProcessors() {
	    int totalProcessors = 0;
	    try {
	        Runtime info = Runtime.getRuntime();
	        totalProcessors = info.availableProcessors();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return (Integer.toString(totalProcessors));    
	}
	
	public static boolean isInteger(String s) {
		boolean isValidInteger = false;
		try	{
			Integer.parseInt(s);
			isValidInteger = true;
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}
		return isValidInteger;
	}
	
	public void generateNoteOnSD(String sFileName, String sBody){
		try {
			File root = new File(Environment.getExternalStorageDirectory(), "Data Sensors BETA");
			if (!root.exists()) {
				root.mkdirs();
			}
			File gpxfile = new File(root, sFileName);
			FileWriter writer = new FileWriter(gpxfile,true);
			writer.append(System.getProperty("line.separator"));
			writer.append(sBody);
			writer.flush();
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getIPPortFromSD() {
		try {
			File root = new File(Environment.getExternalStorageDirectory(), "Data Sensors BETA");
			if (!root.exists()) {
				root.mkdirs();
			}
			File gpxfile = new File(root, "configuration.txt");
			FileReader reader = new FileReader(gpxfile);
			BufferedReader br = new BufferedReader(reader);
			String s;
			while((s = br.readLine()) != null) {
				IP_add=s;
			} 
			reader.close();
			port = (String) IP_add.subSequence(IP_add.indexOf(":")+1, IP_add.length());
			IP_add=(String) IP_add.subSequence(0, IP_add.indexOf(":"));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isMyServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ("com.prototype.sensors.SensorService".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	private boolean isStreamingRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ("com.prototype.sensors.Streaming".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public void startRecording(String action) {
		intentService=new Intent(context, SensorService.class);
		intentService.putExtra("Port_defined", port);
		intentService.putExtra("IP_defined",IP_add);
		intentService.putExtra("timeRate",String.valueOf(timeInMeasures));
		intentService.putExtra("action",action);
		intentService.putExtra("TIME", String.valueOf(textView1.isChecked()));
		intentService.putExtra("GPS", String.valueOf(textView2.isChecked()));
		intentService.putExtra("ACCELEROMETER", String.valueOf(textView3.isChecked()));
		intentService.putExtra("GYROSCOPE", String.valueOf(textView4.isChecked()));
		intentService.putExtra("TEMPERATURE", String.valueOf(textView5.isChecked()));
		intentService.putExtra("LIGHT", String.valueOf(textView6.isChecked()));
		intentService.putExtra("PROXIMITY", String.valueOf(textView7.isChecked()));
		intentService.putExtra("BATTERY", String.valueOf(textView8.isChecked()));
		intentService.putExtra("PROCESSORS", String.valueOf(textView9.isChecked()));
		intentService.putExtra("CHARGE", String.valueOf(textView10.isChecked()));
		intentService.putExtra("MAGNETICFIELD", String.valueOf(textView11.isChecked()));
		intentService.putExtra("WIFI", String.valueOf(textView12.isChecked()));
		context.startService(intentService);
	}

	public void startStreaming(final String action) {
		if(action.equals("start")){
			new AsyncTask<Void, Void, Void>() {
				@Override public Void doInBackground(Void... arg) {
					do {
						streamingString ="*0*" + String.valueOf(mAccelerometer[0]) + "*1*" + String.valueOf(mAccelerometer[1]) + "*2*" + String.valueOf(mAccelerometer[2])+
								 "*3*" + String.valueOf(mGeomagnetic[0])+ "*4*" + String.valueOf(mGeomagnetic[1])+ "*5*" + String.valueOf(mGeomagnetic[2])+
								 "*6*" + String.valueOf(mGyroscope[0])+ "*7*" + String.valueOf(mGyroscope[1])+ "*8*" + String.valueOf(mGyroscope[2]) + "*9*" +
								 String.valueOf(mDegrees[0]) + "*10*" + String.valueOf(mDegrees[1]) + "*11*" + String.valueOf(mDegrees[2]) + "*12*";
						
						final dataClient senderSock = new dataClient(streamingString);
						try {
							senderSock.sendData(IP_add, port, "aux_NOT USED", streamingString);
							streamingString=null;
							Thread.sleep(10);
							} 
						catch (Exception e) {   
							Log.e("SendingDatagram ERROR", e.getMessage(), e);   
							}
						} while(Streaming==true);
					return null;
				}
			}.execute();
		} else if (action.equals("stop")){
			senderSock=null;
		}	
	}
}
