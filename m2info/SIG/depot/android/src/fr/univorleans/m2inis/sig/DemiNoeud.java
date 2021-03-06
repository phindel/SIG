package fr.univorleans.m2inis.sig;
import java.util.*;
/*
Noeud de début ou de fin (avec arcs orientés)
Un DemiNoeud est capable de dormir et de se réveiller (pour usage par un ProjectionPlan lors de la recherche de chemin)
Il faut appeler clear() ou dormir() après usage (pour la mémoire et pour ne pas perturber la recherche de chemin)
@sa fr.univorleans.m2inis.sig.ProjectionPlan
@sa fr.univorleans.m2inis.sig.Graphe#computePath1
*/
public class DemiNoeud extends NoeudPourParcourt{
	public DemiNoeud(Point pos){
		super(pos,-1);//-1 -> pas d'affichage des arcs lié à ce noeud
	}
	/*public void reinit(Point pos){
		reinit();
		for(NoeudPourParcourt n:referencePar)
			n.noeudFin=null;
		this.pos=pos;
		voisinsDebut.clear();
		referencePar.clear();
	}*/
	public void clear(){
		reinit();
		for(NoeudPourParcourt n:referencePar)//gestion de la mémoire
			n.noeudFin=null;
		voisinsDebut.clear();
		referencePar.clear();
	}
	
	/*@Override
	public Collection<Noeud> getVoisins(){
		return voisinsDebut;
	}*/
	@Override
	public Iterable<ArcType<NoeudPourParcourt>> getVoisinsParcourt(){
		return voisinsDebut;
	}
	private Collection<ArcType<NoeudPourParcourt>>voisinsDebut=new ArrayList<ArcType<NoeudPourParcourt>>();
	private Collection<NoeudPourParcourt>referencePar=new ArrayList<NoeudPourParcourt>();
	public void addDebut(NoeudPourParcourt n){
		voisinsDebut.add(new ArcType<NoeudPourParcourt>(n,1));
	}
	public void addFin(NoeudPourParcourt n){
		referencePar.add(n);
		n.noeudFin=this;
	}
	public void dormir(){
		for(NoeudPourParcourt n:referencePar)
			n.noeudFin=null;
		reinit();
	}
	public void reveiller(){
		for(NoeudPourParcourt n:referencePar)
			n.noeudFin=this;
	}
}
