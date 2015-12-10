package fr.univorleans.m2inis.sig;
import java.util.*;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.BitmapFactory.Options;
import android.graphics.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.TextView;
import android.view.WindowManager;
import android.view.MotionEvent;
import android.location.*;
import android.widget.Button;
public class MainActivity extends Activity {

    private MapView mMapView;
    private WindowManager mWindowManager;
    private Display mDisplay;
    private Throwable error;
	private void showError(Throwable re){
		error=re;
		
	}
	public void checkError(){
		if(error!=null){
			TextView t=new TextView(this);
			String txt="Err  "+error.toString();
			for(StackTraceElement st:error.getStackTrace()){
				txt+="\n"+ st;
			}
			t.setText(txt);
			setContentView(t);
		}
	}
	
	/**/
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		setContentView(R.layout.main);
        

	//findViewById(R.id.zDessin);
	try{
		TextView textview=(TextView)findViewById(R.id.textview);
		textview.setText("Hello!");
		textEtat=textview;
		buttonItineraire=(Button)findViewById(R.id.buttonItineraire);
		LocationManager locationManager= (LocationManager)  getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location l) {
			// Called when a new location is found by the network location provider.
			//makeUseOfNewLocation(location);
			mMapView.setUserLocation(l.getLongitude(),l.getLatitude());
			//throw new RuntimeException("Ca marche "+getClass());
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
			};

// Register the listener with the Location Manager to receive location updates
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);//GPS_PROVIDER NETWORK_PROVIDER
			}catch(Throwable re){   
					showError(re);
				}
        // Get an instance of the WindowManager
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();

		//if(true)throw new RuntimeException();
        // instantiate our simulation view and set it as the activity's content
        //mMapView = new MapView(this);
        //setContentView(mSimulationView);
        mMapView=(MapView)findViewById(R.id.zDessin);
        mMapView.setParent(this);
        Thread thr=new Thread(){
        	public void run(){
        		try{
        			mMapView.setDataSource(new LocalDataSource(mMapView,getResources().openRawResource(R.raw.line),getResources().openRawResource(R.raw.zones)));
        		}catch(Throwable re){
					showError(re);
				}
        	}
        };
        thr.start();
        //mMapView = (MapView)findViewById(R.id.zDessin);
		
		//textview.setText("Hello!");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
	private Button buttonItineraire;
    @Override
    protected void onPause() {
        super.onPause();
    }
    public void setRechItineraire(boolean b){
    	rechItineraire=b;
    	if(rechItineraire)
    		buttonItineraire.setText("Annuler la recherche");
    	else
    		buttonItineraire.setText("Rechercher un itinéraire");
    }
    public boolean isRechItineraire(){
    	return rechItineraire;
    }
    private boolean rechItineraire=false;
	public void rechItineraire(View view) {
		//
		setRechItineraire(!rechItineraire);
	}
	TextView textEtat;
    public void setState(String st){
    	 textEtat.setText(st);
    }
}
