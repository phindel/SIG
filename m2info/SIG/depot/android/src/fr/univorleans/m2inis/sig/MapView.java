package fr.univorleans.m2inis.sig;
import java.util.*;
import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.AttributeSet;
import android.view.ScaleGestureDetector;
import android.view.GestureDetector;
//postInvalidateOnAnimation ou postInvalidate?
/*
Classe pour dessiner des cartes à partir d'une source de données
Il est possible de se déplacer dans la carte on de zoomer à la mode des écrans tactiles (en bougeant ses doigts sur l'écran)
Les coordonnées sont en longitude/lattitude et sont transformés en coordonnées normalisées (pour éviter les pertes dues au manque de précision des float). On transforme ensuite vers les coordonnées matériel (device) via les méthodes translate et scale de la classe Canvas.
Les chemins de la carte sont précalculés une fois pour toutes dans un Path (méthode drawCheminsPossibles).
Puisqu'un Path travaille sur des float, il faut utiliser des coordonnées intermédiaire (Normalise) pour éviter de ne dessiner qu'un inutile X au lieu d'une véritable carte.
Services non-événementiels (méthodes publiques):
	afficher une trajectoire: setItineraire
	afficher la position actuelle de l'utilisateur: setUserLocation
	passer en mode choix de points (l'utilisateur pourra choisir un point dans la scène): setZoneSelection
	selectionner une Zone: selectionnerZone
	s'enregistrer en tant que OnPointSelectedListener
*/
public class MapView extends View implements ILoaderObserver/*implements SurfaceHolder.Callback*/{
		public void setUserLocation(double x,double y)
		{
			userLocation=new Point(x,y);
			postInvalidate();
		}
		private Point userLocation;

