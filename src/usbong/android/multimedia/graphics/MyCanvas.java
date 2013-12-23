package usbong.android.multimedia.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MyCanvas extends View implements OnTouchListener {
	private long mMoveDelay = 600;
	private float lastPosX;
	private float lastPosY;
	private float currPosX;
	private float currPosY;
	
	private Bitmap myBitmap;
	
	public Bitmap getMyBitmap() {
		return myBitmap;
	}
	public void setMyBitmap(Bitmap myBitmap) {
		this.myBitmap = myBitmap;
	}

	private Canvas myCanvas;
	
//	private ShapeDrawable myShapeDrawable;	
//	private Path myPath;
	
	public MyCanvas(Context context) {
		super(context);
		this.setOnTouchListener(this);
		
		lastPosX=-1;//getWidth()/2;
		lastPosY=-1;//getHeight()/2;
		currPosX=-1;
		currPosY=-1;
				
//		myPath = new Path();		
//		myShapeDrawable = new ShapeDrawable(new RectShape());
//		myShapeDrawable.getPaint().setColor(0xFFFFFFFF);//white
//		myShapeDrawable.setBounds(0,0, getWidth(),getHeight());

//		System.out.println("getWidth(): "+getWidth()+"getHeight()"+getHeight());
//		myCanvas = new Canvas();
//		try{
//		  myBitmap = Bitmap.createBitmap(this.getWidth(),this.getHeight(),Bitmap.Config.ARGB_8888);//getWidth(), getHeight(), Bitmap.Config.ARGB_8888);		  
//		  myCanvas = new Canvas(myBitmap);
////		  myCanvas.setBitmap(myBitmap);
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
		//myCanvas = new Canvas();
		//mRedrawHandler.sleep(mMoveDelay);
		update();
	}
	//Reference: http://stackoverflow.com/questions/2423327/android-view-ondraw-always-has-a-clean-canvas
	//Last accessed on: July 2, 2010
	public void onSizeChanged(int w, int h, int oldW, int oldH) {
	    if (myBitmap != null) {
		  myBitmap.recycle();
        }
		try{
		  myBitmap = Bitmap.createBitmap(this.getWidth(),this.getHeight(),Bitmap.Config.ARGB_8888);
		  myCanvas = new Canvas(myBitmap);

		  clearCanvas();
//    	  Paint p = new Paint();
//    	  p.setColor(0xFF6A9D69); //blackboard green
//    	  myCanvas.drawRect(0, 0, myBitmap.getWidth(), myBitmap.getHeight(), p);

		}
		catch(Exception e) {
			e.printStackTrace();
		}	  
	}
	
	public void clearCanvas() {
  	  Paint p = new Paint();
	  p.setColor(0xFF6A9D69); //blackboard green
	  myCanvas.drawRect(0, 0, myBitmap.getWidth(), myBitmap.getHeight(), p);
	}
	
	public void update() {
        long now = System.currentTimeMillis();

        //TODO change this, because "now" is always greater than mMoveDelay)
        if (now  > mMoveDelay) {
        	//do updates
        	System.out.println("DITO");
        }
        mRedrawHandler.sleep(mMoveDelay);
	}
	
	/**
     * Create a simple handler that we can use to cause animation to happen.  We
     * set ourselves as a target and we can use the sleep()
     * function to cause an update/invalidate to occur at a later date.
     */
    private RefreshHandler mRedrawHandler = new RefreshHandler();
    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
        	MyCanvas.this.update();
            MyCanvas.this.invalidate();
        }

        public void sleep(long delayMillis) {
                this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };

	
    protected void onDraw(Canvas c) {
    	//canvas.drawColor(0xFFFFFF); //white
    	Paint p = new Paint();
    	p.setColor(Color.WHITE);
    	myCanvas.drawLine(lastPosX, lastPosY, currPosX, currPosY, p);
    	lastPosX = currPosX;
    	lastPosY = currPosY;

		try{
			c.drawBitmap(myBitmap,
					new Rect(0,0, myBitmap.getWidth(),myBitmap.getHeight()),
					new Rect(0,0, myBitmap.getWidth(),myBitmap.getHeight()),
					p);//0,0,null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
    }

	@Override
	public boolean onTouch(View arg0, MotionEvent me) {
		System.out.println("TOUCHED!");
		int meAction = me.getAction();
		if (lastPosX==-1) {
		  lastPosX = me.getX();
		}
		if (lastPosY==-1) {
		  lastPosY = me.getY();
		}
		switch(meAction) {
			case MotionEvent.ACTION_UP:
				lastPosX = -1;
				lastPosY = -1;
				currPosX = -1;
				currPosY = -1;
				return true;
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				currPosX = me.getX();
				currPosY = me.getY();
				this.invalidate();
				return true;
		}
		return false;
	}	
}

