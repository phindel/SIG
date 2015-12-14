package fr.univorleans.m2inis.sig;
import java.util.*;
import java.io.*;
public class Graphe{
	/*private Graphe(IDataSource ds_){
		
		ds=ds_;
		
		//if(true)throw new RuntimeException("t="+mapDecoupee.size());
	}*/
	public Collection<Line> getLines(int type){
		return lines.get(type);
	}
	/*private Noeud plusProcheNoeud(Point pos){
		
		Noeud res=col.iterator().next();
		for(Noeud n:col){
			if(n.getPosition().distance2(pos)<res.getPosition().distance2(pos))
				res=n;
		}
		return res;//TODO
	}*/
	public Collection<NoeudPourParcourt> selectionnerNoeud(Point p){
		Collection<NoeudPourParcourt> res=new ArrayList<NoeudPourParcourt>();
		NoeudPourParcourt plusProche=sommetsDuGraphe.iterator().next();
		for(NoeudPourParcourt n:sommetsDuGraphe){
			if(n.getPosition().distance2(p)<plusProche.getPosition().distance2(p))
				plusProche=n;
		}
		res.add(plusProche);
		for(ArcType<NoeudPourParcourt> a:plusProche.getVoisinsParcourt()){
			if(a.isArcSurLequelOnPeutMarcher())
				res.add(a.getNoeud());
		}
		return res;
	}
	private List<Collection<Line>> lines=new ArrayList<Collection<Line>>();
	private void readLineAsPoint(String ln){
		Scanner sc=new Scanner(ln);
		double x=Double.parseDouble(sc.next());
		double y=Double.parseDouble(sc.next());
		minx=Math.min(minx,x);
		miny=Math.min(miny,y);
		maxx=Math.max(maxx,x);
		maxy=Math.max(maxy,y);
		if(minx>x)throw new RuntimeException();
		if(miny>y)throw new RuntimeException();
		if(maxx<x)throw new RuntimeException();
		if(maxy<y)throw new RuntimeException();
		sommetsDuGraphe.add(new NoeudPourParcourt(new Point(x,y),sommetsDuGraphe.size()));
		
	}
	public Iterable<NoeudPourParcourt> getSommetsDuGraphe(){
		return sommetsDuGraphe;
	}
	private List<NoeudPourParcourt> sommetsDuGraphe=new ArrayList<NoeudPourParcourt>();
	private void readLineAsListeDAdjacence(int numPoint,String ln){
		NoeudPourParcourt nActuel=sommetsDuGraphe.get(numPoint);
		Scanner sc=new Scanner(ln);
		while(sc.hasNext()){
			
			int numAutre=Integer.parseInt(sc.next());
			int type=Integer.parseInt(sc.next());
			NoeudPourParcourt autre=sommetsDuGraphe.get(numAutre);
			nActuel.add(autre,type);
			Line line=new Line();
			line.addPoint(nActuel.getPosition());
			line.addPoint(autre.getPosition());
			lines.get(type-1).add(line);
		}
	}
	private Graphe(ILoaderObserver obs,java.io.InputStream fLignes)throws IOException{
		for(int type=1;type<=ArcType.nombreDeType;++type)
			lines.add(new ArrayList<Line>());
		BufferedReader br=null;
		long lcpt=0;
		try{
			br=new BufferedReader(new InputStreamReader(fLignes));
			br.readLine();//ligne à ignorer
			Pourcent pc=new Pourcent(fLignes.available());
			{
				String ln=br.readLine();
				Scanner tmp=new Scanner(ln);
				double x=Double.parseDouble(tmp.next());
				double y=Double.parseDouble(tmp.next());
				minx=maxx=x;
				miny=maxy=y;
				readLineAsPoint(ln);
				lcpt+=ln.length();
			}
		
			String ln="";
			while((ln=br.readLine())!=null&&!ln.equals("graphe")){
				readLineAsPoint(ln);
				pc.update(lcpt);
				obs.onStateChanged(pc,"Lecture des points "+pc+"");
				lcpt+=ln.length();
			}
			ln="";
			int numLigne=0;
			while((ln=br.readLine())!=null){
				readLineAsListeDAdjacence(numLigne,ln);
				pc.update(lcpt);
				obs.onStateChanged(pc,"Lecture de la liste d'adjacence "+pc+"");
				lcpt+=ln.length();
				++numLigne;
			}
			rechercherArcParcourable();
			
		}finally{
			if(br!=null)
				br.close();
		}
	}
	private void rechercherArcParcourable(){
		NoeudPourParcourt na,nb,proj;
		boolean trouveArcParcourable=false;
		for(NoeudPourParcourt n:sommetsDuGraphe){
			for(ArcType<NoeudPourParcourt> a:n.getVoisinsParcourt()){
				if(a.isArcSurLequelOnPeutMarcher()){
					noeudInitialPourProjection1=n;
					noeudInitialPourProjection2=a.getNoeud();
				}
			}
		}
		/*while(){
			na=sommetsDuGraphe.iterator().next();
			if(na.getVoisins().iterator().hasNext())
				nb=na.getVoisinsParcourt().iterator().next().getNoeud();
			else
				nb=na;
		}
		noeudInitialPourProjection1=na;
		noeudInitialPourProjection2=nb;*/
	}
	NoeudPourParcourt noeudInitialPourProjection1,noeudInitialPourProjection2;
	private double minx,maxx,miny,maxy;
	private IDataSource ds;
	
