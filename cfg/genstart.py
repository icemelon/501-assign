import os;

dartFiles = os.listdir("examples")

for f in dartFiles:
  pos = f.index('.')
  name = f[0 : pos]
  cmd = 'start -c examples/' + f + ' > input/' + name + '.start';
  os.system(cmd)

