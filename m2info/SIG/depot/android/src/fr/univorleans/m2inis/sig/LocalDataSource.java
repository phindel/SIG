package fr.univorleans.m2inis.sig;
import java.util.*;
import java.io.*;

public class LocalDataSource implements IDataSource{
	
	private Graphe graphe;
	private ZoneManager zoneManager;
	public LocalDataSource(ILoaderObserver obs,java.io.InputStream lignes,java.io.InputStream zones)throws IOException{
		graphe=Graphe.fromLines(obs,lignes);
		zoneManager=ZoneManager.fromStream(obs,zones);
	}
	public Point getMinPoint(){
		return graphe.getMinPoint();//return new Point(minx,miny);
	}
	public Point getMaxPoint(){
		return graphe.getMaxPoint();//return new Point(maxx,maxy);
	}
	//private double minx,maxx,miny,maxy;
	public Point getCenter(){
		return graphe.getCenter();
	}
	public Iterable<NoeudPourParcourt> getSommetsDuGraphe(){
		return graphe.getSommetsDuGraphe();
	}
	public void registerProjectionPlan(String nom,Collection<Point> begin,Collection<Point> end){
		mProjectionPlan.put(nom,new ProjectionPlan(graphe,begin,end));
	}
	private Map<String,ProjectionPlan> mProjectionPlan=new HashMap<String,ProjectionPlan>();
	public Line computePath(Collection<Point> begin,Collection<Point> end,Collection<NoeudPourParcourt>beginAlternatif,Collection<NoeudPourParcourt>endAlternatif,String nomProjectionPlan){
		
		return graphe.computePath(begin,end,beginAlternatif,endAlternatif,mProjectionPlan.get(nomProjectionPlan));
	}
	public Collection<NoeudPourParcourt> selectionnerNoeud(Point p){
		return graphe.selectionnerNoeud(p);
	}
	/*public Building getBuildingAt(Point p){//a une liste de services
		return null;//TODO
	}*/
	public Collection<Line> getLines(int type){
		return graphe.getLines(type);
	}
	public int getLineTypeNumber(){
		return ArcType.nombreDeType;
	}
	public BatimentUniv getBuilding(String nom){
		return zoneManager.getBuilding(nom);
	}
	public Collection<String> getBuildingsName(){//pour pouvoir afficher la liste des noms
		return zoneManager.getBuildingsName();
	}
	public Collection<Zone> getZoneAt(Point p){
		return zoneManager.getZoneAt(p);
	}
	public Collection<Zone> getZones(){//pour affichage
		return zoneManager.getZones();
	}
}
