package fr.univorleans.m2inis.sig;
import java.util.*;
import java.io.*;

public class ZoneManager{
	private ZoneManager(ILoaderObserver obs,InputStream file)throws IOException{
		BufferedReader br=null;
		long lcpt=0;
		try{
			br=new BufferedReader(new InputStreamReader(file));
			
			Pourcent pc=new Pourcent(file.available());
		
			String ln="";
			int numLigne=0;
			while((ln=br.readLine())!=null){
				readLineAsZone(ln);
				pc.update(lcpt);
				obs.onStateChanged(pc,"ZoneManager "+pc+"");
				lcpt+=ln.length();
				++numLigne;
			}
			
		}finally{
			if(br!=null)
				br.close();
		}
	}
	private void readLineAsZone(String ln){
		Scanner sc=new Scanner(ln);
		String type=sc.next();
		String nom=sc.next();
		List<Point> lPts=new ArrayList<Point>();
		String prec=null;
		String item="";
		while(sc.hasNext()&&!(item=sc.next()).equals("finPoints")){
			if(prec==null)
				prec=item;
			else{
				lPts.add(new Point(Double.parseDouble(prec),Double.parseDouble(item)));
				prec=null;
			}
		}
		if(type.equals("BatimentUniv"))
			readLineAsBatimentUniv(nom,sc,lPts);
		else
			pushZone(type,nom,lPts);
	}
	private void pushZone(String type,String nom,List<Point>lPts){
		Zone z;
		if(type.equals("Parking"))
			z=new Parking(nom,lPts);
		//lac=1,foret=2,autreBatiment
		else{
			int typi;
			if(type.equals("Lac"))
				typi=Zone.lac;
			else if(type.equals("Foret"))
				typi=Zone.foret;
			else if(type.equals("AutreBatiment"))
				typi=Zone.autreBatiment;
			else throw new RuntimeException("Mauvais format pour le fichier de zones");
			z=new Zone(nom,lPts,typi);
		}
		zones.add(z);
	}
	private void readLineAsBatimentUniv(String nom,Scanner sc,List<Point>lPts){
		Collection<Service> services=new ArrayList<Service>();
		//TODO lire sc (et mettre dans services)
		BatimentUniv bat=new BatimentUniv(nom,lPts,services);
		mBatimentUniv.put(nom,bat);
		zones.add(bat);
	}
	public static ZoneManager fromStream(ILoaderObserver obs,InputStream file)throws IOException{
		ZoneManager res=new ZoneManager(obs,file);
		return res;
	}
	public Collection<Zone> getZones(){//pour affichage
		return zones;
	}
	private Collection<Zone> zones=new ArrayList<Zone>();
	private Map<String,BatimentUniv> mBatimentUniv=new HashMap<String,BatimentUniv>();
	public BatimentUniv getBuilding(String nom){
		return mBatimentUniv.get(nom);
	}
	public Collection<String> getBuildingsName(){//pour pouvoir afficher la liste des noms
		return mBatimentUniv.keySet();
	}
	public Collection<Zone> getZoneAt(Point p){
		Collection<Zone> deuxiemePasse=new ArrayList<Zone>();
		double x=p.getX();
		double y=p.getY();
		for(Zone z:zones){
			if(x>=z.getMinPoint().getX()&&y>=z.getMinPoint().getY()&&x<=z.getMaxPoint().getX()&&y<=z.getMaxPoint().getY())
				deuxiemePasse.add(z);
		}
		Collection<Zone> res=deuxiemePasse;//TODO affiner
		return res;
	}
}
