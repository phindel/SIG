package fr.univorleans.m2inis.sig;
import java.util.*;
/*Noeud pour le parcourt dans le graphe des routes
On a deux type d'arcs partant de ce type de Noeud:
	les arcs classiques: de type NoeudPourParcourt
	un arc de fin: noeudFin qui désigne une fin de trajectoire
*/
public class NoeudPourParcourt extends Noeud<NoeudPourParcourt>{
	public NoeudPourParcourt(Point pos,int id){
		super(pos,id);
	}
	/*
	Appelé quand on commence une recherche de chemin (Dijkstra)
	*/
	public final void reinit(){
		if(pos!=null)
			pos.setMarque(false);
		parent=null;
		fixe=false;
	}
	public void marquer(){
		pos.marquer();
	}
	public boolean isParcouru(){
		return fixe;
	}
	private ArcType<NoeudPourParcourt> conv(ArcType<Noeud<NoeudPourParcourt>> a){
		return new ArcType<NoeudPourParcourt>((NoeudPourParcourt)a.getNoeud(),a.getType());
	}
	/*fournit un itérateur sur les voisins du points (ceux habituels et éventuellement le demi-noeud de fin)*/
	public Iterable<ArcType<NoeudPourParcourt>> getVoisinsParcourt(){
		return new Iterable<ArcType<NoeudPourParcourt>>(){
			public Iterator<ArcType<NoeudPourParcourt>> iterator(){
				return new Iterator<ArcType<NoeudPourParcourt>>(){
					private boolean noeudFinLu=false;
					private Iterator<ArcType<Noeud<NoeudPourParcourt>>>itVoisins=voisins.iterator();
					public ArcType<NoeudPourParcourt> next(){
						if(!hasNext())
							throw new IllegalStateException("No more element");
						if(noeudFinLu)
							return conv(itVoisins.next());
						else{
							ArcType<NoeudPourParcourt> res;
							noeudFinLu=true;
							if(noeudFin!=null)
								res=new ArcType<NoeudPourParcourt>(noeudFin,1);//TODO
							else
								res=conv(itVoisins.next());
							return res;
						}
					}
					public boolean hasNext(){
						if(!noeudFinLu){
							if(noeudFin!=null)
								return true;
							return itVoisins.hasNext();
						}else
							return itVoisins.hasNext();
					}
					public void remove(){
						throw new RuntimeException("Liste en lecture seule");
					}
				};
			}
		};
		//return voisins;
	}
	protected DemiNoeud noeudFin;//null si on ne pointe pas vers un noeud de fin (on ne garde qu'un de ces noeuds; on devrait garder le plus proche)
	NoeudPourParcourt parent;//algo de Dijkstra
	double distance;//algo de Dijkstra
	boolean fixe;//algo de Dijkstra
}


