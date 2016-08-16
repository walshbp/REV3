//
// REV3 Project
// Copyright 2016, - All Rights Reserved
//
// Team BPWALSH
//
package edu.arizona.ece573.bpwalsh.rev3;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

// Runs all unit tests.
@RunWith(Suite.class)
@Suite.SuiteClasses({ConfigDialogInputIPTests.class, ConfigDialogInputPortTests.class,
        ConfigDialogVisibleTests.class, ConfigDialogSequenceTests.class,
        GuiTurnTests.class, GuiPowerTests.class, TurnFilterTests.class, SensorTurnTests.class,
        DisconnectTests.class, DeltaHeadingTests.class})
public class UnitTestSuite {}