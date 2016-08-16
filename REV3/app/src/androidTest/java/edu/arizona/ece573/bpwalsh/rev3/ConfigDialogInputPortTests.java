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
// This test in conjunction with ConfigDialogInputVisibleTests, ConfigDialogInputIpTests, and
// ConfigDialogSequenceTests, verifies requirement B.1.1
//
// Requirement: Upon startup the REV3 application shall immediately enter a configuration screen.
// The configuration screen shall allow the user to enter the IP address or hostname and port of the
// EV3 robot that they wish to connect to. The screen shall also have a connect button that upon
// clicking will establish connectivity to the EV3 robot.
//
// Test Purpose: Verify that the configuration screen will only accept properly formed inputs.
//

public class ConfigDialogInputPortTests {

    @Rule
    public ActivityTestRule<REV3Activity> m_rule = new ActivityTestRule<>(
            REV3Activity.class);


    @Test
    public void validPort1() {
        testValidPort("1024");
    }

    @Test
    public void validPort2() {
        testValidPort("65535");
    }

    @Test
    public void validPort3() {
        testValidPort("10000");
    }

    @Test
    public void validPort4() {
        testValidPort("20000");
    }

    @Test
    public void validPort5() {
        testValidPort("30000");
    }

    @Test
    public void inValidPort1() {
        testMalformedPort("1023");
    }

    @Test
    public void inValidPort2() {
        testMalformedPort("65536");
    }

    @Test
    public void inValidPort3() {
        testMalformedPort("invalid_port_data");
    }

    @Test
    public void inValidPort4() {
        testMalformedPort("0");
    }

    @Test
    public void inValidPort5() {
        testMalformedPort("-1024");
    }

    private void testValidPort(String port) {

        onView(withText("Please Enter IP Address and Port of the EV3 Robot to Connect"))
                .check(matches(isDisplayed()));

        onView(withId(R.id.ev3_port)).perform(replaceText(port));
        onView(withId(android.R.id.button1)).perform(click());

        int port_set = m_rule.getActivity().getConfigurationDialog().getEV3Port();
        assertThat(Integer.toString(port_set), is(port));
    }

    private void testMalformedPort(String port) {

        onView(withText("Please Enter IP Address and Port of the EV3 Robot to Connect"))
                .check(matches(isDisplayed()));

        onView(withId(R.id.ev3_port)).perform(replaceText(port));
        onView(withId(android.R.id.button1)).perform(click());

        int port_set = m_rule.getActivity().getConfigurationDialog().getEV3Port();
        assertThat(Integer.toString(port_set), not(port));
    }
}