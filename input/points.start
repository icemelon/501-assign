    type Point: x#4:int y#8:int
    method show@2: points#8:List i#-4:int
    method main@27: points#-4:List i#-8:int p#-12:Point
    global size#32764:int
    instr 1: nop
    instr 2: enter 4
    instr 3: move 0 i#-4
    instr 4: checknull points#8 :List
    instr 5: add (4) length_offset#4 :int*
    instr 6: load (5) :int
    instr 7: cmplt i#-4 (6) :bool
    instr 8: blbc (7) [25]
    instr 9: checknull points#8 :List
    instr 10: checkbounds (9) i#-4
    instr 11: add (9) 8 :dynamic*
    instr 12: mul i#-4 4 :int
    instr 13: add (11) (12) :dynamic*
    instr 14: load (13) :dynamic
    instr 15: checknull (14) :dynamic
    instr 16: lddynamic (15) y_offset#? :dynamic
    instr 17: checknull (16) :dynamic
    instr 18: checktype (17) Integer_type#8 :Integer
    instr 19: add (18) value_offset#4 :int*
    instr 20: load (19) :int
    instr 21: write (20)
    instr 22: add i#-4 1 :int
    instr 23: move (22) i#-4
    instr 24: br [4]
    instr 25: wrl
    instr 26: ret 4
    instr 27: entrypc
    instr 28: enter 12
    instr 29: add size_base#32764 GP :int*
    instr 30: store 10 (29)
    instr 31: add size_base#32764 GP :int*
    instr 32: load (31) :int
    instr 33: newlist (32) :List
    instr 34: move (33) points#-4
    instr 35: move 0 i#-8
    instr 36: add size_base#32764 GP :int*
    instr 37: load (36) :int
    instr 38: cmplt i#-8 (37) :bool
    instr 39: blbc (38) [58]
    instr 40: new Point_type#12 :Point
    instr 41: move (40) p#-12
    instr 42: checknull p#-12 :Point
    instr 43: add (42) x_offset#4 :int*
    instr 44: store i#-8 (43)
    instr 45: checknull p#-12 :Point
    instr 46: add (45) y_offset#8 :int*
    instr 47: mul i#-8 i#-8 :int
    instr 48: store (47) (46)
    instr 49: checknull points#-4 :List
    instr 50: checkbounds (49) i#-8
    instr 51: add (49) 8 :dynamic*
    instr 52: mul i#-8 4 :int
    instr 53: add (51) (52) :dynamic*
    instr 54: store p#-12 (53)
    instr 55: add i#-8 1 :int
    instr 56: move (55) i#-8
    instr 57: br [36]
    instr 58: param points#-4
    instr 59: call [2]
    instr 60: ret 0
    instr 61: nop

