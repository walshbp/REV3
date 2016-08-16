#!/home/bwalsh/apps/anaconda2/bin/python

import subprocess
import shlex
from bs4 import BeautifulSoup
import re
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import datetime
import os.path
import matplotlib

matplotlib.style.use('ggplot')

class SvnDiff(object):
    def __init__(self, rev_num, file_name):
        command_line = 'svn diff -c' + str(rev_num) + " " + file_name
        self._output = subprocess.check_output(shlex.split(command_line)).split('\n')[5:]

    def get_num_added_lines(self):
        count = 0
        for line in self._output:
            if line.startswith('+'):
                count = count + 1
        return count


    def get_num_deleted_lines(self):
        count = 0
        for line in self._output:
            if line.startswith('-'):
                count = count + 1
        return count




class SvnLog(object):
    def __init__(self):
        command_line = 'svn log -v --xml'
        output = subprocess.check_output(shlex.split(command_line))
        self._soup = BeautifulSoup(output, 'xml')


    def get_repo_entries(self, file_regex = None):
        if file_regex:
            rev3_paths = self._soup.find_all("path",string=re.compile(file_regex))
        else:
            rev3_paths = self._soup.find_all("path",string=re.compile("/edu/arizona/ece573/bpwalsh/rev3/"))

        entries = []
        for path in rev3_paths:
            logentry = path.parent.parent
            if logentry not in entries:
                entries.append(logentry)
        entries.reverse()     # early entries first
        return entries

    def get_file_activity(self, file):
        rev3_paths = self._soup.find_all("path",string=file)

        entries = []
        for path in rev3_paths:
            logentry = path.parent.parent
            if logentry not in entries:
                entries.append(logentry)

        return entries

    def get_added_lines_by_rev(self, logentry, file = None):
        if file:
            files = logentry.find_all("path",string=re.compile(file))
        else:
            files = logentry.find_all("path",string=re.compile("/edu/arizona/ece573/bpwalsh/rev3/"))
        count = 0
        for f in files:
            f_str = f.string
            short_file = f_str[f_str.find('REV3'):]
            d = SvnDiff(logentry['revision'], short_file)
            count = count + d.get_num_added_lines()
        return count

def get_commit_count(entries):
    commit_count = {}
    for l in entries:
        dt = datetime.datetime.strptime(l.date.string, '%Y-%m-%dT%H:%M:%S.%fZ')
        udate = datetime.datetime(year = dt.year, month = dt.month, day = dt.day)
        if udate in commit_count: 
            commit_count[udate] += 1
        else:
            commit_count[udate] = 1
    return commit_count

a = SvnLog()
log_entries = a.get_repo_entries()

cc = get_commit_count(log_entries)
di = pd.to_datetime(cc.keys(), format='%Y-%m-%d').map(lambda t: t.strftime('%Y-%m-%d'))
df = pd.Series(cc.values(), index = di )
df.plot(kind='bar')
plt.show()

total_log_entries = a.get_repo_entries('REV3/')
cc = get_commit_count(total_log_entries)
di = pd.to_datetime(cc.keys(), format='%Y-%m-%d').map(lambda t: t.strftime('%Y-%m-%d'))
df2 = pd.Series(cc.values(), index = di )
df2.plot(kind='bar')
plt.show()

df3 = pd.concat([df, df2], axis=1 )
df3.columns = [".java commits", "total commits"]
df3.plot(kind='bar')
plt.show()

#for l in log_entries:
#    count = a.get_added_lines_by_rev(l)
#    print l['revision'] + " " + str(count)

dates = []
revs = []
for l in log_entries:
   dt = datetime.datetime.strptime(l.date.string, '%Y-%m-%dT%H:%M:%S.%fZ')
   dates.append(dt)
   revs.append(l['revision'])




files = [
"REV3/app/src/main/java/edu/arizona/ece573/bpwalsh/rev3/SensorDataLogger.java",
"REV3/app/src/main/java/edu/arizona/ece573/bpwalsh/rev3/ConfigurationDialog.java",
"REV3/app/src/main/java/edu/arizona/ece573/bpwalsh/rev3/CommandProcessor.java",
"REV3/app/src/main/java/edu/arizona/ece573/bpwalsh/rev3/SensorPowerProcessor.java",
"REV3/app/src/main/java/edu/arizona/ece573/bpwalsh/rev3/REV3Activity.java",
"REV3/app/src/main/java/edu/arizona/ece573/bpwalsh/rev3/GUITurnProcessor.java",
"REV3/app/src/main/java/edu/arizona/ece573/bpwalsh/rev3/SensorTurnProcessor.java",
"REV3/app/src/main/java/edu/arizona/ece573/bpwalsh/rev3/ProtocolProcessor.java",
"REV3/app/src/main/java/edu/arizona/ece573/bpwalsh/rev3/GUIPowerProcessor.java"]


data = {}
for i,f in enumerate(files):
   lines_added = []
   for l in log_entries:
       count = a.get_added_lines_by_rev(l,f)
       lines_added.append(count)
   key = os.path.basename(f)
   data[key] = lines_added

   

df = pd.DataFrame(data, index = revs)
df.plot(kind='bar', stacked = True)
plt.show()
