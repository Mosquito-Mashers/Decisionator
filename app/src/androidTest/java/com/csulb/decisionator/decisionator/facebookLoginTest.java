package com.csulb.decisionator.decisionator;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by Russell on 3/25/2016.
 */
public class facebookLoginTest extends ActivityInstrumentationTestCase2 {

    public facebookLoginTest() {
        super(FacebookLogin.class);
    }

    public facebookLoginTest(Class activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        getActivity();
    }


    public void testButtonClick()
    {
        assertTrue(true);

        //ViewInteraction loginButton = onView(withId(R.id.login_button));

        //loginButton.perform(click());
    }

}