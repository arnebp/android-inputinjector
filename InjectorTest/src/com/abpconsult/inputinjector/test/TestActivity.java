package com.abpconsult.inputinjector.test;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.abpconsult.inputinjector.InjectionManager;

public class TestActivity extends Activity implements OnTouchListener, OnClickListener
{
	private static final String TAG = TestActivity.class.getSimpleName();
	private Broadcaster mBroadCast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_test);
		findViewById(R.id.btn).setOnTouchListener(this);
	}
	
	@Override
	protected void onResume() 
	{
		mBroadCast = new Broadcaster(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
		super.onResume();
	}
	
	@Override
	protected void onPause() 
	{
		mBroadCast.kill();
		super.onPause();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{
		Log.v(TAG, "TOUCH EVENT: "+event.toString());
		return false;
	}

	@Override
	public void onClick(View v) 
	{
		Log.v(TAG, "BTN PRESS EVENT");
	}
	
	private class Broadcaster implements Runnable
	{
		private boolean isBroadcasting;
		private int width,height;
		
		public Broadcaster(int width,int height)
		{
			isBroadcasting = true;
			this.width=width;
			this.height=height;
			new Thread(this).start();
		}
		
		public void kill()
		{
			isBroadcasting = false;
		}
		
		@Override
		public void run()
		{
			InjectionManager im = new InjectionManager(TestActivity.this);
			Random r = new Random();
			while(isBroadcasting)
			{
				try 
				{
					int w = Math.abs(r.nextInt() % width);
					int h = Math.abs(r.nextInt() % height);
					
					int n = r.nextInt() % 10;
					
					im.injectTouchEventDown(w, h);
					im.injectTouchEventRelease(w+n, h+n);
					
					Log.d("TEST", "x: "+w+", "+h);
				
					Thread.sleep(200);
				} 
				catch (InterruptedException ie)
				{
					ie.printStackTrace();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
