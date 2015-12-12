import java.sql.*; 
import java.util.*; 
import org.postgis.*; 
import java.io.*;
public class ImporteurJDBC { 

public static void main(String[] args) { 

  java.sql.Connection conn; 
	PrintStream ps=null;
		
			
  try { 
    ps=new PrintStream(new File(args[0]));
    /* 
    * Load the JDBC driver and establish a connection. 
    */
    Class.forName("org.postgresql.Driver"); 
    String url = "jdbc:postgresql://localhost:5432/univ2"; 
    conn = DriverManager.getConnection(url, "postgres", "a"); 
    /* 
    * Add the geometry types to the connection. Note that you 
    * must cast the connection to the pgsql-specific connection 
    * implementation before calling the addDataType() method. 
    */
    ((org.postgresql.PGConnection)conn).addDataType("geometry",Class.forName("org.postgis.PGgeometry"));
    ((org.postgresql.PGConnection)conn).addDataType("box3d",Class.forName("org.postgis.PGbox3d"));

    /* 
    * Create a statement and execute a select query. 
    */ 
    Statement s = conn.createStatement(); 
    ResultSet r = s.executeQuery("select ST_Transform(way,4326),osm_id,building,name,amenity,landuse from planet_osm_polygon"); 
    int cptBatUniv=0;
	while( r.next() ) { 
		/* 
		* Retrieve the geometry as an object then cast it to the geometry type. 
		* Print things out. 
		*/ 
		PGgeometry geom = (PGgeometry)r.getObject(1); 
		int id = r.getInt(2); 
		System.out.println("Row " + id + ":");
		System.out.println(geom.toString());
		String batiment=r.getString(3);
		String nom=r.getString(4);
		String amenity=r.getString(5);
		String landuse=r.getString(6);
		System.out.println(r.getString(4));
		if(batiment==null)
			batiment="";
		if(nom==null)
			nom="";
		if(amenity==null)
			amenity="";
		if(landuse==null)
			landuse="";
		
		if(batiment.equals("university")){
			if(nom.equals("")){
				nom="bat"+cptBatUniv;
				
			}
			++cptBatUniv;
			ps.print("BatimentUniv "+nom.replace(" ","_"));
			printPointsDeLaZone(ps,(Polygon)geom.getGeometry());
			//TODO afficher la liste des services
			ps.println();
		}
		else if(!batiment.equals("")){
			ps.print("AutreBatiment <TODO> ");
			printPointsDeLaZone(ps,(Polygon)geom.getGeometry());
			ps.println();
		}else if(amenity.equals("parking")){
			ps.print("Parking <TODO> ");
			printPointsDeLaZone(ps,(Polygon)geom.getGeometry());
			ps.println();
		}else if(landuse.equals("forest")){
			ps.print("Foret <TODO> ");
			printPointsDeLaZone(ps,(Polygon)geom.getGeometry());
			ps.println();
		}else if(landuse.equals("basin")){
			ps.print("Lac <TODO> ");
			printPointsDeLaZone(ps,(Polygon)geom.getGeometry());
			ps.println();
		}
	} 
    s.close(); 
    conn.close(); 
  } 
catch( Exception e ) { 
  e.printStackTrace(); 
  } 
} 
	private static void printPointsDeLaZone(PrintStream ps,Polygon poly)throws IOException{
		
		LinearRing rng = poly.getRing(0); 
		
		for( int p = 0; p < rng.numPoints(); ++p ) { 
			Point pt = rng.getPoint(p);
			ps.print(" "+pt.getX()+" "+pt.getY());
		}
	}
}
