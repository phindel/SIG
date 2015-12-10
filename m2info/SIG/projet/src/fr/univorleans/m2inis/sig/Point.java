package fr.univorleans.m2inis.sig;

public class Point implements java.io.Serializable{
	public Point(double x_,double y_){
		x=x_;
		y=y_;
	}
	@Override
	public boolean equals(Object o){
		if(o==null||o.getClass()!=getClass())
			return false;
		Point p=(Point)o;
		return x==p.x&&y==p.y;
	}
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	public double moinsX(Point p){
		return x-p.x;
	}
	public double moinsY(Point p){
		return y-p.y;
	}
	private boolean marque;
	public boolean isMarque(){
		return marque;
	}
	public void marquer(){
		marque=true;
	}
	public void setMarque(boolean b){
		marque=b;
	}
	public String toString(){
		return x+","+y;
	}
	public double distance1(Point p){
		return Math.abs(x-p.x)+Math.abs(y-p.y);
	}
	public double distance2(Point p){
		return Math.sqrt((x-p.x)*(x-p.x)+(y-p.y)*(y-p.y));
	}
	private double x,y;
}
