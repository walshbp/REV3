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
from collections import OrderedDict

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
            rev3_paths = self._soup.find_all("path",string=re.compile("main/java/edu/arizona/ece573/bpwalsh/rev3/"))

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

    def get_deleted_lines_by_rev(self, logentry, file = None):
        if file:
            files = logentry.find_all("path",string=re.compile(file))
        else:
            files = logentry.find_all("path",string=re.compile("/edu/arizona/ece573/bpwalsh/rev3/"))
        count = 0
        for f in files:
            f_str = f.string
            short_file = f_str[f_str.find('REV3'):]
            d = SvnDiff(logentry['revision'], short_file)
            count = count + d.get_num_deleted_lines()
        return count

def get_commit_count(entries):
    commit_count = OrderedDict()
    for l in entries:
        dt = datetime.datetime.strptime(l.date.string, '%Y-%m-%dT%H:%M:%S.%fZ')
        udate = datetime.datetime(year = dt.year, month = dt.month, day = dt.day)
        if udate in commit_count: 
            commit_count[udate] += 1
        else:
            commit_count[udate] = 1
    return commit_count


