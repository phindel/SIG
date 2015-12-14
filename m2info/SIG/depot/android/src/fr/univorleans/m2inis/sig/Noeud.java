package fr.univorleans.m2inis.sig;

import java.util.*;
public class Noeud<N extends Noeud> implements java.io.Serializable{
	public Noeud(Point pos_,int id_){
		pos=pos_;
		id=id_;
	}
	private int id;
	public int getId(){
		return id;
	}
	public void add(N n,int typeChemin){
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


