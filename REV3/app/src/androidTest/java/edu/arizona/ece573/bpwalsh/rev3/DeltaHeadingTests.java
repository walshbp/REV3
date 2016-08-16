//
// REV3 Project
// Copyright 2016, - All Rights Reserved
//
// Team BPWALSH
//
//
package edu.arizona.ece573.bpwalsh.rev3;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.widget.ImageView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.os.SystemClock.sleep;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
@LargeTest

//
// This test verifies requirement A.2.3 and A.3.3
//
// Requirement (A.2.3): The REV3 application shall be capable of receiving the change in heading (since
// configuration time) over the EV3 protocol. The application shall display the change in heading
// via an indicator arrow on the “drive” screen.
//
// Requirement (A.3.3): The EV3 protocol shall contain a message to receive the delta change in
// heading (since configuration) from the EV3. The delta change in heading shall be expressed in
// degrees.
//
// Test Purpose: Verify that the REV3 application can receive commands from the EV3 Proxy and that
// the “Display Delta Heading” sequence is executed.
//
public class DeltaHeadingTests {

    @Rule
    public ActivityTestRule<REV3Activity> m_rule = new ActivityTestRule<>(
            REV3Activity.class);


    @Test
    //
    // Delta heading test
    //
    public void deltaHeadingTest() {
        onView(withText("Please Enter IP Address and Port of the EV3 Robot to Connect"))
                .check(matches(isDisplayed()));

        onView(withId(R.id.ev3_host)).perform(replaceText("192.168.1.1"));
        onView(withId(R.id.ev3_port)).perform(replaceText("8888"));
        onView(withId(android.R.id.button1)).perform(click());

        int maxcount = 50;
        int count = 0;

        while (count < maxcount)
        {
            sleep(10);
            ProtocolProcessor.ReceivedMsg ret = m_rule.getActivity().getReceivedDeltaHeading();
            if (ret != null && ret.isNew())
            {
                ImageView image = (ImageView) m_rule.getActivity().findViewById(R.id.turnIndicator);
                double achieved_rotation = image.getRotation();
                Log.v("DeltaHeading", "reported: " + ret.getValue() + " achieved: " + achieved_rotation);
                assertEquals(ret.getValue(), achieved_rotation, 0.00001);
                count++;
            }

        }

    }

}