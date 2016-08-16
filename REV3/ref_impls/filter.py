#!/usr/bin/env python 

#
# REV3 Project
# Copyright 2016, - All Rights Reserved
#
# Team BPWALSH
#

import csv
import math
import argparse
import matplotlib.pyplot as plt
import numpy as np

#
# Plots reference filter metrics and/or generates reference implementation
# data file.
#
def plot_it(file_name, factors):
    fig, ax = plt.subplots()
    first = True
    threshold = 5.0
    print factors
    for factor, label  in factors:
        f = Filter(factor)
        with open(file_name, 'rb') as csvfile:
            datareader = csv.reader(csvfile, delimiter=',', quotechar='|')
            ts = list()
            rolls = list()
            roll_ests = list()
            commands = list()
            old_command = 0
            for line in datareader:
                #print line
                timestamp = int(line[0])
                sensor_type = int(line[1])
                v0 = float(line[2])
                v1 = float(line[3])
                v2 = float(line[4])
                #print timestamp, sensor_type, v0, v1, v2
                t, roll, roll_acc_est = f.filter(timestamp, sensor_type, v0, v1, v2)
                ts.append(t)
                rolls.append(roll* 180.0 / math.pi)
                roll_ests.append(roll_acc_est* 180.0 / math.pi)

                command = roll * 180.0 / math.pi  * 100.0 / 90.0;
                if command > 100.0:
                    command = 100.0
                elif command < -100.0:
                    command = -100.0
                #print "com: ", old_command, command
                if abs(old_command - command) > threshold:
                    old_command = command
                commands.append(old_command)
                #print t, roll * 180.0 / math.pi
        if first:
            first = False
            ax.plot(ts,roll_ests, label = "Accelerometer Roll Est")
        else:
            #ax2 = ax.twinx()
            ax.plot(ts,commands, label=  "Generated Command")
            #ax2.xlabel("Generated Command")
        ax.plot(ts,rolls, label = label)
    plt.xlabel('Time (secs)')
    plt.ylabel('Roll Estimate (degrees)')
    plt.title('Reference Sensor Turn Filter Implementation')
    plt.legend()
    plt.show()

def write_ref_outputs(in_file_name, out_file_name, factor):

    threshold = 5.0
    old_command = 0.0

    f = Filter(factor)
    with open(out_file_name, 'wb') as outfile:
        with open(in_file_name, 'rb') as csvfile:
            datareader = csv.reader(csvfile, delimiter=',', quotechar='|')
            ts = list()
            rolls = list()
            for line in datareader:
                #print line
                timestamp = int(line[0])
                sensor_type = int(line[1])
                v0 = float(line[2])
                v1 = float(line[3])
                v2 = float(line[4])
                #print timestamp, sensor_type, v0, v1, v2
                t, roll, roll_acc_est = f.filter(timestamp, sensor_type, v0, v1, v2)

                command = round(-roll * 180.0 / math.pi  * 100.0 / 90.0 / 5) * 5.0;
                if command > 100.0:
                    command = 100.0
                elif command < -100.0:
                    command = -100.0
                #print "com: ", old_command, command
                if abs(old_command - command) >= threshold:
                    old_command = command
                outfile.write("%d,%d,%f,%f,%f,%f,%f\n" 
                    % (timestamp, sensor_type, v0, v1, v2, roll, old_command))
                #print("%d,%f,%f" % (timestamp, roll, roll_acc_est))

class Filter(object):

    NS2S = 1.0 / 1000000000.0    
    TYPE_ACCELEROMETER = 1
    TYPE_GYROSCOPE = 4

    def __init__(self, factor):
        self._acc_roll_est = 0.0
        self._filter_roll_est = 0.0
        self._timestamp = None
        self._timestamp0 = None
        self._factor = factor
        self._initialized = False

    def filter(self, timestamp, sensor_type, v0, v1, v2):
        if not self._timestamp0:
            self._timestamp0 = timestamp
            #print "Changing Timestamo"

        if sensor_type == Filter.TYPE_ACCELEROMETER:
            self._acc_roll_est = math.atan2(v1,v0)
            if not self._initialized:
                self._filter_roll_est = self._acc_roll_est
                self._initialized = True

        elif sensor_type == Filter.TYPE_GYROSCOPE:
            if self._timestamp and self._initialized:
                # complimentary filter, blending gyro and accelerometer
                # measurements
                dt = (timestamp - self._timestamp) * Filter.NS2S
                #print "dt: ", dt, self._factor, self._filter_roll_est
                pre_est = self._filter_roll_est -  v2 * dt
                if abs(pre_est - self._acc_roll_est) < math.pi:
                    self._filter_roll_est =  ((1.0 - self._factor) * 
                        ( self._filter_roll_est -  v2 * dt) + 
                        self._factor * self._acc_roll_est)
                else:
                    # our accelerometer estimate is invalid beyond +/- 180 deg
                    # in these cases we will just use gyro inputs
                    self._filter_roll_est = pre_est

            self._timestamp = timestamp
            
        else:
            raise Exception('Unknown sensor type: ' + str(sensor_type))

        return ((timestamp-self._timestamp0)* Filter.NS2S, self._filter_roll_est, self._acc_roll_est)


parser = argparse.ArgumentParser(description='Reference filter implementation')

parser.add_argument('in_file', metavar='IN_FILE', type=str,
                   help='The REV3 recorded sensor log file to be played back through the reference filter')
parser.add_argument('--plot', action='store_true', help = 'Plot reference filter outputs with various blending factors')
parser.add_argument('--record',  type=str,  metavar='OUT_FILE', help = 'Record reference filter outputs for comparison with unit tests')

args = parser.parse_args()

#print str(args)
if args.plot:
    plot_it(args.in_file,  [(0.0,"Uncorrected Gyro Est"),(0.05,"Ref Filter Est")])

if args.record:
    write_ref_outputs(args.in_file, args.record, 0.05)
