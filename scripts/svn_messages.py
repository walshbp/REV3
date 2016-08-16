#!/home/bwalsh/apps/anaconda2/bin/python

from svn_util import *

import pandas as pd
import matplotlib.pyplot as plt
import matplotlib

matplotlib.style.use('ggplot')


a = SvnLog()
log_entries = a.get_repo_entries()

for l in log_entries:
    dt = datetime.datetime.strptime(l.date.string, '%Y-%m-%dT%H:%M:%S.%fZ')
    print dt.strftime('%m-%d-%Y') + ", " + l['revision'] +  ', ' + l.msg.string



