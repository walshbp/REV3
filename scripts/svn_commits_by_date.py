#!/home/bwalsh/apps/anaconda2/bin/python

from svn_util import *

import pandas as pd
import matplotlib.pyplot as plt
import matplotlib

matplotlib.style.use('ggplot')


a = SvnLog()
log_entries = a.get_repo_entries()

cc = get_commit_count(log_entries)
di = pd.to_datetime(cc.keys(), format='%Y-%m-%d')
#di = cc.keys()
java_commits = pd.Series(cc.values(), index = di )


total_log_entries = a.get_repo_entries('REV3/')
cc = get_commit_count(total_log_entries)
di = pd.to_datetime(cc.keys(), format='%Y-%m-%d')
#di = cc.keys()
total_commits = pd.Series(cc.values(), index = di)


df = pd.concat([java_commits, total_commits], axis=1 )
df.columns = [".java commits", "total commits"]
ax = df.plot(kind='bar', rot= 0)
ax.set_xlabel("Date of Commits")
ax.set_ylabel("Total # of Commits")
ax.set_title("SVN Commit Activity by Date")
#ax.xaxis_date()
ax.set_xticklabels([dt.strftime('%m-%d\n%Y') for dt in df.index])
#ax.xaxis.set_major_formatter(matplotlib.dates.DateFormatter('%Y-%m-%d'))

plt.show()