	public static Graphe fromLines(ILoaderObserver obs,java.io.InputStream fLignes)throws IOException{
		Graphe res=new Graphe(obs,fLignes);
		
		return res;
	}
	public Point getMinPoint(){
		return new Point(minx,miny);
	}
	public Point getMaxPoint(){
		return new Point(maxx,maxy);
	}
	public Point getCenter(){
		return new Point((minx+maxx)*.5,(miny+maxy)*.5);
	}
	public static class ProjSurSegment{
		/*
		Il s'agit du point a*coef+b*(1-coef)
		*/
		public ProjSurSegment(NoeudPourParcourt a_,Point proj_,double dist_,NoeudPourParcourt b_){
			a=a_;
			b=b_;
			proj=proj_;
			dist=dist_;
		}
		public Point getProj(){
			return proj;
		}
		public NoeudPourParcourt getA(){
			return a;
		}
		public NoeudPourParcourt getB(){
			return b;
		}
		public ProjSurSegment plusProche(NoeudPourParcourt n,Point pos){
			ProjSurSegment res=this;
			if(!n.getVoisins().iterator().hasNext()){
				ProjSurSegment p=projeter(n,n,pos);
				res=res.plusProche(p);
			}else
				for(ArcType<NoeudPourParcourt> a:n.getVoisinsParcourt()){
					NoeudPourParcourt v=a.getNoeud();
					if(a.isArcSurLequelOnPeutMarcher())
						res=res.plusProche(projeter(n,v,pos));
				}
			return res;
		}
		public ProjSurSegment plusProche(ProjSurSegment p){
			if(dist<p.dist)
				return this;
			else
				return p;
		}
		private NoeudPourParcourt a,b;
		private Point proj;
		private double dist;
	}
	//Projeter le point pos sur le segment [a,b], a peut être égal à b
	private static ProjSurSegment projeter(NoeudPourParcourt a,NoeudPourParcourt b,Point pos){
		if(a.equals(b))
			return new ProjSurSegment(a,a.getPosition(),pos.distance2(a.getPosition()),b);
		//cas pos sur la droite (a,b)
		double ax=a.getPosition().getX();
		double ay=a.getPosition().getY();
		double bx=b.getPosition().getX();
		double by=b.getPosition().getY();
		double dx=ax-bx;
		double dy=ay-by;
		double t=((pos.getX()-bx)*(ax-bx)+(pos.getY()-by)*(ay-by))/(dx*dx+dy*dy);
		if(t<0)
			t=0;
		else
			if(t>1)
			t=1;
		Point proj=new Point(t*dx+bx,t*dy+by);
		return new ProjSurSegment(a,proj,pos.distance2(proj),b);
	}
	//private Collection<NoeudPourParcourt> graphe;
	//private DemiNoeud noeudDebut=new DemiNoeud(),noeudFin=new DemiNoeud();
	
	
	public ProjSurSegment projectionSurSegment(Point pos){
		ProjSurSegment res=projeter(noeudInitialPourProjection1,noeudInitialPourProjection2,pos);
		//for(Collection<Noeud> c:mapDecoupee){
			for(NoeudPourParcourt n:sommetsDuGraphe){
				res=res.plusProche(n,pos);
			}
		//}
		
		//return null;//TODO
		return res;//new ProjSurSegment(na,proj,nb);
	}
	
