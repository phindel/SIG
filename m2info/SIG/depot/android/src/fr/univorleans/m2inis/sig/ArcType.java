package fr.univorleans.m2inis.sig;

/*
Classe pour représenter un arc dans le Graphe
Composé d'un noeud et d'un type (à choisir dans les constantes de la classe)
*/
public class ArcType<N>{
	public ArcType(N n_,int type_){
		n=n_;
		type=type_;
	}
	public static final int route=1,cheminPietonier=2,coursDEau=3,tramway=4;
	public static final int nombreDeType=4;//TODO mettre à jour quand on change le nombre de type
	private N n;
	private int type;
	public N getNoeud(){
		return n;
	}
	public int getType(){
		return type;
	}
	public boolean isArcSurLequelOnPeutMarcher(){
		return type==route||type==cheminPietonier;
	}
}
