    type A: x#4:int y#8:int
    type B: y#4:int a#8:A
    type C: y#4:A
    method init1@2: a#12:A b#8:B
    method init2@10: c#8:dynamic
    method main@19: a#-4:A b2#-8:B c#-12:C
    global b#32764:dynamic
    instr 1: nop
    instr 2: enter 0
    instr 3: checknull b#8 :B
    instr 4: add (3) a_offset#8 :A*
    instr 5: store a#12 (4)
    instr 6: checknull a#12 :A
    instr 7: add (6) x_offset#4 :int*
    instr 8: store 19 (7)
    instr 9: ret 8
    instr 10: enter 0
    instr 11: checknull c#8 :dynamic
    instr 12: new Integer_type#8 :Integer
    instr 13: add (12) value_offset#4 :int*
    instr 14: store 23 (13)
    instr 15: stdynamic (12) (11) y_offset#?
    instr 16: add b_base#32764 GP :dynamic*
    instr 17: store c#8 (16)
    instr 18: ret 4
    instr 19: entrypc
    instr 20: enter 12
    instr 21: new A_type#12 :A
    instr 22: move (21) a#-4
    instr 23: add b_base#32764 GP :dynamic*
    instr 24: store 0 (23)
    instr 25: new B_type#12 :B
    instr 26: move (25) b2#-8
    instr 27: param a#-4
    instr 28: param b2#-8
    instr 29: call [2]
    instr 30: param a#-4
    instr 31: call [10]
    instr 32: param b2#-8
    instr 33: call [10]
    instr 34: checknull b2#-8 :B
    instr 35: add (34) a_offset#8 :A*
    instr 36: load (35) :A
    instr 37: checknull (36) :A
    instr 38: add (37) y_offset#8 :int*
    instr 39: load (38) :int
    instr 40: add b_base#32764 GP :dynamic*
    instr 41: load (40) :dynamic
    instr 42: checknull (41) :dynamic
    instr 43: lddynamic (42) y_offset#? :dynamic
    instr 44: checknull (43) :dynamic
    instr 45: checktype (44) Integer_type#8 :Integer
    instr 46: add (45) value_offset#4 :int*
    instr 47: load (46) :int
    instr 48: add (39) (47) :int
    instr 49: checknull b2#-8 :B
    instr 50: add (49) a_offset#8 :A*
    instr 51: load (50) :A
    instr 52: checknull (51) :A
    instr 53: add (52) x_offset#4 :int*
    instr 54: load (53) :int
    instr 55: add (48) (54) :int
    instr 56: sub (55) 23 :int
    instr 57: write (56)
    instr 58: wrl
    instr 59: new Integer_type#8 :Integer
    instr 60: add (59) value_offset#4 :int*
    instr 61: store 42 (60)
    instr 62: istype (59) Integer_type#8 :bool
    instr 63: blbc (62) [74]
    instr 64: add b_base#32764 GP :dynamic*
    instr 65: load (64) :dynamic
    instr 66: checknull (65) :dynamic
    instr 67: lddynamic (66) y_offset#? :dynamic
    instr 68: checknull (67) :dynamic
    instr 69: checktype (68) Integer_type#8 :Integer
    instr 70: add (69) value_offset#4 :int*
    instr 71: load (70) :int
    instr 72: write (71)
    instr 73: wrl
    instr 74: write 7
    instr 75: wrl
    instr 76: ret 0
    instr 77: nop

