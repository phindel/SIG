package fr.univorleans.m2inis.sig;
import java.util.*;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.BitmapFactory.Options;
import android.graphics.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
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

public class MapView extends View implements ILoaderObserver/*implements SurfaceHolder.Callback*/{
		public void setUserLocation(double x,double y)
		{
			userLocation=new Point(x,y);
			postInvalidate();
		}
		private Point userLocation;
        private float mXDpi;
        private float mYDpi;
        private float mMetersToPixelsX;
        private float mMetersToPixelsY;
        //private Bitmap mBitmap;
        private float mXOrigin;
        private float mYOrigin;
        private float mHorizontalBound;
        private float mVerticalBound;
        private Line cheminPropose;
        private boolean ignoreUserChosePoint=false;
/*public void surfaceDestroyed(SurfaceHolder holder) {}
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {}

    public void surfaceCreated(SurfaceHolder holder) {}*/
        public MapView(Context context, AttributeSet attrs) {
        	super(context, attrs);
			// Acquire a reference to the system Location Manager
			
			
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
			
			public boolean onSingleTapConfirmed(MotionEvent event){
				if(center!=null){
				//if((int) event.getX()==userClickX&&(int) event.getY()==userClickY)
				{
					Point ps=fromDeviceToRealPoint(event.getX(),event.getY());
					if(prevPoint_cheminPropose!=null){
						parent.setState("...");
						long prevTime=System.currentTimeMillis();
						cheminPropose=dataSource.computePath(ps,prevPoint_cheminPropose);
						Point prev=null;
						double dist=0;
						for(Point p:cheminPropose.getPoints()){
							if(prev!=null){
								dist+=prev.distance2(p);
							}
							prev=p;
						}
						parent.setState("Calcul: "+(System.currentTimeMillis()-prevTime)+"ms");
						postInvalidate();
						//parent.setState("Dist: "+dist);
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

			//Detects that new pointers are going down.
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
				//TODO faire en sorte que zPoint apparaisse toujours au même endroit sur l'écran (calculer center en fonction)
				//center=new Point(-((zpx-width/2)/mulx+zPoint.getX()),((zpy-height/2)/muly-zPoint.getY()));
				//center=new Point(-(zpx-width/2)/mulx+zPoint.getX(),(zpy-height/2)/muly+zPoint.getY());
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
	/*private void syncZoom(){
		Point min=dataSource.getMinPoint();
		Point max=dataSource.getMaxPoint();
		double dx=max.getX()-min.getX();
		double dy=max.getY()-min.getY();
		double d=Math.min(dx,dy);
		int wh=Math.min(width,height);
		mulx=wh/(d)*zoom;
		muly=wh/(d)*zoom;
		//mulNormalisation=wh/d;
		
		
	}*/
	private void syncZoom(){
		
		double d=Math.min(tailleNormalisationX*2,tailleNormalisationY*2);
		int wh=Math.min(width,height);
		mulx=wh/(d)*zoom;
		muly=wh/(d)*zoom;
	}
	
	//private double mulNormalisation;
	private void preparerScene(){
		
	}
	public void setDataSource(IDataSource src){
		lignes=new ArrayList<float[]>();//sinon java.util.ConcurrentModificationException dans la méthode onDraw
		for(Line l:src.getLines())
			lignes.add(new float[(l.getPoints().size()-1)*4]);
		dataSource=src;
		//center=src.getCenter();
		center=new Point(0,0);//tailleNormalisationX/2,tailleNormalisationX/2);
		pathChemins=new Path();
		
		Point min=dataSource.getMinPoint();
		Point max=dataSource.getMaxPoint();
		double dx=max.getX()-min.getX();
		double dy=max.getY()-min.getY();
		centreReelX=dataSource.getCenter().getX();
		centreReelY=dataSource.getCenter().getY();
    	mulNormalisationX=2*tailleNormalisationX/dx;
    	mulNormalisationY=2*tailleNormalisationY/dy;
		syncZoom();
    	
		drawCheminsPossibles(pathChemins);
    	
		setDataSource_complete=true;
	}
	private double tailleNormalisationX=500;
	private double tailleNormalisationY=500;
	private boolean setDataSource_complete;
	private List<float[]> lignes;
	private IDataSource dataSource;
	int userClickX,userClickY;
	Point prevPoint_cheminPropose;
	@Override
    public boolean onTouchEvent(MotionEvent event){
			
			boolean retVal = scaleGestureDetector.onTouchEvent(event);
			retVal = gestureDetector.onTouchEvent(event) || retVal;
			
			return retVal || super.onTouchEvent(event);
        
        /*
        int action = event.getAction();
        if(dataSource!=null){
        	scaleGestureDetector.onTouchEvent(event);
        }

        if (action == MotionEvent.ACTION_DOWN) {
        	userClickX = (int) event.getX();
            userClickY = (int) event.getY();
            //colPt.add(new Point(x,y));
            return true;

        } else if (action == MotionEvent.ACTION_UP) {
            /*userClickX = (int) event.getX();
            userClickY = (int) event.getY();
            center=toRealPoint(userClickX,userClickY);
            zoom*=2;/
            //center=toRealPoint(userClickX,userClickY);
            return true;
        }

        return false;*/
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
//if(true)throw new RuntimeException();
        DisplayMetrics metrics = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mXDpi = metrics.xdpi;
        mYDpi = metrics.ydpi;
        mMetersToPixelsX = mXDpi / 0.0254f;
        mMetersToPixelsY = mYDpi / 0.0254f;

        // rescale the ball so it's about 0.5 cm on screen
        /*Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        final int dstWidth = (int) (sBallDiameter * mMetersToPixelsX + 0.5f);
        final int dstHeight = (int) (sBallDiameter * mMetersToPixelsY + 0.5f);
        mBitmap = Bitmap.createScaledBitmap(ball, dstWidth, dstHeight, true);*/
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width=w;
        height=h;
    }
	private int width,height;
    private Path pathChemins;
	//private long prevTime;
	/*Transformer de point normalisé vers point device*/
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
		mPaint.setColor(Color.YELLOW);
		canvas.drawCircle(0,0,30,mPaint);
		canvas.drawCircle(-500,-500,10,mPaint);
		canvas.drawCircle(500,-500,10,mPaint);
		canvas.drawCircle(-500,500,10,mPaint);
		canvas.drawCircle(500,500,10,mPaint);
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
         * compute the new position of our object, based on accelerometer
         * data and present time.
         */

        /*final float xc = mXOrigin;
        final float yc = mYOrigin;
        final float xs = mMetersToPixelsX;
        final float ys = mMetersToPixelsY;
        //final Bitmap bitmap = mBitmap;
        final int count = particleSystem.getParticleCount();
        for (int i = 0; i < count; i++) {
            /*
             * We transform the canvas so that the coordinate system matches
             * the sensors coordinate system with the origin in the center
             * of the screen and the unit is the meter.
             * /

            final float x = xc + particleSystem.getPosX(i) * xs;
            final float y = yc - particleSystem.getPosY(i) * ys;
            canvas.drawBitmap(bitmap, x, y, null);
        }*/
        
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
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
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


		mPaint.setStrokeWidth(4);
		//canvas.drawPoint((float)((0)*mulx+width/2),(float)((0)*muly+height/2),mPaint);
		if(pathChemins!=null){
			mPaint.setColor(Color.rgb(255,255,255));
			canvas.drawPath(pathChemins,mPaint);
		}
		
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
		mPaint.setStrokeWidth(6);
		mPaint.setColor(Color.YELLOW);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
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
		
		System.out.println("MapView "+txt);
                
        mPaint.setTextSize(20);
		mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(txt,0,20,mPaint);
        // and make sure to redraw asap
        if(invalider)
        	invalidate();
		parent.checkError();
    }
    private void drawCheminsPossibles(Path pathChemins){
    	Random rand=new Random(0);
    	mPaint.setStrokeWidth(2);
        Iterator<float[]>itt=lignes.iterator();
        
        
		for(Line l:dataSource.getLines()){
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
