package com.csulb.decisionator.decisionator;

import android.support.test.espresso.ViewInteraction;
import android.test.ActivityInstrumentationTestCase2;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Russell on 3/25/2016.
 */
public class UITest extends ActivityInstrumentationTestCase2 {

    public UITest() {
        super(FacebookLogin.class);
    }

    public UITest(Class activityClass) {
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
        ViewInteraction loginButton = onView(withId(R.id.login_button));

        loginButton.perform(click());
    }

}