	public Line computePath(Collection<Point> begin,Collection<Point> end,Collection<NoeudPourParcourt>beginAlternatif,Collection<NoeudPourParcourt>endAlternatif,ProjectionPlan projectionPlan){
		if(begin==null||end==null)throw new IllegalArgumentException("Pas pris en charge");//TODO
		/*if((begin==null)==(beginAlternatif==null))
			throw new IllegalArgumentException("Une option à la fois");
		if((end==null)==(endAlternatif==null))
			throw new IllegalArgumentException("Une option à la fois");*/
		
		long prevTime=System.currentTimeMillis();
		ProjectionPlan ppDynamic=null;
		if(begin.size()>0||end.size()>0){
			ppDynamic=new ProjectionPlan(this,begin,end);
			ppDynamic.reveiller();
		}
		if(projectionPlan!=null)
			projectionPlan.reveiller();
		long time=System.currentTimeMillis();
		System.out.println("computePath preparation: "+(time-prevTime)+" ms");
		/*ProjSurSegment projBegin=projectionSurSegment(begin);
		ProjSurSegment projEnd=projectionSurSegment(end);
		
		noeudDebut.reinit(projBegin.getProj());
		noeudFin.reinit(projEnd.getProj());
		lbegin.add(noeudDebut);
		lend.add(noeudFin);
		if(projBegin.getA()==projEnd.getA()&&projBegin.getB()==projEnd.getB()){
			noeudDebut.addDebut(noeudFin);
			noeudFin.addFin(noeudDebut);
		}else{
			noeudDebut.addDebut(projBegin.getA());
			if(projBegin.getB()!=projBegin.getA())//arrive quand le point d'un segment le plus proche d'un point est à une des extrémités
				noeudDebut.addDebut(projBegin.getB());
		
			noeudFin.addFin(projEnd.getA());
			if(projEnd.getA()!=projEnd.getB())
				noeudFin.addFin(projEnd.getB());
		}*/
		
		Collection<NoeudPourParcourt> lbegin=new ArrayList<NoeudPourParcourt>();
		Set<NoeudPourParcourt>lend=new HashSet<NoeudPourParcourt>();
		if(ppDynamic!=null){
			lbegin.addAll(ppDynamic.getLbegin());
			lend.addAll(ppDynamic.getLend());
		}
		if(projectionPlan!=null){
			lbegin.addAll(projectionPlan.getLbegin());
			lend.addAll(projectionPlan.getLend());
		}
		Line res=computePath1(lbegin,lend);
		if(projectionPlan!=null)
			projectionPlan.dormir();
		if(ppDynamic!=null)
			ppDynamic.clear();
		
		
		return res;
	}
	
