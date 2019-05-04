import json
import os
import sys

data = json.load(sys.stdin)
newdata = {x: os.environ.get(x, data[x]) for x in data}
json.dump(newdata, sys.stdout, indent=2)
