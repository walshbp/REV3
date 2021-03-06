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
// This test in conjunction with ConfigDialogInputIPTests, ConfigDialogInputPortTests, and
// ConfigDialogSequenceTests, verifies requirement B.1.1
//
// Requirement: Upon startup the REV3 application shall immediately enter a configuration screen.
// The configuration screen shall allow the user to enter the IP address or hostname and port of the
// EV3 robot that they wish to connect to. The screen shall also have a connect button that upon
// clicking will establish connectivity to the EV3 robot.
//
// Test Purpose: Verify that “Upon startup the REV3 application shall immediately enter a
// configuration screen.”
//
public class ConfigDialogVisibleTests {

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

}