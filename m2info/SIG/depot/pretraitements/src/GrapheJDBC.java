import fr.univorleans.m2inis.sig.*;
import java.sql.*;
import java.util.*;
import java.io.*;
import org.postgis.PGgeometry;
import org.postgis.LineString;

public class GrapheJDBC{
	public GrapheJDBC(Connection conn_)throws Exception{
		conn=conn_;
		lireLigne();
		preparerDecoupage();
		Collection<Line> col=lines;
		decouperLigne(col);
		//Collection<Collection<Noeud>>tmp=new LinkedList<LinkedList<Noeud>>();
		creerGraphe();
		
	}
	private double minx=-Double.MAX_VALUE,maxx=Double.MAX_VALUE,miny=-Double.MAX_VALUE,maxy=Double.MAX_VALUE;
	private void preparerDecoupage(){
		int t=2000;//TODO constante magique
		mapDecoupee=new ArrayList<Collection<NoeudPourTraitement> >(t);
		
		coeffDecoup=mapDecoupee.size()/(maxx-minx);
		int i=0;
		while(i<t){
			mapDecoupee.add(i,new ArrayList<NoeudPourTraitement>());
			++i;
		}
	}
	
	private void creerGraphe(){
		
		int ca=0;
		for(Line la:lines){
			//obs.onStateChanged("Generation du graphe "+(((ca*1000.)/col.size())*.1)+"%");
			
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
	public void printLineTo(File out)throws IOException{
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
	private Connection conn;
	private void lireLigne(ResultSet r,int cptRoute)throws Exception{
		PGgeometry geom = (PGgeometry)r.getObject(1); 
		int id = r.getInt(2); 
		//System.out.println("Row " + id + ":");
		//System.out.println(geom.toString());
		String highway=r.getString(3);
		String name=r.getString(4);
		String route=r.getString(5);
		if(name==null)
			name="";
		if(highway!=null||route!=null){//route:bus ou tram
			if(name.equals("")){
				name="route"+cptRoute;
				
			}
			Line l=new Line();
			LineString lineIn=(LineString)geom.getGeometry();
			for( int p = 0; p < lineIn.numPoints(); ++p ) { 
				org.postgis.Point pt = lineIn.getPoint(p);
				//System.out.print(" "+pt.getX()+" "+pt.getY());
				double x=pt.getX();
				double y=pt.getY();
				minx=Math.min(minx,x);
				miny=Math.min(miny,y);
				maxx=Math.max(maxx,x);
				maxy=Math.max(maxy,y);
				l.addPoint(new Point(x,y));
			}
			lines.add(l);
			
			//ps.print("BatimentUniv "+name.replace(" ","_"));
			//printPointsDeLaZone(ps,(Polygon)geom.getGeometry());
			//TODO afficher la liste des services
			//ps.println();
		}
	}
	private void lireLigne()throws Exception{
		//TODO
		
		
		Statement s = conn.createStatement(); 
    ResultSet r = s.executeQuery("select ST_Transform(way,4326),osm_id,highway,name,route from planet_osm_line"); 
    int cptRoute=0;
    
	while( r.next() ) {
		/* 
		* Retrieve the geometry as an object then cast it to the geometry type. 
		* Print things out. 
		*/ 
		
		lireLigne(r,cptRoute);
		++cptRoute;
	}
    s.close(); 
		
		
		
		
		
		
		
	}
	private Collection<Line> lines=new ArrayList<Line>();
}


