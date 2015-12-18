package fr.univorleans.m2inis.sig;

import java.util.*;
/*
Un Noeud d'un graphe représente une intersection de segments de routes
Il sera ainsi possible de parcourir un graphe qui représente un carte
*/
public class Noeud<N extends Noeud> implements java.io.Serializable{
	public Noeud(Point pos_,int id_){
		pos=pos_;
		id=id_;
	}
	private int id;
	public int getId(){
		return id;
	}
	public void add(Noeud<N> n,int typeChemin){
		voisins.add(new ArcType<Noeud<N>>(n,typeChemin));
		n.voisins.add(new ArcType<Noeud<N>>(this,typeChemin));
	}
	public double distance2(Noeud n){
		return pos.distance2(n.pos);
	}
	public Point getPosition(){
		return pos;
	}
	@Override
	public String toString(){
		return pos.toString();
	}
	
	public Iterable<ArcType<Noeud<N>>> getVoisins(){
		return voisins;
	}
	@Override
	public boolean equals(Object o){
		if(this==o)
			return true;
		if(o==null||o.getClass()!=getClass())
			return false;
		Noeud n=(Noeud)o;
		return pos.equals(n.pos);
	}
	@Override
	public int hashCode(){
		return pos.hashCode();
	}
	/*public Collection<Noeud> getVoisins(){
		return voisins;
	}*/

	protected Point pos;
	protected Collection<ArcType<Noeud<N>>>voisins=new ArrayList<ArcType<Noeud<N>>>();
	private Collection<Face>faces=new ArrayList<Face>();
}


