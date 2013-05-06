import os;

def getCmdOutput(cmd):
  child = os.popen(cmd)
  data = child.read()
  err = child.close()
  if err:
    raise(RuntimeError, '%s failed with exit code %d' %(cmd, err))
  return data.split('\n')


dartFiles = os.listdir("input")

for f in dartFiles:
  pos = f.index('.')
  name = f[0 : pos]
  cmd = './run.sh input/' + f
  #print(cmd)
 
  avg1 = 0.0
  avg2 = 0.0
  for i in range(0, 10):
    output = getCmdOutput(cmd)
    avg1 += float(output[0])
    avg2 += float(output[1])
    block = int(output[2])
    max = int(output[3])

  avg1 /= 10.0
  avg2 /= 10.0
  print('%s\t%.2f\t%.2f\t%d\t%d' %(name, avg1, avg2, block, max))

