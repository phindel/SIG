package fr.univorleans.m2inis.sig;
import java.util.*;

public class Line{
	public Line(){}
	
	public void addPoint(Point p){
		pts.add(p);
	}
	public List<Point> getPoints(){
		return pts;
	}
	private List<Point>pts=new LinkedList<Point>();
}
