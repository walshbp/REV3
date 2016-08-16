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
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
//
// This test verifies requirement B.2.1 and B.3.1
//
// Requirement (B.2.1): “From the “drive” screen the user shall be able to adjust the commanded
// forward/reverse (+100%/-100%) speed via touch screen controls. The touchscreen commands will be
// translated to the proper EV3 protocol message necessary to accelerate/decelerate.
//
// Requirement (B.3.1): The EV3 protocol shall contain a message to command the speed of the EV3 by
// specifying the percent power level. +100% shall be interpreted as full power forward. -100% shall
// interpreted as full power backwards.
//
// Test Purpose: Verify that upon touching forward/reverse touch screen controls that the “Command
// Power Level (GUI)” sequence (see design document sequence model 5.3.2) is executed. Verify that
// the bounds (+100%/-100%) are held.
//
public class GuiPowerTests {

    @Rule
    public ActivityTestRule<REV3Activity> m_rule = new ActivityTestRule<>(
            REV3Activity.class);


    @Test
    //
    // Is Configuration Dialog Displayed
    //
    public void guiPowerTest() {

        onView(withText("Please Enter IP Address and Port of the EV3 Robot to Connect"))
                .check(matches(isDisplayed()));

        onView(withId(R.id.ev3_host)).perform(replaceText("192.168.1.1"));
        onView(withId(R.id.ev3_port)).perform(replaceText("8888"));
        onView(withId(android.R.id.button1)).perform(click());

        assertThat(m_rule.getActivity().getTransmittedPowerCommand(), is(0.0f));
        onView(withId(R.id.forwardButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedPowerCommand(), is(20.0f));
        onView(withId(R.id.forwardButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedPowerCommand(), is(40.0f));
        onView(withId(R.id.forwardButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedPowerCommand(), is(60.0f));
        onView(withId(R.id.forwardButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedPowerCommand(), is(80.0f));
        onView(withId(R.id.forwardButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedPowerCommand(), is(100.0f));
        onView(withId(R.id.forwardButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedPowerCommand(), is(100.0f));
        onView(withId(R.id.backwardButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedPowerCommand(), is(80.0f));
        onView(withId(R.id.backwardButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedPowerCommand(), is(60.0f));
        onView(withId(R.id.backwardButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedPowerCommand(), is(40.0f));
        onView(withId(R.id.backwardButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedPowerCommand(), is(20.0f));
        onView(withId(R.id.backwardButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedPowerCommand(), is(0.0f));
        onView(withId(R.id.backwardButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedPowerCommand(), is(-20.0f));
        onView(withId(R.id.backwardButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedPowerCommand(), is(-40.0f));
        onView(withId(R.id.backwardButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedPowerCommand(), is(-60.0f));
        onView(withId(R.id.backwardButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedPowerCommand(), is(-80.0f));
        onView(withId(R.id.backwardButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedPowerCommand(), is(-100.0f));
        onView(withId(R.id.backwardButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedPowerCommand(), is(-100.0f));
    }


}
