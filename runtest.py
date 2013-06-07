import os;

def getCmdOutput(cmd):
  child = os.popen(cmd)
  data = child.read()
  err = child.close()
  if err:
    raise(RuntimeError, '%s failed with exit code %d' %(cmd, err))
  return data.split('\n')


dartFiles = os.listdir("input")

for file in dartFiles:
  pos = file.index('.')
  name = file[0 : pos]

  print("Program: " + name)
  optCmd = "./run.sh input/" + file + " -profile=pos -backend=asm > 1.start"
  os.system(optCmd)
  runCmd = "start -r -p 1.start"
#  runCmd = "start -r -p input/" + file
  os.system(runCmd)

