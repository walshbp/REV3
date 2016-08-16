//
// REV3 Project
// Copyright 2016, - All Rights Reserved
//
// Team BPWALSH
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
import static junit.framework.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
    @LargeTest
//
// This test and the SensorTurnTest verifies requirement B.2.5
//
// Requirement: From the “drive” screen the user shall be able to adjust the commanded turn rate
// (+100% turn left/-100% turn right) speed by turning the phone left or right. The user interaction
// shall be captured by the phones gyro sensor. The gyro outputs will be translated to the proper
// EV3 protocol “turn” message.
//
// Test Purpose: Verify that the turn filter is behaving in accordance to the reference
// implementation.
//
    public class TurnFilterTests {

        @Rule
        public ActivityTestRule<REV3Activity> m_rule = new ActivityTestRule<>(
                REV3Activity.class);


    @Test
    public void resting_at_0() {
        testFile("at_0_ref_filter");
    }

    @Test
    public void rotate_left_180() {
        testFile("left_180_ref_filter");
    }

    @Test
    public void laying_flat() {
        testFile("laying_flat_ref_filter");
    }

    @Test
    public void laying_flat_simulated() {
        testFile("laying_flat_simulated_ref_filter");
    }
    @Test
    public void rotate_right_180() {
        testFile("right_180_ref_filter");
    }

    @Test
    public void cycle_plus_minus_90() {
        testFile("rotate_to_90_ref_filter");
    }

    @Test
    public void cycle_180_simulated() {
        testFile("cyclic_simulated_ref_filter");
    }

    private void testFile(String file_name) {
        m_rule.getActivity().disableSensors();

        //Log.d("SensorTurnTest", "start: ");
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
            double roll = m_rule.getActivity().getTurnProcessorCommand();
            double command = m_rule.getActivity().getTransmittedTurnCommand();
            //Log.d("SensorTurnTest", "assertEquals: " + timestamp + " " + roll + " " + expected_roll + " " + 0.00001);
            assertEquals(roll, expected_roll, 0.00001);
            //assertEquals(command, expected_command, 0.00001);
        }
        scanner.close();    }
}
