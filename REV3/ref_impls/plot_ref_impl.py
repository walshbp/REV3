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
# Plots data file from reference filter implemntation (filter.py)
#

NS2S = 1.0 / 1000000000.0  

def plot_it(file_name):
    fig, ax = plt.subplots()
    with open(file_name, 'rb') as csvfile:
        datareader = csv.reader(csvfile, delimiter=',', quotechar='|')
        ts = list()
        roll_ests = list()
        commands = list()
        t0 = None
        for line in datareader:
            #print line
            timestamp = int(line[0])
            sensor_type = int(line[1])
            v0 = float(line[2])
            v1 = float(line[3])
            v2 = float(line[4])
            roll_est = float(line[5])
            command = float(line[6])
            if not t0:
                t0 = timestamp
            t = (timestamp-t0) * NS2S
            ts.append(t)
            roll_ests.append(roll_est* 180.0 / math.pi)
            commands.append(command)

        ax.plot(ts,roll_ests, label = "Ref Filter Roll Est")

        ax.plot(ts,commands, label=  "Generated Command")

    plt.xlabel('Time (secs)')
    plt.ylabel('Roll Estimate (degrees), Generated Command')
    plt.title('Reference Sensor Turn Filter Implementation')
    plt.legend()
    plt.show()

parser = argparse.ArgumentParser(description='Plot data from reference filter implementation')
parser.add_argument('in_file', metavar='IN_FILE', type=str,
                   help='The REV3 recorded reference impl log file to be plotted')

args = parser.parse_args()
plot_it(args.in_file)
