package fr.univorleans.m2inis.sig;
import java.util.*;

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
		if(dataSource!=null)
			throw new IllegalStateException("dataSource deja definie");
		dataSource=src;
		dataSource.registerProjectionPlan("parking",collectionVide,calculerCentreDesParking());
	}
	private static Collection<Point> toCollectionDePoint(Point p){
		if(p==null)
			return null;
		Collection<Point> res=new ArrayList<Point>();
		res.add(p);
		return res;
	}
	private IDataSource dataSource;
	public void onPointSelected(Point pos){
		mapView.setZoneSelection(true);
		if(dernierZoneValableSelectionnee==null)
			return;
		Line cheminPropose;
		Collection<NoeudPourParcourt> pointsDentree=new ArrayList<NoeudPourParcourt>();
		Point centreZone=dernierZoneValableSelectionnee.getPointEntree(pointsDentree);
		
		/*if(dernierZoneValableSelectionnee.getPointEntree()!=null)
			cheminPropose=dataSource.computePath(pos,null,null,dernierZoneValableSelectionnee.getPointEntree());
		else
			cheminPropose=dataSource.computePath(pos,dernierZoneValableSelectionnee.getCenter(),null,null);*/
		cheminPropose=dataSource.computePath(toCollectionDePoint(pos),toCollectionDePoint(centreZone),null,pointsDentree,"null");
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
	private Collection<Point> calculerCentreDesParking(){
		Collection<Point> res=new ArrayList<Point>();
		for(Zone z:dataSource.getZones()){
			if(z instanceof Parking)
				res.add(z.getCenter());
		}
		return res;
	}
	private Collection<Point> collectionVide=new ArrayList<Point>();
	public void rechParkingLePlusProche(Point p){
		//TODO remplir dernierZoneValableSelectionnee
		
		
		
		//TODO remplir centreDesParking
		//TODO? remplir les points d'entrée des parking
		Line cheminPropose;
		cheminPropose=dataSource.computePath(toCollectionDePoint(p),collectionVide,null,null,"parking");
		mapView.setItineraire(cheminPropose);
	}
	public void rechParkingLePlusProcheDuBatimentUnivActuel(){
		//TODO remplir dernierZoneValableSelectionnee
		Line cheminPropose;
		Collection<NoeudPourParcourt> pointsDentree=new ArrayList<NoeudPourParcourt>();
		Point centreZone=dernierZoneValableSelectionnee.getPointEntree(pointsDentree);
		cheminPropose=dataSource.computePath(toCollectionDePoint(centreZone),collectionVide,pointsDentree,null,"parking");
		mapView.setItineraire(cheminPropose);
	}
	public void rechItineraire_etape1(){
		//TODO utile?
	}
}
