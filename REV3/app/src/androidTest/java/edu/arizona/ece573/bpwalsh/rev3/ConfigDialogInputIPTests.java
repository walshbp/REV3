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
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
//
// This test in conjunction with ConfigDialogInputVisibleTests, ConfigDialogInputPortTests, and
// ConfigDialogSequenceTests, verifies requirement B.1.1
//
// Requirement: Upon startup the REV3 application shall immediately enter a configuration screen.
// The configuration screen shall allow the user to enter the IP address or hostname and port of the
// EV3 robot that they wish to connect to. The screen shall also have a connect button that upon
// clicking will establish connectivity to the EV3 robot.
//
// Test Purpose: Verify that the configuration screen will only accept properly formed inputs.
//
public class ConfigDialogInputIPTests {

    @Rule
    public ActivityTestRule<REV3Activity> m_rule = new ActivityTestRule<>(
            REV3Activity.class);


    @Test
    //
    // Is Configuration Dialog Displayed
    //
    public void configDialogVisibleTest() {
        sleep(500);
        Log.d("CONFIG_TEST", "Running Configuration Dialog Test...");
        onView(withText("Please Enter IP Address and Port of the EV3 Robot to Connect"))
                .check(matches(isDisplayed()));
        Log.d("CONFIG_TEST", "COMPLETE");
    }

    @Test
    public void validIpAddressTest1() {
        testValidIpAddress("0.0.0.0");
    }

    @Test
    public void validIpAddressTest2() {
        testValidIpAddress("255.255.255.255");
    }

    @Test
    public void validIpAddressTest3() {
        testValidIpAddress("10.0.0.0");
    }

    @Test
    public void validIpAddressTest4() {
        testValidIpAddress("192.168.1.1");
    }

    @Test
    public void validIpAddressTest5() {
        testValidIpAddress("172.16.1.1");
    }

    @Test
    public void inValidIpAddressTest1() {

        testMalformedIpAddress("invalid.hostname.com");
    }

    @Test
    public void inValidIpAddressTest2() {

        testMalformedIpAddress("0.0.0.256");
    }

    @Test
    public void inValidIpAddressTest3() {

        testMalformedIpAddress("192.168.1.256");
    }

    @Test
    public void inValidIpAddressTest4() {

        testMalformedIpAddress("192.168.1.1.1");
    }

    @Test
    public void inValidIpAddressTest5() {

        testMalformedIpAddress("192..168.1.1");
    }

    @Test
    public void inValidIpAddressTest6() {

        testMalformedIpAddress("0.0.0.-1");
    }

    private void testValidIpAddress(String ip_address) {

        sleep(500);
        onView(withText("Please Enter IP Address and Port of the EV3 Robot to Connect"))
                .check(matches(isDisplayed()));

        onView(withId(R.id.ev3_host)).perform(replaceText(ip_address));
        onView(withId(android.R.id.button1)).perform(click());

        assertThat(m_rule.getActivity().getConfigurationDialog().getEV3Host(), is(ip_address));
    }

    private void testMalformedIpAddress(String ip_address) {
        sleep(500);
        onView(withText("Please Enter IP Address and Port of the EV3 Robot to Connect"))
                .check(matches(isDisplayed()));

        onView(withId(R.id.ev3_host)).perform(replaceText(ip_address));
        onView(withId(android.R.id.button1)).perform(click());

        assertThat(m_rule.getActivity().getConfigurationDialog().getEV3Host(), not(ip_address));
    }
}