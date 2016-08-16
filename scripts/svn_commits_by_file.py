#!/home/bwalsh/apps/anaconda2/bin/python

from svn_util import *

import pandas as pd
import matplotlib.pyplot as plt
import matplotlib

matplotlib.style.use('ggplot')


a = SvnLog()
log_entries = a.get_repo_entries()


dates = []
revs = []
for l in log_entries:
   dt = datetime.datetime.strptime(l.date.string, '%Y-%m-%dT%H:%M:%S.%fZ')
   dates.append(dt)
   revs.append(l['revision'])


files = [
"REV3/app/src/main/java/edu/arizona/ece573/bpwalsh/rev3/ConfigurationDialog.java",
"REV3/app/src/main/java/edu/arizona/ece573/bpwalsh/rev3/CommandProcessor.java",
"REV3/app/src/main/java/edu/arizona/ece573/bpwalsh/rev3/SensorPowerProcessor.java",
"REV3/app/src/main/java/edu/arizona/ece573/bpwalsh/rev3/REV3Activity.java",
"REV3/app/src/main/java/edu/arizona/ece573/bpwalsh/rev3/GUITurnProcessor.java",
"REV3/app/src/main/java/edu/arizona/ece573/bpwalsh/rev3/SensorTurnProcessor.java",
"REV3/app/src/main/java/edu/arizona/ece573/bpwalsh/rev3/ProtocolProcessor.java",
"REV3/app/src/main/java/edu/arizona/ece573/bpwalsh/rev3/GUIPowerProcessor.java",
]

data = {}
data2 = {}
for i,f in enumerate(files):
   lines_added = []
   lines_deleted = []
   for l in log_entries:
       count = a.get_added_lines_by_rev(l,f)
       lines_added.append(count)
       count = a.get_deleted_lines_by_rev(l,f)
       lines_deleted.append(count)
   key = os.path.basename(f)
   data[key] = lines_added
   data2[key] = lines_deleted

   

df = pd.DataFrame(data, index = revs)
ax = df.plot(kind='bar', stacked = True, rot=0)
ax.set_xlabel("Revision #")
ax.set_ylabel("# of Java Source Lines Added")
ax.set_title("SVN Activity: Lines Added to File WRT Revision")
plt.show()

   

df = pd.DataFrame(data2, index = revs)
ax = df.plot(kind='bar', stacked = True, rot=0)
ax.set_xlabel("Revision #")
ax.set_ylabel("# of Java Source Lines Deleted")
ax.set_title("SVN Activity: Lines Deleted to File WRT Revision")
plt.show()
