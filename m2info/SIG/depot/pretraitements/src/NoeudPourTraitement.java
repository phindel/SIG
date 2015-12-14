
import fr.univorleans.m2inis.sig.*;

public class NoeudPourTraitement extends Noeud<NoeudPourTraitement>{
	public NoeudPourTraitement(Point pos,int id){
		super(pos,id);
		//num=num_;
	}
	//private int num;
	public void printVoisins(java.io.PrintStream ps){
		for(ArcType<Noeud<NoeudPourTraitement>> a:getVoisins()){
			NoeudPourTraitement n=(NoeudPourTraitement)a.getNoeud();
			if(getId()>n.getId())
				ps.print(n.getId()+" "+a.getType()+" ");
		}
		ps.println();
	}
}

