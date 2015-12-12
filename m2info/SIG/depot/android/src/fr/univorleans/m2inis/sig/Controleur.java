package fr.univorleans.m2inis.sig;

public class Controleur implements OnZoneSelectedListener,OnPointSelectedListener{
	public Controleur(MapView mapView_){
		mapView=mapView_;
	}
	private MapView mapView;
	private Zone dernierZoneValableSelectionnee;
	public void onZoneSelected(Zone z){
		if(z.getType()==Zone.parking||z.getType()==Zone.batimentUniv)
			dernierZoneValableSelectionnee=z;
	}
	public void setDataSource(IDataSource src){
		dataSource=src;
	}
	private IDataSource dataSource;
	public void onPointSelected(Point pos){
		mapView.setZoneSelection(true);
		if(dernierZoneValableSelectionnee==null)
			return;
		Line cheminPropose;
		if(dernierZoneValableSelectionnee.getPointEntree()!=null)
		cheminPropose=dataSource.computePath(pos,null,null,dernierZoneValableSelectionnee.getPointEntree());
		else
			cheminPropose=dataSource.computePath(pos,dernierZoneValableSelectionnee.getCenter(),null,null);
		Point prev=null;
		double dist=0;
		for(Point p:cheminPropose.getPoints()){
			if(prev!=null){
				dist+=prev.distance2(p);
			}
			prev=p;
		}
		mapView.setItineraire(cheminPropose);
	}
	public void rechParkingLePlusProche(Point p){
		//TODO remplir dernierZoneValableSelectionnee
	}
	public void rechParkingLePlusProcheDuBatimentUnivActuel(){
		//TODO remplir dernierZoneValableSelectionnee
	}
	public void rechItineraire_etape1(){
		//TODO utile?
	}
}
