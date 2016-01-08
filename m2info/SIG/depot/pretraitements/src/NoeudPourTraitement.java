
import fr.univorleans.m2inis.sig.*;
/*Un NoeudPourTraitement sert à représenter un Noeud dans le cas du prétraitement*/
public class NoeudPourTraitement extends Noeud<NoeudPourTraitement>{
	public NoeudPourTraitement(Point pos,int id){
		super(pos,id);
	}
	/*Affiche les voisins dont l'id est inférieur à l'id du noeud actuel*/
	public void printVoisins(java.io.PrintStream ps){
		for(ArcType<Noeud<NoeudPourTraitement>> a:getVoisins()){
			NoeudPourTraitement n=(NoeudPourTraitement)a.getNoeud();
			if(getId()>n.getId())
				ps.print(n.getId()+" "+a.getType()+" ");
		}
		ps.println();
	}
}

