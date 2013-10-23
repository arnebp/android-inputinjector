InputInjector
=============

Android library that eases the process of injecting InputEvents (MotionEvent, KeyEvent) to you Android app. The library uses internal API calls to inject events and depend on the accessability of these. This library will therefore not work on all devices but theoretically support Android 2.3 and forward (API level 9-18+).

###Androd 2.3 (API level 9-15)

In older versions of Android we envoke the same system calls as used by the Instrumentation framework.

####Permission
No special permission needs to be set.

###Androd 4.1.2 (API level 16 and forward)

As of API level 16 we have access to the InputManager class. We use this as the basis for the input injection.

####Permission
Using InputManager for injection requires setting permission *android.permission.INJECT_EVENTS* in your manifest. 

Using this permission may require altering *Lint Error Checking* in order to be able to compile. In Eclipse this is done by going to *Window->Preferences->Android->Lint Error Checking* and then finding *ProtectedPermissions* and setting severity to something else than *error*.

**NOTE**: In order to inject events to other apps using InputManager, your apk must be signed with system level certificate.