        private Line cheminPropose;
        public void setItineraire(Line ch){
        	cheminPropose=ch;
        	postInvalidate();
        }
        private boolean ignoreUserChosePoint=false;
        private int []couleurTypeLigne;
        public MapView(Context context, AttributeSet attrs) {
        	super(context, attrs);
			
			
			couleurTypeLigne=new int[]{Color.WHITE,Color.YELLOW,Color.BLUE,Color.MAGENTA};
			
			
			gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, 
			float distanceX, float distanceY) {
				if(center==null)
					return true;
				//Point d=toRealPoint(distanceX,distanceY);
				center=new Point(center.getX()+distanceX,center.getY()-distanceY);
				postInvalidate();
				return true;
			}
			/*Quand l'utilisateur tape sur l'écran*/
			public boolean onSingleTapConfirmed(MotionEvent event){
				if(center!=null){
				//if((int) event.getX()==userClickX&&(int) event.getY()==userClickY)
				{
					Point ps=fromDeviceToRealPoint(event.getX(),event.getY());
					/*selectionnerNoeud=dataSource.selectionnerNoeud(ps);
					Iterator<NoeudPourParcourt> itSel=selectionnerNoeud.iterator();
					selectionnerNoeud_p=itSel.next().getPosition();
					itSel.remove();postInvalidate();*/
					if(zoneSelection){
						//TODO
						//onZoneSelectedListener.onZoneSelected();
					}else{
						onPointSelectedListener.onPointSelected(ps);
					}
					
					
					
					prevPoint_cheminPropose=ps;
				}
				if(true){
					userClickX = (int) event.getX();
					userClickY = (int) event.getY();
				}
				
			}return true;
			}
			});
			scaleGestureDetector=new ScaleGestureDetector(context,new ScaleGestureDetector.SimpleOnScaleGestureListener(){

			.
			@Override
			public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
				if(center==null)
					return false;
				lastSpanX = scaleGestureDetector.getCurrentSpanX();
				lastSpanY = scaleGestureDetector.getCurrentSpanY();
				prevZoom=zoom;
				zpx=scaleGestureDetector.getFocusX();
				zpy=scaleGestureDetector.getFocusY();
				zPoint=fromDevicetoNormalisedPoint(zpx,zpy);
				parent.setState("Scale");
				//ignoreUserChosePoint=true;
				return true;
			}
			private float zpx,zpy;
			private Point zPoint;
			private float prevZoom;
			@Override
			public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
				if(center==null)
					return false;
				
				/*if(!scaleGestureDetector.isInProgress())
					return true;*/
				float spanX=scaleGestureDetector.getCurrentSpanX();
				float spanY=scaleGestureDetector.getCurrentSpanY();
				/*if(spanX+spanY>lastSpanX+lastSpanY)
					zoom=Math.min(zoom*1.4f,1e2f);
				else zoom=Math.max(zoom/1.4f,5e-1f);*/
				float rapport=(spanX+spanY)/(lastSpanX+lastSpanY);
				if(rapport>1f)
					zoom=Math.min(prevZoom*rapport,1e1f);
				else //if(rapport<1f/1.2f)
					zoom=Math.max(prevZoom*rapport,5e-1f);
				//else
				//	zoom=prevZoom;
				System.err.println("OOOO onScale "+zoom);
				
				syncZoom();
				
				//
				//
				
				//on fait en sorte que zPoint apparaisse toujours au même endroit sur l'écran
				center=new Point(0,0);
				Point dev=fromNormalisedToDevicePoint(zPoint);
				center=new Point(dev.getX()-zpx,zpy-dev.getY());
				/*lastSpanX=spanX;
				lastSpanY=spanY;*/
				postInvalidate();
				return true;
			}
			@Override
			public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
				//center=toRealPoint(scaleGestureDetector.getFocusX(),scaleGestureDetector.getFocusY());
				
				/*if(spanX>lastSpanX)
				zoom*=1.4f;
				else zoom/=1.4f;*/
				parent.setState("");
				//ignoreUserChosePoint=false;
			}
			}) ;
			
        }
	/*
	Synchronise mulx et muly
	*/
	private void syncZoom(){
		
		double d=Math.min(tailleNormalisationX*2,tailleNormalisationY*2);
		int wh=Math.min(width,height);
		mulx=wh/(d)*zoom;
		muly=wh/(d)*zoom;
	}
	
	//private double mulNormalisation;
	private void preparerScene(){
		
	}
	private Collection<NoeudPourParcourt> selectionnerNoeud;
	private Point selectionnerNoeud_p;
	/*Initialise la carte avec les données*/
	public void setDataSource(IDataSource src){
		
		
		dataSource=src;
		//center=src.getCenter();
		center=new Point(0,0);//tailleNormalisationX/2,tailleNormalisationX/2);
		
		
		Point min=dataSource.getMinPoint();
		Point max=dataSource.getMaxPoint();
		double dx=max.getX()-min.getX();
		double dy=max.getY()-min.getY();
		centreReelX=dataSource.getCenter().getX();
		centreReelY=dataSource.getCenter().getY();
    	mulNormalisationX=2*tailleNormalisationX/dx;
    	mulNormalisationY=2*tailleNormalisationY/dy;
		syncZoom();
    	pathChemins=new Path[src.getLineTypeNumber()];
    	for(int type=0;type<src.getLineTypeNumber();++type){
			pathChemins[type]=new Path();
			drawCheminsPossibles(pathChemins[type],type);
    	}
    	pathPoints=new Path();
    	for(NoeudPourParcourt n:dataSource.getSommetsDuGraphe()){//pour que ça soit plus joli, on dessine des disques à l'emplacement des noeuds
        	Point p=n.getPosition();
        	pathPoints.addCircle(toPointNormaliseX(p),toPointNormaliseY(p),2,Path.Direction.CW);
        }
		setDataSource_complete=true;
	}
	private double tailleNormalisationX=500;
	private double tailleNormalisationY=500;
	private boolean setDataSource_complete;
	private IDataSource dataSource;
	int userClickX,userClickY;
	Point prevPoint_cheminPropose;
	@Override
    public boolean onTouchEvent(MotionEvent event){
			
			boolean retVal = scaleGestureDetector.onTouchEvent(event);
			retVal = gestureDetector.onTouchEvent(event) || retVal;
			
			return retVal || super.onTouchEvent(event);
        
        
        

        
    }
	private float lastSpanX;
	private float lastSpanY;
    ScaleGestureDetector scaleGestureDetector;
    GestureDetector gestureDetector;
    private MainActivity parent;
	public void setParent(MainActivity ma){
		parent=ma;
	}
	//private Collection<Point> colPt=new LinkedList<Point>();
        
	public void onStateChanged(Pourcent pc,String st){
		loaderState=st;
		pcLoaderState=pc;
	}
	private Pourcent pcLoaderState;
	private String loaderState;
    public MapView(Context context) {
        super(context);
        
        //
        

        
        
        
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width=w;
        height=h;
    }
	private int width,height;
    private Path[] pathChemins;
    private Path pathPoints;
	//private long prevTime;
	/*Transformer la scène de point normalisé vers point device
	Les transformations sur la scène sont dans l'ordre inverse de ce qui est écrit dans le code*/
	private void transformer(Canvas canvas){
		canvas.translate((float)-center.getX(),(float)center.getY());//on déplace le carré selon l'utilisateur
		if(height<width)//centrer le carré horizontalement
			canvas.translate((width-height)/2,0);
		else{//centrer le carré verticalement
			canvas.translate(0,(height-width)/2);
		}
		canvas.scale((float)mulx,(float)-muly);//on met dans le carré (0,0)->(c,c) où c=zoom*min(height,width)
		canvas.translate((float)(tailleNormalisationX),(float)(-tailleNormalisationY));//on met entre 0 et tailleNormalisationX*2, -tailleNormalisationY*2 et 0
		//canvas.translate((float)(width/2),(float)(height/2));
		//canvas.scale((float)mulx*1e-4f,(float)muly*1e-4f);
		/*mPaint.setColor(Color.YELLOW);
		canvas.drawCircle(0,0,30,mPaint);
		canvas.drawCircle(-500,-500,10,mPaint);
		canvas.drawCircle(500,-500,10,mPaint);
		canvas.drawCircle(-500,500,10,mPaint);
		canvas.drawCircle(500,500,10,mPaint);*/
	}
	private Point fromDevicetoNormalisedPoint(float x_,float y_){
    	double x=x_;
    	double y=y_;
    	x-=-center.getX();
    	y-=center.getY();
    	if(height<width)//centrer le carré horizontalement
			x-=(width-height)/2;
		else{//centrer le carré verticalement
			y-=(height-width)/2;
		}
    	x/=mulx;
    	y/=-muly;
    	
    	x-=tailleNormalisationX;
    	y-=-tailleNormalisationY;
    	System.out.println("fromDevicetoNormalisedPoint "+x+" "+y);
    	return new Point(x,y);
    	//return toRealPoint(x,y);//TODO pas bon
    }
    public void setOnZoneSelectedListener(OnZoneSelectedListener onZoneSelectedListener_){
    	onZoneSelectedListener=onZoneSelectedListener_;
    }
    private OnZoneSelectedListener onZoneSelectedListener;
    public void setOnPointSelectedListener(OnPointSelectedListener onPointSelectedListener_){
    	onPointSelectedListener=onPointSelectedListener_;
    }
    private OnPointSelectedListener onPointSelectedListener;
    public void setZoneSelection(boolean zoneSelection_){
    	zoneSelection=zoneSelection_;
    }
    /*Fait en sorte que le point real soit en dev sur l'écran*/
    private void moveRealPointToDevicePoint(Point real,Point dev){
    	Point norm=new Point(toPointNormaliseX(real),toPointNormaliseY(real));
    	Point centerPrec=center;
    	center=new Point(0,0);
    	//on veut fromNormalisedToDevicePoint(norm)=dev
    	Point dec=fromNormalisedToDevicePoint(norm);
    	
    	center=new Point(dec.getX()-dev.getX(),dev.getY()-dec.getY());
    	
    	//on ne fait que X ou Y
    	if(dev.getX()==0)//valeur spéciale; pas de décallage en X
    		center=new Point(centerPrec.getX(),center.getY());
    	if(dev.getY()==0)//valeur spéciale; pas de décallage en Y
    		center=new Point(center.getX(),centerPrec.getY());
    }
    private boolean zoneSelection=true;
    private static final double marge=12;
    public void selectionnerZone(Zone z){
    	zoneSelectionnee=z;
    	if(fromRealToDevicePoint(z.getMinPoint()).getX()<marge)
    		moveRealPointToDevicePoint(z.getMinPoint(),new Point(marge,0));//center=new Point(center.getX()-100,center.getY());
    	else if(fromRealToDevicePoint(z.getMaxPoint()).getX()>width-marge)
    		moveRealPointToDevicePoint(z.getMaxPoint(),new Point(width-marge,0));//center=new Point(center.getX()+100,center.getY());
    	if(fromRealToDevicePoint(z.getMaxPoint()).getY()<marge)
    		moveRealPointToDevicePoint(z.getMaxPoint(),new Point(0,marge));//center=new Point(center.getX(),center.getY()+100);
    	else if(fromRealToDevicePoint(z.getMinPoint()).getY()>height-marge)
    		moveRealPointToDevicePoint(z.getMinPoint(),new Point(0,height-marge));//center=new Point(center.getX(),center.getY()-100);
    	postInvalidate();
    }
    private Zone zoneSelectionnee;
    private Point fromRealToDevicePoint(Point real){
    	return fromNormalisedToDevicePoint(new Point(toPointNormaliseX(real),toPointNormaliseY(real)));
    }
    private Point fromNormalisedToDevicePoint(Point nor){
    	double x=nor.getX();
    	double y=nor.getY();
    	x+=tailleNormalisationX;
    	y+=-tailleNormalisationY;
    	x*=mulx;
    	y*=-muly;
    	
    	
    	if(height<width)//centrer le carré horizontalement
			x+=(width-height)/2;
		else{//centrer le carré verticalement
			y+=(height-width)/2;
		}
    	
    	x+=-center.getX();
    	y+=center.getY();
    	
    	System.out.println("fromNormalisedToDevicePoint "+x+" "+y);
    	return new Point(x,y);
    	//return toRealPoint(x,y);//TODO pas bon
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
		long prevTime=System.currentTimeMillis();
		boolean invalider=false;
		//Matrix matrixOriginale=canvas.getMatrix();
		canvas.save();//sauvegarde la matrice de transformation
		
        /*

         */


        
        
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
		//canvas.drawLine(0,100,200,300,mPaint);
		
		mPaint.setTextSize(20);
		mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
		if(setDataSource_complete){
		//return (float)((p.getX()-center.getX())*mulx+width/2);
		//return (float)((center.getY()-p.getY())*muly+height/2);
		transformer(canvas);
		
		
		//------------- dessin des zones -------------
        Path path=new Path();
		mPaint.setStyle(Paint.Style.FILL);
		for(Zone z:dataSource.getZones()){
			path.reset();
			mPaint.setColor(z.getColor());
			List<Point> lim=z.getLimite();
			path.moveTo(toPointNormaliseX(lim.get(0)),toPointNormaliseY(lim.get(0)));
			for(Point p:lim)
			{
				path.lineTo(toPointNormaliseX(p),toPointNormaliseY(p));
			}
			canvas.drawPath(path,mPaint);
		}
        mPaint.setStyle(Paint.Style.STROKE);
		/*mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeJoin(Paint.Join.ROUND);*/
        mPaint.setStrokeWidth(2);
		mPaint.setColor(Color.YELLOW);
		/*for(Point p:colPt){
			mPaint.setColor(Color.YELLOW);
			canvas.drawPoint(p.x,p.y,mPaint);
		}*/
		Point min=dataSource.getMinPoint();
		Point max=dataSource.getMaxPoint();
		//canvas.drawText("min:"+min+" max:"+max, 300, 200, mPaint);
		/*mulx=width/(max.getX()-min.getX())*zoom;
		muly=height/(max.getY()-min.getY())*zoom;*/
		int c=0;

		//------------- dessin des chemins (carte) -------------
		mPaint.setStrokeWidth(4);
		//canvas.drawPoint((float)((0)*mulx+width/2),(float)((0)*muly+height/2),mPaint);
		if(pathChemins!=null){
			
			for(int type=0;type<dataSource.getLineTypeNumber();++type){
				mPaint.setColor(couleurTypeLigne[type]);
				canvas.drawPath(pathChemins[type],mPaint);
				
			}
			mPaint.setColor(Color.WHITE);
			mPaint.setStrokeWidth(1);
			mPaint.setStyle(Paint.Style.FILL);
			canvas.drawPath(pathPoints,mPaint);
		}
		
		//------------- dessin du chemin proposé à l'utilisateur -------------
		mPaint.setStyle(Paint.Style.STROKE);
		if(cheminPropose!=null){
			path.reset();
			//mPaint.setColor(Color.rgb(255,255,255));
			mPaint.setColor(Color.YELLOW);
			mPaint.setStrokeWidth(10);
			Point prev=null;
			for(Point p:cheminPropose.getPoints()){
				if(prev!=null)
					//canvas.drawLine(toDevicePointx(prev),toDevicePointy(prev),toDevicePointx(p),toDevicePointy(p),mPaint);
					path.lineTo(toPointNormaliseX(p),toPointNormaliseY(p));
				else
					path.moveTo(toPointNormaliseX(p),toPointNormaliseY(p));
				
				prev=p;
			}
			canvas.drawPath(path,mPaint);
		}
		if(selectionnerNoeud!=null){//debug
			path.reset();
			mPaint.setColor(Color.RED);
			mPaint.setStrokeWidth(3);
			Point prev=null;
			for(NoeudPourParcourt p:selectionnerNoeud){
				path.moveTo(toPointNormaliseX(selectionnerNoeud_p),toPointNormaliseY(selectionnerNoeud_p));
				path.lineTo(toPointNormaliseX(p.getPosition()),toPointNormaliseY(p.getPosition()));
				
			}
			canvas.drawPath(path,mPaint);
		}
		
		//------------- affichage de la zone sélectionnée -------------
		if(zoneSelectionnee!=null){
			
			mPaint.setColor(Color.RED);
			mPaint.setStrokeWidth(3);
			canvas.drawRect(toPointNormaliseX(zoneSelectionnee.getMinPoint())-10,toPointNormaliseY(zoneSelectionnee.getMinPoint())-10,toPointNormaliseX(zoneSelectionnee.getMaxPoint())+10,toPointNormaliseY(zoneSelectionnee.getMaxPoint())+10,mPaint);
		}
		mPaint.setStrokeWidth(6);
		mPaint.setColor(Color.YELLOW);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		//------------- dessin de la position de l'utilisateur -------------
		if(userLocation!=null){
			mPaint.setColor(Color.RED);
			canvas.drawCircle(toPointNormaliseX(userLocation),toPointNormaliseY(userLocation),8,mPaint);
			mPaint.setColor(Color.YELLOW);
			canvas.drawCircle(toPointNormaliseX(userLocation),toPointNormaliseY(userLocation),3,mPaint);
		}
		
		double reelUserClickX=(userClickX-width/2)/mulx+center.getX();//userClickX
		double reelUserClickY=-((userClickY-height/2)/muly-center.getY());//userClickY
		mPaint.setColor(Color.WHITE);
		//canvas.drawText("Position réelle: "+userLocation, 500, 400, mPaint);
		//canvas.drawText("center:"+center+" lignes:"+dataSource.getLines().size(), 100, 100, mPaint);

		}else{
			/*canvas.translate(100,100);
			canvas.scale(2,2);
			canvas.translate(00,80);*/
			mPaint.setTextSize(20);
			mPaint.setColor(Color.RED);
		    mPaint.setStyle(Paint.Style.FILL);
		    String str;
		    if(loaderState==null)
		    	str="Chargement...";
		    else
		    	str=loaderState;
		    canvas.drawText(str,0,120,mPaint);
		    int x,dx;
		    int y,dy;
		    x=width/3;
		    dx=width/3;
		    y=150;
		    dy=20;
		    mPaint.setStrokeWidth(dy+10);
		    mPaint.setColor(Color.RED);
		    canvas.drawLine(x,y-1,x+dx,y,mPaint);
		    mPaint.setStrokeWidth(dy);
		    mPaint.setColor(Color.WHITE);
		    if(pcLoaderState!=null)
		    	canvas.drawLine(x,y,x+(float)(dx*pcLoaderState.getValue()),y,mPaint);
		    invalider=true;
		}
		//canvas.setMatrix(matrixOriginale);
		canvas.restore();
		//canvas.translate(100,140);
		long time=System.currentTimeMillis();
		String txt="";
		
			txt=(time-prevTime)+" ms";
		if(setDataSource_complete)
			System.out.println("MapView "+txt+"  centre (coord réelles):"+fromDeviceToRealPoint(0*.5f,0*.5f));
                
        mPaint.setTextSize(20);
		mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(txt,0,20,mPaint);
        // and make sure to redraw asap
        if(invalider)
        	invalidate();
		parent.checkError();
    }
    /*Initialise le Path des différents type de chemin pour usage ultérieur (améliore les performances)*/
    private void drawCheminsPossibles(Path pathChemins,int type){
    	Random rand=new Random(0);
    	mPaint.setStrokeWidth(2);
        
        
        
		for(Line l:dataSource.getLines(type)){
			mPaint.setColor(Color.rgb(rand.nextInt(155)+100,rand.nextInt(255),rand.nextInt(255)));
			Point prev=null;
			//float[]tb=itt.next();
			int tbc=0;
			for(Point p:l.getPoints()){
				if(prev!=null){
					//canvas.drawLine(toDevicePointx(prev),toDevicePointy(prev),toDevicePointx(p),toDevicePointy(p),mPaint);
					/*tb[tbc]=toDevicePointx(prev);
					tb[tbc+1]=toDevicePointy(prev);
					tb[tbc+2]=toDevicePointx(p);
					tb[tbc+3]=toDevicePointy(p);
					tbc+=4;*/
					pathChemins.lineTo(toPointNormaliseX(p),toPointNormaliseY(p));
				}else{
					pathChemins.moveTo(toPointNormaliseX(p),toPointNormaliseY(p));
				}
				
				
				int m=1;
				if(p.isMarque())
					m=2;
				mPaint.setStrokeWidth(4*m*zoom);
				if(p.isMarque()){
					mPaint.setStrokeWidth(4*m*zoom);
					//canvas.drawPoint(toDevicePointx(p),toDevicePointy(p),mPaint);
				}
				mPaint.setStrokeWidth(2);
				prev=p;
				//canvas.drawPoint((float)((p.getX()-center.getX())*mulx+width/2),(float)((p.getY()-center.getY())*muly+height/2),mPaint);
				
				//canvas.drawText(((p.getX()-center.getX())*mulx+200f)+","+((p.getY()-center.getY())*muly+200f), 20, 120+20*c, mPaint);++c;
			}
			//canvas.drawLines(tb,mPaint);
			
		}
		
    }
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float zoom=1;
    private double mulx,muly;
    private Point center;
    /*private float toDevicePointx(Point p){
    	//return (float)((p.getX()));
    	//return (float)((p.getX()-center.getX())*mulx+width/2);
    	return (float)((p.getX()-center.getX())*mulNormalisation);
    }
    private float toDevicePointy(Point p){
    	//return (float)((-p.getY()));
    	return (float)((center.getY()-p.getY())*mulNormalisation);
    	//return (float)((center.getY()-p.getY())*muly+height/2);
    }*/
    private float toPointNormaliseX(Point p){
    	return (float)((p.getX()-centreReelX)*mulNormalisationX);
    }
    private float toPointNormaliseY(Point p){
    	float res=(float)((p.getY()-centreReelY)*mulNormalisationY);
    	//if(res<-501||res>501)throw new RuntimeException("normalisation mauvaise");
    	return res;
    }
    private double centreReelX,centreReelY;
    private double mulNormalisationX,mulNormalisationY;
    private Point fromDeviceToRealPoint(float x,float y){
    	Point p=fromDevicetoNormalisedPoint(x,y);
    	return new Point(p.getX()/mulNormalisationX+centreReelX,p.getY()/mulNormalisationY+centreReelY);
    }
    /*private Point toRealPoint(float x,float y){
    	return new Point((x-width/2)/mulx+center.getX(),-((y-height/2)/muly-center.getY()));
    	//
    }*/
}
