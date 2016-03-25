package com.csulb.decisionator.decisionator;

import android.test.InstrumentationTestCase;
import android.util.Log;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class asyncTaskTest extends InstrumentationTestCase {
//    private static boolean called;
//
//    protected void setUp() throws Exception {
//        super.setUp();
//        called = false;
//    }
//
//    protected void tearDown() throws Exception {
//        super.tearDown();
//    }
//
//    public final void testSuccessfulFetch() throws Throwable {
//        // create  a signal to let us know when our task is done.
//        final CountDownLatch signal = new CountDownLatch(1);
//
//        // Execute the async task on the UI thread! THIS IS KEY!
//        runTestOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                new getFinalLocation (URL, "", new Listener() {
//                    public void onSuccess(Object response) {
//                        Log.d("Location", "RESTListener: on success callback");
//                        called = true;
//                    }
//                    public void onError(Object response) { }
//                }) {
//                    @Override
//                    protected void onPostExecute(Response response) {
//                        Log.d("Location", "running onPostExecute");
//                        super.onPostExecute(response);
//
//	    	            /* This is the key, normally you would use some type of listener
//	    	             * to notify your activity that the async call was finished.
//	    	             *
//	    	             * In your test method you would subscribe to that and signal
//	    	             * from there instead.
//	    	             */
//                        signal.countDown();
//                    }
//                }.execute();
//            }
//        });
//
//
//	    /* The testing thread will wait here until the UI thread releases it
//	     * above with the countDown() or 30 seconds passes and it times out.
//	     */
//        signal.await(10, TimeUnit.SECONDS);
//        assertTrue(called);
//    }
}
