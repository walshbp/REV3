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


@RunWith(AndroidJUnit4.class)
@LargeTest
//
// This test verifies requirement A.2.2
//
// Requirement: “From the “drive” screen the user shall be able to adjust the commanded turn
// rate (+100% turn left/-100% turn right) speed via touch screen controls. The touchscreen
// commands will be translated to the proper EV3 protocol message necessary to turn.
//
// Test Purpose: Verify that upon touching left/right touch screen controls that the “Command
// Power Level (GUI)” sequence (see design document sequence 5.2.2) is executed. Verify that
// the bounds (+100%/-100%) are held.
//
public class GuiTurnTests {

    @Rule
    public ActivityTestRule<REV3Activity> m_rule = new ActivityTestRule<>(
            REV3Activity.class);


    @Test
    public void guiTurnTest() {

        m_rule.getActivity().disableSensors();

        onView(withText("Please Enter IP Address and Port of the EV3 Robot to Connect"))
                .check(matches(isDisplayed()));

        onView(withId(R.id.ev3_host)).perform(replaceText("192.168.1.1"));
        onView(withId(R.id.ev3_port)).perform(replaceText("8888"));
        onView(withId(android.R.id.button1)).perform(click());

        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(0.0f));
        onView(withId(R.id.leftButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(20.0f));
        onView(withId(R.id.leftButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(40.0f));
        onView(withId(R.id.leftButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(60.0f));
        onView(withId(R.id.leftButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(80.0f));
        onView(withId(R.id.leftButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(100.0f));
        onView(withId(R.id.leftButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(100.0f));
        onView(withId(R.id.rightButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(80.0f));
        onView(withId(R.id.rightButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(60.0f));
        onView(withId(R.id.rightButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(40.0f));
        onView(withId(R.id.rightButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(20.0f));
        onView(withId(R.id.rightButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(0.0f));
        onView(withId(R.id.rightButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(-20.0f));
        onView(withId(R.id.rightButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(-40.0f));
        onView(withId(R.id.rightButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(-60.0f));
        onView(withId(R.id.rightButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(-80.0f));
        onView(withId(R.id.rightButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(-100.0f));
        onView(withId(R.id.rightButton)).perform(click());
        assertThat(m_rule.getActivity().getTransmittedTurnCommand(), is(-100.0f));
    }
}
