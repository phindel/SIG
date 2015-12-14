import fr.univorleans.m2inis.sig.*;
import java.io.*;
import java.util.*;
public class GrapheInit{
	
	public GrapheInit(File lignes,File zones)throws IOException{
		lireLignes(lignes);
		lireZones(zones);
		int t=2000;//TODO constante magique
		mapDecoupee=new ArrayList<Collection<NoeudPourTraitement> >(t);
		
		coeffDecoup=mapDecoupee.size()/(maxx-minx);
		int i=0;
		while(i<t){
			mapDecoupee.add(i,new ArrayList<NoeudPourTraitement>());
			++i;
		}
		Collection<Line> col=lines;
		decouperLigne(col);
		//Collection<Collection<Noeud>>tmp=new LinkedList<LinkedList<Noeud>>();
		Pourcent pc=new Pourcent(col.size());
		int ca=0;
		for(Line la:col){
			//obs.onStateChanged("Generation du graphe "+(((ca*1000.)/col.size())*.1)+"%");
			pc.update(ca);
			//obs.onStateChanged(pc,"Generation du graphe "+pc);
			Iterator<Point>ita=la.getPoints().iterator();
			NoeudPourTraitement preca=creerSiInexistant(ita.next());
			if(ita.hasNext()){//TODO: doit toujours arriver, non?
				NoeudPourTraitement pa=creerSiInexistant(ita.next());
				preca.add(pa,1);//TODO
				
				NoeudPourTraitement suiva;
				while(ita.hasNext()){
					suiva=creerSiInexistant(ita.next());
					suiva.add(pa,1);//TODO


					
					preca=pa;
					pa=suiva;
				}
			}
			++ca;
		}
	}
	private Collection<Line> lines=new ArrayList<Line>();
	private void readLine(String ln){
		
		Scanner sc=new Scanner(ln);
		Line l=new Line();
		while(sc.hasNext()){
			
			double x=Double.parseDouble(sc.next());
			if(sc.hasNext()){
				//throw new RuntimeException("AAA Ligne "+lcpt);
			double y=Double.parseDouble(sc.next());
			minx=Math.min(minx,x);
			miny=Math.min(miny,y);
			maxx=Math.max(maxx,x);
			maxy=Math.max(maxy,y);
			if(minx>x)throw new RuntimeException();
			if(miny>y)throw new RuntimeException();
			if(maxx<x)throw new RuntimeException();
			if(maxy<y)throw new RuntimeException();
			//if(sc.hasNext())
			l.addPoint(new Point(x,y));
			}
			
		}
		lines.add(l);
	}
	private double minx,maxx,miny,maxy;
	private Collection<Zone> zones=new ArrayList<Zone>();
	private void lireZones(File lignes)throws IOException{
		BufferedReader br=null;
		try{
			br=new BufferedReader(new FileReader(lignes));
			String ln="";
			while((ln=br.readLine())!=null){
				//readZone(ln);
			}
			
			
			
		}finally{
			if(br!=null)
				br.close();
		}
	}
	private void lireLignes(File lignes)throws IOException{
		
		int lcpt=0;
		BufferedReader br=null;
		try{
			br=new BufferedReader(new FileReader(lignes));
			Pourcent pc=null;//new Pourcent(br.available());
			{
				String ln=br.readLine();
				Scanner tmp=new Scanner(ln);
				double x=Double.parseDouble(tmp.next());
				double y=Double.parseDouble(tmp.next());
				minx=maxx=x;
				miny=maxy=y;
				readLine(ln);
				lcpt+=ln.length();
			}
		
			String ln="";
			while((ln=br.readLine())!=null){
				readLine(ln);
				//pc.update(lcpt);
				//obs.onStateChanged(pc,"Lecture des lignes "+pc+"");
				lcpt+=ln.length();
			}
			ArrayList<Point> l=new ArrayList<Point>();//minx,maxx,miny,maxy
			l.add(new Point(minx,miny));
			l.add(new Point(minx,maxy));
			l.add(new Point((maxx+minx)*.5,maxy));
			l.add(new Point(maxx,miny));
			l.add(new Point(minx,miny));
			//zones.add(new Zone("TODO Enlever!!",l));
			
			/*for(int c=0;c<200;++c){
				double cx=Math.random()*(minx-maxx)+maxx;
				double cy=Math.random()*(miny-maxy)+maxy;
				double dx=1e-4;
				double dy=1e-4;
				l=new ArrayList<Point>();//minx,maxx,miny,maxy
				l.add(new Point(cx-dx,cy-dy));
				l.add(new Point(cx-dx,cy+dy));
				l.add(new Point((cx*2)*.5,cy+dy));
				l.add(new Point(cx+dx,cy-dy));
				l.add(new Point(cx-dx,cy-dy));
				zones.add(new Zone("TODO Enlever!!",l));
			}*/
			
		}finally{
			if(br!=null)
				br.close();
		}
	}
	private void printLineTo(File out)throws IOException{
		PrintStream ps=null;
		try{
			ps=new PrintStream(out);
			ps.println("points du graphe");
			for(Collection<NoeudPourTraitement> c:mapDecoupee)
				for(NoeudPourTraitement n:c)
					ps.println(n.getPosition().getX()+" "+n.getPosition().getY());
			ps.println("graphe");
			for(Collection<NoeudPourTraitement> c:mapDecoupee)
				for(NoeudPourTraitement n:c)
					n.printVoisins(ps);
			
		}finally{
			if(ps!=null)
				ps.close();
		}
	}
	public static void main(String[]args){
		try{
			GrapheInit gi=fromLines(new File(args[0]),new File(args[1]));
			gi.printLineTo(new File(args[2]));
			//gi.printZoneTo(new File(args[3]));
			
		}catch(IOException io){
			System.err.println(io);
		}
	}
	static Comparator<Point> compXCrois=new Comparator<Point>(){
		public int compare(Point o1,Point o2){
			if(o1.getX()==o2.getX())
				return 0;
			else if(o1.getX()<o2.getX())
				return -1;
			else
				return 1;
		}
	};
	static Comparator<Point> compXDeCrois=new Comparator<Point>(){
		public int compare(Point o1,Point o2){
			if(o1.getX()==o2.getX())
				return 0;
			else if(o1.getX()>o2.getX())
				return -1;
			else
				return 1;
		}
	};
	static Comparator<Point> compYCrois=new Comparator<Point>(){
		public int compare(Point o1,Point o2){
			if(o1.getY()==o2.getY())
				return 0;
			else if(o1.getY()<o2.getY())
				return -1;
			else
				return 1;
		}
	};
	static Comparator<Point> compYDeCrois=new Comparator<Point>(){
		public int compare(Point o1,Point o2){
			if(o1.getY()==o2.getY())
				return 0;
			else if(o1.getY()>o2.getY())
				return -1;
			else
				return 1;
		}
	};
	private void decouperLigne(Collection<Line>col){
		int t=130;
		double coeffDecoupX=t/(maxx-minx);
		double coeffDecoupY=t/(maxy-miny);
		ArrayList<ArrayList<Collection<Segment> > > segmentsParRegions=new ArrayList<ArrayList<Collection<Segment> > >(t);//on a des segments qui pouront être dans plusieurs "régions". 1 région: un rectangle de la scène
		int i=0;
		while(i<t){
			int j=0;
			ArrayList<Collection<Segment> > s2=new ArrayList<Collection<Segment> >(t);
			segmentsParRegions.add(i,s2);
			while(j<t){
				Collection<Segment> co=new ArrayList<Segment>();
				s2.add(j,co);
				++j;
			}
			++i;
		}
		Pourcent pc=new Pourcent(col.size()*2);
		int cpt=0;
		for(Line l:col){
			Point prev=null;
			//obs.onStateChanged("Recherche d'intersection "+(((cpt*500.)/col.size())*.1)+"%");
			pc.update(cpt);
			//obs.onStateChanged(pc,"Recherche d'intersection "+pc);
			++cpt;
			for(Point p:l.getPoints()){
				if(prev!=null){
					Segment s=new Segment(prev,p);
					int dx=Math.max(0,(int)Math.floor(coeffDecoupX*(Math.min(p.getX(),prev.getX())-minx))-1);
					int fx=Math.min(t-1,(int)Math.ceil(coeffDecoupX*(Math.max(p.getX(),prev.getX())-minx)));
					int dy=Math.max(0,(int)Math.floor(coeffDecoupY*(Math.min(p.getY(),prev.getY())-miny))-1);
					int fy=Math.min(t-1,(int)Math.ceil(coeffDecoupY*(Math.max(p.getY(),prev.getY())-miny)));
					//s.setRectangleCoord(dx,dy,fx,fy);
					int x=dx;
					while(x<=fx){
						int y=dy;
						while(y<=fy){
							segmentsParRegions.get(x).get(y).add(s);
							++y;
						}
						++x;
					}
				}
				prev=p;
			}
		}
		cpt=0;
		for(Line l:col){
			//obs.onStateChanged("Recherche d'intersection "+(((cpt*500.)/col.size())*.1+50)+"%");
			pc.update(cpt+col.size());
			//obs.onStateChanged(pc,"Recherche d'intersection "+pc);
			++cpt;
			Point prev=null;
			ListIterator<Point> it=l.getPoints().listIterator();
			while(it.hasNext()){
				Point p=it.next();
				if(prev!=null){
					//copie depuis le for précédent
					int dx=Math.max(0,(int)Math.floor(coeffDecoupX*(Math.min(p.getX(),prev.getX())-minx))-1);
					int fx=Math.min(t-1,(int)Math.ceil(coeffDecoupX*(Math.max(p.getX(),prev.getX())-minx)));
					int dy=Math.max(0,(int)Math.floor(coeffDecoupY*(Math.min(p.getY(),prev.getY())-miny))-1);
					int fy=Math.min(t-1,(int)Math.ceil(coeffDecoupY*(Math.max(p.getY(),prev.getY())-miny)));
					List<Point>nouvPoints=new ArrayList<Point>();
					Set<Segment>segmentsFaits=new HashSet<Segment>();
					int x=dx;
					while(x<=fx){
						int y=dy;
						while(y<=fy){
							for(Segment s:segmentsParRegions.get(x).get(y)){
								if((prev!=s.getA()||p!=s.getB())&&!segmentsFaits.contains(s)){
									Point inter=s.intersecte(prev,p);
									if(inter!=null){
										//inter.marquer();
										nouvPoints.add(inter);
									}
									segmentsFaits.add(s);
								}
							}
							++y;
						}
						++x;
					}
					Comparator<Point> comp;
					if(p.getX()>prev.getX())
						comp=compXCrois;
					else
						if(p.getX()>prev.getX())comp=compXDeCrois;
					else if(p.getY()>prev.getY())
						comp=compYCrois;
					else
						comp=compYDeCrois;
						
					Collections.sort(nouvPoints,comp);
					
					if(nouvPoints.size()>0){
						//if(true)throw new RuntimeException();
						nouvPoints.add(p);
						it.remove();//on met p après le contenu de nouvPoints
						Point prec=null;
						for(Point np:nouvPoints){
							if(prec==null||!prec.equals(p))
								it.add(np);
						}
						//it.add(p);
					}
				}
				prev=p;
			}
		}
	}
	private double coeffDecoup;
	private ArrayList<Collection<NoeudPourTraitement> > mapDecoupee;
	private NoeudPourTraitement creerSiInexistant(Point pos){
		Collection<NoeudPourTraitement> col=mapDecoupee.get((int)Math.floor(coeffDecoup*(pos.getX()-minx)));
		for(NoeudPourTraitement n:col){
			if(n.getPosition().distance2(pos)<3e-5)
				return n;
		}
		NoeudPourTraitement n=new NoeudPourTraitement(pos,col.size());
		col.add(n);
		return n; 
	}
	public static GrapheInit fromLines(File lignes,File zones)throws IOException{
		
		GrapheInit g=new GrapheInit(lignes,zones);
		
		
		return g;
	}
}
