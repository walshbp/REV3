#!/usr/bin/env python

#
# REV3 Project
# Copyright 2016, - All Rights Reserved
#
# Team BPWALSH
#

import numpy as np
import math

#
# generate simulated EV3 telemetry data

S2NS = 1000000000.0  

period = 5.0 #secs
dt = 0.005
mag =  math.pi
times = np.arange(0.0, 200.0, dt)

for t in times:

    rot = mag * math.sin(2.0 * math.pi * t / period)
    print "TM"
    print str(rot * 180.0 / math.pi)