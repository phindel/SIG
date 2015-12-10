package fr.univorleans.m2inis.sig;
import java.util.List;
import android.graphics.Color;
public class Zone{
	public Zone(String nom_,List<Point>polygoneExterieur_,int type_){
		nom=nom_;
		polygoneExterieur=polygoneExterieur_;
		type=type_;
	}
	private int type;
	public static final int lac=1,foret=2,parking=3,batimentUniv=4,autreBatiment=5;
	public int getColor(){
		switch(type){
			case lac:
				return Color.BLUE;
			case foret:
				return Color.GREEN;
			case parking:
				return Color.WHITE;
			case batimentUniv:
				return Color.RED;
			case autreBatiment:
				return Color.GRAY;
			default:
			throw new RuntimeException("type non valide");
		}
		//return Color.WHITE;
	}
	public String getNom(){
		return nom;
	}
	public List<Point> getLimite(){
		return polygoneExterieur;
	}
	private String nom;
	private List<Point>polygoneExterieur;
}
