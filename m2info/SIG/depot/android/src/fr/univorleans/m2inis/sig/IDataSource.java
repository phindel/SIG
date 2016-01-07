package fr.univorleans.m2inis.sig;
import java.util.*;
/*
Représente un service permettant récupérer des données d'un carte et de calculer des chemins
(Au début il s'agissait d'une sorte de DAO)

*/
public interface IDataSource{
	/*
	Calcule un chemin entre begin et end
	Normalement beginAlternatif et endAlternatif aurait dû être utilisable, mais il aurait fallut calculer les points d'entrée des Zone pour que ça soit utile
	@param nomProjectionPlan nom de la projection à utiliser; préalablement configurée via registerProjectionPlan, mais on peut mettre n'importe quoi d'autre si on n'a pas besoin de ProjectionPlan
	@sa registerProjectionPlan
	*/
	Line computePath(Collection<Point> begin,Collection<Point> end,Collection<NoeudPourParcourt>beginAlternatif,Collection<NoeudPourParcourt>endAlternatif,String nomProjectionPlan);
	/*
	Enregister un ProjectionPlan pour usage ultérieur
	Permet de calculer rapidement le parking le plus proche assez rapidement dans l'application
	Couteux en temps (car la projection d'un point sur un segment est couteux, dans ce code)
	Ne sert que parce que les points d'entrée des Zone ne sont pas calculés
	*/
	void registerProjectionPlan(String nom,Collection<Point> begin,Collection<Point> end);
	//Building getBuildingAt(Point p);//a une liste de services
	BatimentUniv getBuilding(String nom);
	Collection<Zone> getZoneAt(Point p);
	Collection<String> getBuildingsName();//pour pouvoir afficher la liste des noms
	Collection<Zone> getZones();//pour affichage
	Collection<Line> getLines(int type);//arrêtes du graphe qui sont du type en question (voir la classe ArcType); type est entre 0 et getLineTypeNumber()-1
	public Iterable<NoeudPourParcourt> getSommetsDuGraphe();
	int getLineTypeNumber();
	/*
	Retourne le centre de la bounding box de la zone
	*/
	Point getCenter();
	/*
	Retourne point (xm,ym) tel que xm est le minimum des abcisses des points de la zone, de même ym pour les ordonnées
	*/
	public Point getMinPoint();
	/*
	Retourne point (xm,ym) tel que xm est le maximum des abcisses des points de la zone, de même ym pour les ordonnées
	*/
	public Point getMaxPoint();
	/*
	Retourne le Noeud le plus proche du point et ses voisins vers lesquels on peut aller depuis ce noeud (plutôt inutile)
	*/
	public Collection<NoeudPourParcourt> selectionnerNoeud(Point p);

	
}
