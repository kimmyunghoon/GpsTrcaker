package com.example.gpsinfo;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MagnetView extends SurfaceView implements Runnable, SurfaceHolder.Callback{
    Thread mThread;
    SurfaceHolder mSurfaceHolder;
    volatile boolean running = false;

    //Creates new surface view as well as a new surfaceholder, which allows access to the surface
    public MagnetView (Context context){
        super(context);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        //this.setZOrderOnTop(true);
        //getHolder().setFormat(PixelFormat.TRANSLUCENT);


    }

    public void resume(){
        running = true;
        mThread = new Thread(this);
        mThread.start();
    }

    public void pause(){
        boolean retry = true;
        running = false;
        while(retry){
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while(running){
            if(mSurfaceHolder.getSurface().isValid()){
                Canvas canvas = mSurfaceHolder.lockCanvas();
                //... actual drawing on canvas

                canvas.drawARGB(100, 255, 255, 80);

                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        this.resume();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

}