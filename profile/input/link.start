    type A: value#4:int next#8:A
    method init@2: count#8:int a#-4:A
    method sum@24: a#8:A
    method main@44: a#-4:A
    global tmp#32764:A
    global total#32760:int
    instr 1: nop
    instr 2: enter 4
    instr 3: cmple count#8 0 :bool
    instr 4: blbc (3) [8]
    instr 5: add tmp_base#32764 GP :A*
    instr 6: store 0 (5)
    instr 7: br [23]
    instr 8: sub count#8 1 :int
    instr 9: param (8)
    instr 10: call [2]
    instr 11: new A_type#12 :A
    instr 12: move (11) a#-4
    instr 13: checknull a#-4 :A
    instr 14: add (13) value_offset#4 :int*
    instr 15: store count#8 (14)
    instr 16: checknull a#-4 :A
    instr 17: add (16) next_offset#8 :A*
    instr 18: add tmp_base#32764 GP :A*
    instr 19: load (18) :A
    instr 20: store (19) (17)
    instr 21: add tmp_base#32764 GP :A*
    instr 22: store a#-4 (21)
    instr 23: ret 4
    instr 24: enter 0
    instr 25: isnull a#8 :bool
    instr 26: blbc (25) [30]
    instr 27: add total_base#32760 GP :int*
    instr 28: store 0 (27)
    instr 29: br [43]
    instr 30: checknull a#8 :A
    instr 31: add (30) next_offset#8 :A*
    instr 32: load (31) :A
    instr 33: param (32)
    instr 34: call [24]
    instr 35: add total_base#32760 GP :int*
    instr 36: load (35) :int
    instr 37: checknull a#8 :A
    instr 38: add (37) value_offset#4 :int*
    instr 39: load (38) :int
    instr 40: add (36) (39) :int
    instr 41: add total_base#32760 GP :int*
    instr 42: store (40) (41)
    instr 43: ret 4
    instr 44: entrypc
    instr 45: enter 4
    instr 46: param 10
    instr 47: call [2]
    instr 48: add tmp_base#32764 GP :A*
    instr 49: load (48) :A
    instr 50: move (49) a#-4
    instr 51: param a#-4
    instr 52: call [24]
    instr 53: add total_base#32760 GP :int*
    instr 54: load (53) :int
    instr 55: write (54)
    instr 56: wrl
    instr 57: ret 0
    instr 58: nop

