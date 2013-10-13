package android.view;

import android.os.IBinder;
import android.view.KeyEvent;

public interface IWindowManager {
    public static class Stub {
        public static IWindowManager asInterface( IBinder binder ) {
            return null;
        }
    }

    public boolean injectKeyEvent(KeyEvent ev, boolean sync);
    public boolean injectPointerEvent(MotionEvent ev, boolean sync);
    public boolean injectTrackballEvent(MotionEvent ev, boolean sync);
}