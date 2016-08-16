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

import java.io.InputStream;
import java.util.Scanner;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static java.lang.Thread.sleep;
import static junit.framework.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
@LargeTest

//
// This test and the TurnFilterTests verifies requirement B.2.5.  The test also verifies requirement
// B.3.2.
//
// Requirement (B.2.5) : From the “drive” screen the user shall be able to adjust the commanded turn
// rate (+100% turn left/-100% turn right) speed by turning the phone left or right. The user
// interaction shall be captured by the phones gyro sensor. The gyro outputs will be translated to
// the proper EV3 protocol “turn” message.
//
// Requirement (B.3.2): The EV3 protocol shall contain a message to command the turn rate of the
// EV3. +100% shall correspond to a hard left turn. -100% shall correspond to a hard right turn.
//
// Test Purpose: Verify that upon turning the phone left or right that the “Command Turn (Gyro)”
// sequence (see design document sequence 5.4.2) is executed and providing expected outputs. Verify
// that the bounds (+100%/-100%) are held.
//
public class SensorTurnTests {

    @Rule
    public ActivityTestRule<REV3Activity> m_rule = new ActivityTestRule<>(
            REV3Activity.class);

    @android.test.UiThreadTest
    @Test
    public void rotate_left_180() {
        testFile("left_180_ref_filter");
    }

    @android.test.UiThreadTest
    @Test
    public void rotate_right_180() {
        testFile("right_180_ref_filter");
    }

    private void testFile(String file_name) {

        m_rule.getActivity().disableSensors();

        onView(withText("Please Enter IP Address and Port of the EV3 Robot to Connect"))
                .check(matches(isDisplayed()));

        onView(withId(R.id.ev3_host)).perform(replaceText("192.168.1.1"));
        onView(withId(R.id.ev3_port)).perform(replaceText("8888"));
        onView(withId(android.R.id.button1)).perform(click());

        m_rule.getActivity().setTurnSource(TurnProcessor.DataSource.SENSOR);

        InputStream ins = m_rule.getActivity().getResources().openRawResource(
                m_rule.getActivity().getResources().getIdentifier( file_name,
                        "raw", m_rule.getActivity().getPackageName()));
        Scanner scanner = new Scanner(ins);
        scanner.useDelimiter(",|\\n");
        while(scanner.hasNext()){
            long timestamp = Long.parseLong(scanner.next());
            int sensor_type = Integer.parseInt(scanner.next());
            float v0 = Float.parseFloat(scanner.next());
            float v1 = Float.parseFloat(scanner.next());
            float v2 = Float.parseFloat(scanner.next());
            double expected_roll = Double.parseDouble(scanner.next());
            double expected_command = Double.parseDouble(scanner.next());

            m_rule.getActivity().processSensorEvent(timestamp, sensor_type, v0, v1, v2);
            double command = m_rule.getActivity().getTransmittedTurnCommand();
            //Log.d("SensorTurnTest", "assertEquals: " + command + " " + expected_command + " " + 0.00001);
            //assertEquals(roll, expected_roll, 0.00001);
            assertEquals(command, expected_command, 0.00001);
        }
        scanner.close();
    }
}
