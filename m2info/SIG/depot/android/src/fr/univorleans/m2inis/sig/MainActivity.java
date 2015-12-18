package fr.univorleans.m2inis.sig;
import java.util.*;
import android.app.Activity;
import android.content.Context;

import android.graphics.*;

import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import android.view.Display;

import android.view.View;
import android.widget.TextView;
import android.view.WindowManager;
import android.view.MotionEvent;
import android.location.*;
import android.widget.*;
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
		buttonRechParkingLePlusProcheLocalisationActuelle=(Button)findViewById(R.id.buttonRechParkingLePlusProcheLocalisationActuelle);
		listeDeZones=(ListView)findViewById(R.id.listeDeZones);
		LocationManager locationManager= (LocationManager)  getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location l) {
			// Called when a new location is found by the network location provider.
			//makeUseOfNewLocation(location);
			userLocation=new Point(l.getLongitude(),l.getLatitude());
			mMapView.setUserLocation(l.getLongitude(),l.getLatitude());
			buttonRechParkingLePlusProcheLocalisationActuelle.setEnabled(true);
			
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
        controleur=new Controleur(mMapView);
        mMapView.setOnZoneSelectedListener(controleur);
        mMapView.setOnPointSelectedListener(controleur);
        Thread thr=new Thread(){
        	public void run(){
        		try{
        			long prevTime=System.currentTimeMillis();
        			dataSource=new LocalDataSource(mMapView,getResources().openRawResource(R.raw.line),getResources().openRawResource(R.raw.zones));
        			long time=System.currentTimeMillis();
					String txt="";
					txt=(time-prevTime)+" ms";
					System.out.println("Durée du chargement: "+txt);
        			mMapView.setDataSource(dataSource);
        			controleur.setDataSource(dataSource);
        			runOnUiThread(new Runnable() {

					@Override
						public void run() {
							afficherListeBatiment(null);
						}
					});
        			
        		}catch(Throwable re){
					showError(re);
				}
        	}
        };
        thr.start();
        //mMapView = (MapView)findViewById(R.id.zDessin);
		
		//textview.setText("Hello!");
    }
    private Controleur controleur;
	private IDataSource dataSource;
    @Override
    protected void onResume() {
        super.onResume();
    }
	private Button buttonRechParkingLePlusProcheLocalisationActuelle;
    @Override
    protected void onPause() {
        super.onPause();
    }
    /*public void setRechItineraire(boolean b){
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
	}*/
	private Point userLocation;
	public void rechParkingLePlusProcheLocalisationActuelle(View view){
		controleur.rechParkingLePlusProche(userLocation);
	}
	public void rechParkingLePlusProcheDuBatimentUnivActuel(View view){
		controleur.rechParkingLePlusProcheDuBatimentUnivActuel();
	}
	public void rechItineraire_etape1(View view){
		controleur.rechItineraire_etape1();
		mMapView.setZoneSelection(false);
	}
	
	public void afficherListeBatiment(View view) {
		ArrayAdapter<String> arr=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		for(String nom:dataSource.getBuildingsName()){
			arr.add(nom);
		}
		listeDeZones.setAdapter(arr);
		listeDeZones.setOnItemClickListener(itemClickListener_batimentsUniv);
	}
	
	AdapterView.OnItemClickListener itemClickListener_batimentsUniv=new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, final View view,
		int position, long id) {
			String nom=(String)parent.getItemAtPosition(position);
			Zone z=dataSource.getBuilding(nom);
			mMapView.selectionnerZone(z);
			controleur.onZoneSelected(z);
		}

		};
	private ListView listeDeZones;
	TextView textEtat;//.getSelectedItem
    public void setState(String st){
    	 textEtat.setText(st);
    }
}
