package fr.univorleans.m2inis.sig;
import java.util.List;
import android.graphics.Color;
public class Zone{
	public Zone(String nom_,List<Point>polygoneExterieur_,int type_){
		nom=nom_;
		polygoneExterieur=polygoneExterieur_;
		type=type_;
		double minx=polygoneExterieur.get(0).getX();
		double maxx=minx;
		double miny=polygoneExterieur.get(0).getY();
		double maxy=miny;
		for(Point p:polygoneExterieur)
		{
			double x=p.getX();
			double y=p.getY();
			minx=Math.min(minx,x);
			miny=Math.min(miny,y);
			maxx=Math.max(maxx,x);
			maxy=Math.max(maxy,y);
		}
		minPoint=new Point(minx,miny);
		maxPoint=new Point(maxx,maxy);
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
	public Point getMinPoint(){
		return minPoint;
	}
	public Point getMaxPoint(){
		return maxPoint;
	}
	private Point minPoint,maxPoint;
	private String nom;
	private List<Point>polygoneExterieur;
}
