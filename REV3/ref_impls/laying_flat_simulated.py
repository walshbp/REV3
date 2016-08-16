#!/usr/bin/env python

#
# REV3 Project
# Copyright 2016, - All Rights Reserved
#
# Team BPWALSH
#

import numpy as np

#
# generate simulated sensor data of phone laying flat
#

NS2S = 1.0 / 1000000000.0  

times = np.arange(0.0, 200.0, 0.05)

for t in times:
    print "%d,%d,%f,%f,%f" % (t/NS2S,1, 0.0, 0.0, 9.81) # accelerometer inputs
    print "%d,%d,%f,%f,%f" % (t/NS2S,4, 0.0, 0.0, 0.0) # gyro inputs