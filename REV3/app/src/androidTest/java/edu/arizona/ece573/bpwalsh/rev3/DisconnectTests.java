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

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
@LargeTest
//
// This test  verifies requirement A.1.2
//
// Requirement: If the REV3 application looses connectivity to the EV3 the application shall
// reinitialize and return to the configuration screen.
//
// Test Purpose: Verify that upon disconnect that the REV3 application executes the “Disconnect”
// sequence (see design document, sequence 5.8.2) and returns to the configuration screen.
//
public class DisconnectTests {

    @Rule
    public ActivityTestRule<REV3Activity> m_rule = new ActivityTestRule<>(
            REV3Activity.class);

    @Test
    public void disconnect_test() {
        m_rule.getActivity().disableSensors();

        onView(withText("Please Enter IP Address and Port of the EV3 Robot to Connect"))
                .check(matches(isDisplayed()));

        onView(withId(R.id.ev3_host)).perform(replaceText("192.168.1.1"));
        onView(withId(R.id.ev3_port)).perform(replaceText("8888"));

        onView(withId(android.R.id.button1)).perform(click());

        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(0.0f));
        onView(withId(R.id.leftButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(20.0f));

        m_rule.getActivity().simulateDisconnect();

        onView(withId(R.id.leftButton)).perform(click());

        //sleep(500);
        onView(withText("Please Enter IP Address and Port of the EV3 Robot to Connect"))
                .check(matches(isDisplayed()));

        onView(withId(R.id.ev3_host)).perform(replaceText("192.168.1.2"));
        onView(withId(R.id.ev3_port)).perform(replaceText("9090"));

        onView(withId(android.R.id.button1)).perform(click());

        assertTrue(m_rule.getActivity().isConnected());

    }
}
