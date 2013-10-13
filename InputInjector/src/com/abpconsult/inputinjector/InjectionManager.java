package com.abpconsult.inputinjector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.IWindowManager;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.MotionEvent;

/**
 * InjectionManager is main class for injection InputEvents.
 * <p>
 * The class uses internal APIs to inject. Injection depends on android.permission.INJECT_EVENTS.
 * 
 * @author Anders Bo Pedersen, ABP Consult Aps, 2013
 */
public class InjectionManager
{
	private static final String TAG = InjectionManager.class.getSimpleName();
	
	private static int INJECT_INPUT_EVENT_MODE_ASYNC;
	private static int INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT;
	private static int INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH;
	
	private static final String INTERNAL_SERVICE_PRE_JELLY = "window";
	private IBinder mWmbinder;
	private IWindowManager mWinMan;
	
	private Object mInputManagerInternal;
	private Object mInputManager;
	private Method mInjectEventMethod;
	
	public InjectionManager(Context c)
	{
		if(Integer.valueOf(android.os.Build.VERSION.SDK_INT) < android.os.Build.VERSION_CODES.JELLY_BEAN)
		{
			mWmbinder = ServiceManager.getService( INTERNAL_SERVICE_PRE_JELLY );
			mWinMan = IWindowManager.Stub.asInterface( mWmbinder );
			
			printDeclaredMethods(mWinMan.getClass());
			
			//TODO: Implement full injection support for pre Jelly Bean solutions
		}
		else
		{
			mInputManager = c.getSystemService(Context.INPUT_SERVICE);
			
			try
			{
				//printDeclaredMethods(mInputManager.getClass());
				
				//Unveil hidden methods
				mInjectEventMethod = mInputManager.getClass().getDeclaredMethod("injectInputEvent", new Class[] { InputEvent.class, Integer.TYPE });
				mInjectEventMethod.setAccessible(true);
				Field eventAsync = mInputManager.getClass().getDeclaredField("INJECT_INPUT_EVENT_MODE_ASYNC");
				Field eventResult = mInputManager.getClass().getDeclaredField("INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT");
				Field eventFinish = mInputManager.getClass().getDeclaredField("INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH");
				eventAsync.setAccessible(true);
				eventResult.setAccessible(true);
				eventFinish.setAccessible(true);
				INJECT_INPUT_EVENT_MODE_ASYNC = eventAsync.getInt(mInputManager.getClass());
				INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT = eventResult.getInt(mInputManager.getClass());
				INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH = eventFinish.getInt(mInputManager.getClass());
			}
			catch (NoSuchMethodException nsme)
			{
				Log.e(TAG,  "Critical methods not available");
			}
			catch (NoSuchFieldException nsfe)
			{
				Log.e(TAG,  "Critical fields not available");
			}
			catch (IllegalAccessException iae)
			{
				Log.e(TAG,  "Critical fields not accessable");
			}
		}
	}
	
	public void injectTouchEventDown(int x, int y)
	{
		MotionEvent me = MotionEvent.obtain(
				SystemClock.uptimeMillis(),
				SystemClock.uptimeMillis()+10,
				MotionEvent.ACTION_DOWN,
				x,
				y,
				0
				);
		
		if(Integer.valueOf(android.os.Build.VERSION.SDK_INT) < android.os.Build.VERSION_CODES.JELLY_BEAN)
			;
		else
			me.setSource(InputDevice.SOURCE_TOUCHSCREEN);
		
		injectEvent(me, INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT);
		me.recycle();
	}
	
	public void injectTouchEventRelease(int x, int y)
	{
		MotionEvent me = MotionEvent.obtain(
				SystemClock.uptimeMillis(),
				SystemClock.uptimeMillis()+10,
				MotionEvent.ACTION_UP,
				x,
				y,
				0
				);
		
		if(Integer.valueOf(android.os.Build.VERSION.SDK_INT) < android.os.Build.VERSION_CODES.JELLY_BEAN)
			;
		else
			me.setSource(InputDevice.SOURCE_TOUCHSCREEN);
		
		injectEvent(me, INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT);
		me.recycle();
	}
	
	private void injectEvent(InputEvent ie, int mode)
	{
		try 
		{
			if(Integer.valueOf(android.os.Build.VERSION.SDK_INT) < android.os.Build.VERSION_CODES.JELLY_BEAN)
				;
			else
				mInjectEventMethod.invoke(mInputManager, new Object[] { ie, mode });
		}
		catch (IllegalAccessException iae)
		{
			Log.e(TAG,  "Critical methods not accessable: "+iae.getLocalizedMessage());
		}
		catch (InvocationTargetException ite)
		{
			Log.e(TAG, "Error invoking injection method: "+ite.getLocalizedMessage());
		}
		catch (Exception e)
		{
			Log.e(TAG, "Error using injection method: "+e.getLocalizedMessage());
		}
	}
	
	private void printDeclaredMethods(Class c)
	{
		Method[] methods = c.getDeclaredMethods();
		for(Method m : methods)
		{
			Log.d(TAG, "InputManager method: "+m.getName());
		}
	}
}
