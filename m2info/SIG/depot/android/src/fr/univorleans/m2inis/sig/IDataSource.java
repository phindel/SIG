package fr.univorleans.m2inis.sig;
import java.util.*;
public interface IDataSource{
	Line computePath(Point begin,Point end);
	//Building getBuildingAt(Point p);//a une liste de services
	BatimentUniv getBuilding(String nom);
	Collection<Zone> getZoneAt(Point p);
	Collection<String> getBuildingsName();//pour pouvoir afficher la liste des noms
	Collection<Zone> getZones();//pour affichage
	Collection<Line> getLines(int type);
	int getLineTypeNumber();
	Point getCenter();
	public Point getMinPoint();
	public Point getMaxPoint();
	public Collection<NoeudPourParcourt> selectionnerNoeud(Point p);//TODO
	//Point getUserLocation();//peut être null
	//boolean isUserCentric();//la position de l'utilisateur doit être au milieu du dessin?
	
}
