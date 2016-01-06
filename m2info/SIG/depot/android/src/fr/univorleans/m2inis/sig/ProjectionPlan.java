package fr.univorleans.m2inis.sig;

import java.util.*;
/*
Permet de calculer une fois pour toutes les projection de points sur segments (qui prennent du temps)
Cycle de vie:
	A sa création, l'object appelle dormir()
	Usage: reveiller() -> utilisation-> dormir()
	Destruction: clear()
Ne pas invoquer clear() après le dernier usage peut provoquer des fuites de mémoire. Il ne faut plus utiliser l'objet après un clear()
*/
public class ProjectionPlan{
	public ProjectionPlan(Graphe g,Collection<Point> begin,Collection<Point> end){
				
		
		
		for(Point p:begin){
			Graphe.ProjSurSegment proj=g.projectionSurSegment(p);
			DemiNoeud dn=new DemiNoeud(proj.getProj());
			demiNoeuds.add(dn);
			lbegin.add(dn);
			dn.addDebut(proj.getA());
			if(proj.getB()!=proj.getA())//arrive quand le point d'un segment le plus proche d'un point est à une des extrémités
				dn.addDebut(proj.getB());
		}
		for(Point p:end){
			Graphe.ProjSurSegment proj=g.projectionSurSegment(p);
			DemiNoeud dn=new DemiNoeud(proj.getProj());
			demiNoeuds.add(dn);
			lend.add(dn);
			dn.addFin(proj.getA());
			if(proj.getB()!=proj.getA())//arrive quand le point d'un segment le plus proche d'un point est à une des extrémités
				dn.addFin(proj.getB());
		}
		dormir();
	}
	/*
	Retourne les Noeud correspondant aux points de début (parcourable)
	*/
	public Collection<NoeudPourParcourt> getLbegin(){
		return lbegin;
	}
	/*
	Retourne les Noeud correspondant aux points de fin (parcourable)
	*/
	public Set<NoeudPourParcourt>getLend(){
		return lend;
	}
	private Collection<NoeudPourParcourt>lbegin=new LinkedList<NoeudPourParcourt>();
	private Set<NoeudPourParcourt>lend=new HashSet<NoeudPourParcourt>();
	public void dormir(){
		for(DemiNoeud d:demiNoeuds)
			d.dormir();
	}
	public void reveiller(){
		for(DemiNoeud d:demiNoeuds)
			d.reveiller();
	}
	private Collection<DemiNoeud> demiNoeuds=new ArrayList<DemiNoeud>();//pour la gestion de la mémoire
	public void clear(){
		for(DemiNoeud d:demiNoeuds)
			d.clear();
		demiNoeuds.clear();
	}
}
