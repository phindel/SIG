package fr.univorleans.m2inis.sig;
import java.util.*;
/*
Noeud de début ou de fin
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
		voisinsDebut.add(new ArcType<NoeudPourParcourt>(n,1));//TODO
	}
	public void addFin(NoeudPourParcourt n){
		//TODO
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
