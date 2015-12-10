package fr.univorleans.m2inis.sig;
import java.util.*;

public class Line{
	public Line(){}
	
	void addPoint(Point p){
		//if(true)throw new RuntimeException("Ca marche!");
		pts.add(p);
	}
	public List<Point> getPoints(){
		return pts;
	}
	private List<Point>pts=new LinkedList<Point>();
}
