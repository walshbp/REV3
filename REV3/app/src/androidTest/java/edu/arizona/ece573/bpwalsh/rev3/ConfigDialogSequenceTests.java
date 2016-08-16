//
// REV3 Project
// Copyright 2016, - All Rights Reserved
//
// Team BPWALSH
//
package edu.arizona.ece573.bpwalsh.rev3;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
//
// This test in conjunction with ConfigDialogInputIPTests, ConfigDialogInputPortTests, and
// ConfigDialogVisibleTests, verifies requirement B.1.1
//
// Requirement: Upon startup the REV3 application shall immediately enter a configuration screen.
// The configuration screen shall allow the user to enter the IP address or hostname and port of the
// EV3 robot that they wish to connect to. The screen shall also have a connect button that upon
// clicking will establish connectivity to the EV3 robot.
//
// Test Purpose: Verify that upon clicking the “connect” button that the “Start Up” sequence
// (sequence model 5.1.2 from design document) is executed and that connectivity is established to
// the EV3 robot proxy.
//
public class ConfigDialogSequenceTests {

    @Rule
    public ActivityTestRule<REV3Activity> m_rule = new ActivityTestRule<>(
            REV3Activity.class);


    @Test
    //
    // Is Configuration Dialog Displayed
    //
    public void rev3IsConnected() {
        sleep(500);
        onView(withText("Please Enter IP Address and Port of the EV3 Robot to Connect"))
                .check(matches(isDisplayed()));

        onView(withId(R.id.ev3_host)).perform(replaceText("192.168.1.1"));
        onView(withId(R.id.ev3_port)).perform(replaceText("8888"));
        onView(withId(android.R.id.button1)).perform(click());

        assertTrue(m_rule.getActivity().isConnected());
    }

}