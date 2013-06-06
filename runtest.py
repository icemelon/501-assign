import os
import sys

def getCmdOutput(cmd):
  child = os.popen(cmd)
  data = child.read()
  err = child.close()
  if err:
    raise(RuntimeError, '%s failed with exit code %d' %(cmd, err))
  return data.split('\n')


inputFile = "input/" + sys.argv[1] + ".start"
firstRunCmd = "start -r -p " + inputFile
os.system(firstRunCmd)
optCmd = "./run.sh " + inputFile + " -profile=pos -backend=asm > 1.start"
os.system(optCmd)
secondRunCmd = "start -r -p 1.start"
os.system(secondRunCmd)