	/*public Line computePath(Point begin,Point end,Collection<NoeudPourParcourt>beginAlternatif,Collection<NoeudPourParcourt>endAlternatif){
		noeudDebut.reinit(begin);
		noeudFin.reinit(end);
		Collection<NoeudPourParcourt>lbegin=new LinkedList<NoeudPourParcourt>();
		Collection<NoeudPourParcourt>lend=new LinkedList<NoeudPourParcourt>();
		if(false){
			//lbegin.add(plusProcheOld(begin));//TODO
			//lend.add(plusProcheOld(end));//TODO
		}else{
			/*lbegin.add(noeudDebut);
			lend.add(noeudFin);
			noeudDebut.addDebut(plusProcheOld(begin));
			noeudFin.addFin(plusProcheOld(end));* /
			ProjSurSegment projBegin=projectionSurSegment(begin);
			ProjSurSegment projEnd=projectionSurSegment(end);
			
			noeudDebut.reinit(projBegin.getProj());
			noeudFin.reinit(projEnd.getProj());
			lbegin.add(noeudDebut);
			lend.add(noeudFin);
			if(projBegin.getA()==projEnd.getA()&&projBegin.getB()==projEnd.getB()){
				noeudDebut.addDebut(noeudFin);
				noeudFin.addFin(noeudDebut);
			}else{
				noeudDebut.addDebut(projBegin.getA());
				if(projBegin.getB()!=projBegin.getA())//arrive quand le point d'un segment le plus proche d'un point est à une des extrémités
					noeudDebut.addDebut(projBegin.getB());
			
				noeudFin.addFin(projEnd.getA());
				if(projEnd.getA()!=projEnd.getB())
					noeudFin.addFin(projEnd.getB());
			}
			
		}
		return computePath1(begin,end,lbegin,lend);
	}*/
	private Line computePath1(Collection<NoeudPourParcourt>begin,Set<NoeudPourParcourt>end){
		Line res=new Line();
		//res.addPoint(begin.iterator().next().pos);
		//res.addPoint(end.iterator().next().pos);
		//for(Collection<Noeud>c:mapDecoupee)
			for(NoeudPourParcourt n:sommetsDuGraphe){
				/*n.parent=null;
				n.fixe=false;*/
				n.reinit();
			}
		List<NoeudPourParcourt> liste=new LinkedList<NoeudPourParcourt>();
		for(NoeudPourParcourt n:begin){
			n.parent=n;
			n.distance=0;
		}
		liste.addAll(begin);
		NoeudPourParcourt n=algoDijkstra(liste,end);
		if(n!=null){
			//res.addPoint(realEnd);
			//TODO ajouter la fin normale
			while(n.parent!=n){
				res.addPoint(n.getPosition());
				n=n.parent;
			}
			res.addPoint(n.getPosition());
			//TODO ajouter le début normal
			//res.addPoint(realBegin);
		}else{
			res.addPoint(begin.iterator().next().getPosition());
			res.addPoint(end.iterator().next().getPosition());
		}
		return res;
	}
	private void configVoisins(List<NoeudPourParcourt> lt,NoeudPourParcourt n){
		for(ArcType<NoeudPourParcourt> a:n.getVoisinsParcourt()){
			NoeudPourParcourt v=a.getNoeud();
			if(!v.fixe&&(a.isArcSurLequelOnPeutMarcher())){
				if(v.parent==null)
					lt.add(v);
				double distance=n.distance+n.distance2(v);
				if(v.parent==null||v.distance>distance){
					v.parent=n;
					v.distance=distance;
				}
			}
		}
	}
	private NoeudPourParcourt plusProche(List<NoeudPourParcourt> l)
	{
		NoeudPourParcourt res=l.iterator().next();
		for(NoeudPourParcourt n:l){
			if(n.distance<res.distance)
				res=n;
		}
		Iterator<NoeudPourParcourt>it=l.iterator();
		while(it.hasNext()){
			if(it.next()==res){
				it.remove();
				return res;
			}
		}
		return null;
	}
	private NoeudPourParcourt algoDijkstra(List<NoeudPourParcourt> lt,Set<NoeudPourParcourt>end){
		while(lt.size()>0){
			NoeudPourParcourt n=plusProche(lt);
			n.fixe=true;
			n.marquer();
			configVoisins(lt,n);
			System.out.println(lt.size()+" "+n);
			if(end.contains(n))
				return n;
		}
		return null;
	}
	//private Collection<Noeud> graphe=new LinkedList<Noeud>();
	
}
