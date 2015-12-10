package fr.univorleans.m2inis.sig;
public class Pourcent{
	public Pourcent(long max_){
		max=max_;
	}
	private long max,val;
	public void update(long v_){
		val=v_;
	}
	public double getValue(){
		return val/(double)max;
	}
	public String toString(){
		String s=(Math.round((val*1000)/max)*.1)+"";
		if(s.indexOf(".")!=-1)
			s=s.substring(0,s.indexOf(".")+2);
		return s+"%";
	}
}
