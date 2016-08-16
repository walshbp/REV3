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
# generate simulated sensor data of phone being rotated between +/-180
#

S2NS = 1000000000.0  

period = 60.0 #secs
dt = 0.005
mag =  math.pi
times = np.arange(0.0, 200.0, dt)

for t in times:

    rot = mag * math.sin(2.0 * math.pi * t / period)
    rate = -mag * 2.0 * math.pi * 1.0 / period *math.cos(2.0 * math.pi * t / period)

    v0 = 9.81 * math.cos(rot)
    v1 = 9.81 *math.sin(rot)
    print "%d,%d,%f,%f,%f" % (t*S2NS,1, v0, v1, 0.0) # accelerometer inputs
    print "%d,%d,%f,%f,%f" % (t*S2NS,4, 0.0, 0.0, rate) # gyro inputs
