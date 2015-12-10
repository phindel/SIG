package fr.univorleans.m2inis.sig;

public class Segment{
	public Segment(Point a_,Point b_){
		a=a_;
		b=b_;
	}
	private Point a,b;
	public Point getA(){
		return a;
	}
	public Point getB(){
		return b;
	}
	
	public Point intersecte(Point c,Point d){
		//if(true)return null;
		/*Segment s=new Segment(sa,sb);
		if (s.prodVectoriel(a,b) == 0)
			return null;

		double l = (s.prodVectoriel(a, s.a))
				/ s.prodVectoriel(a,b);

		if (l < 0 || l > 1)
			return null;
		else
			return new Point(a.getX() + l * (b.getX() - a.getX()), a.getY() + l* (b.getY() - a.getY()));*/
		double amb1=a.moinsX(b);
		double amb2=a.moinsY(b);
		double bmd1=b.moinsX(d);
		double bmd2=b.moinsY(d);
		double cmd1=c.moinsX(d);
		double cmd2=c.moinsY(d);
		double numt=prodScalaire(amb1,amb2,bmd1,bmd2);
		double denum=prodScalaire(amb1,amb2,cmd1,cmd2);
		if(denum==0)
			return null;
		if(Math.signum(numt)!=Math.signum(denum))//cas t<0
			return null;
		if(Math.abs(numt)>Math.abs(denum))//cas t>1
			return null;
		double nums=prodScalaire(cmd1,cmd2,bmd1,bmd2);
		if(Math.signum(nums)!=Math.signum(denum))//cas s<0
			return null;
		if(Math.abs(nums)>Math.abs(denum))//cas s>1
			return null;
		double t=numt/denum;
		return new Point(c.getX()*t+d.getX()*(1f-t),c.getY()*t+d.getY()*(1f-t));
	}
	public static double prodScalaire(double x1,double x2,double y1,double y2){
		return x1*y2-x2*y1;
	}
	public double prodVectoriel(Point sa,Point sb) {
		return (b.getX()-a.getX())*(sb.getY()-sa.getY())-(b.getY()-a.getY())*(sb.getX()-sa.getX());
	}
	/*public int dx,dy,fx,fy;//TODO accesseurs pour rendre ces champs private
	public void setRectangleCoord(int dx_,int dy_,int fx_,int fy_){
		dx=dx_;
		dy=dy_;
		fx=fx_;
		fy=fy_;
	}*/
}
