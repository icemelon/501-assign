    method main@2: i#-4:int j#-8:int v#-12:int
    global prime#32764:List
    instr 1: nop
    instr 2: entrypc
    instr 3: enter 12
    instr 4: newlist 400 :List
    instr 5: add prime_base#32764 GP :List*
    instr 6: store (4) (5)
    instr 7: add prime_base#32764 GP :List*
    instr 8: load (7) :List
    instr 9: checknull (8) :List
    instr 10: checkbounds (9) 0
    instr 11: add (9) 8 :dynamic*
    instr 12: mul 0 4 :int
    instr 13: add (11) (12) :dynamic*
    instr 14: new Integer_type#8 :Integer
    instr 15: add (14) value_offset#4 :int*
    instr 16: store 2 (15)
    instr 17: store (14) (13)
    instr 18: add prime_base#32764 GP :List*
    instr 19: load (18) :List
    instr 20: checknull (19) :List
    instr 21: checkbounds (20) 0
    instr 22: add (20) 8 :dynamic*
    instr 23: mul 0 4 :int
    instr 24: add (22) (23) :dynamic*
    instr 25: load (24) :dynamic
    instr 26: checknull (25) :dynamic
    instr 27: checktype (26) Integer_type#8 :Integer
    instr 28: add (27) value_offset#4 :int*
    instr 29: load (28) :int
    instr 30: write (29)
    instr 31: move 1 i#-4
    instr 32: move 3 v#-12
    instr 33: cmplt i#-4 400 :bool
    instr 34: blbc (33) [106]
    instr 35: move 0 j#-8
    instr 36: cmplt j#-8 i#-4 :bool
    instr 37: blbc (36) [87]
    instr 38: add prime_base#32764 GP :List*
    instr 39: load (38) :List
    instr 40: checknull (39) :List
    instr 41: checkbounds (40) j#-8
    instr 42: add (40) 8 :dynamic*
    instr 43: mul j#-8 4 :int
    instr 44: add (42) (43) :dynamic*
    instr 45: load (44) :dynamic
    instr 46: add prime_base#32764 GP :List*
    instr 47: load (46) :List
    instr 48: checknull (47) :List
    instr 49: checkbounds (48) j#-8
    instr 50: add (48) 8 :dynamic*
    instr 51: mul j#-8 4 :int
    instr 52: add (50) (51) :dynamic*
    instr 53: load (52) :dynamic
    instr 54: checknull (45) :dynamic
    instr 55: checktype (54) Integer_type#8 :Integer
    instr 56: add (55) value_offset#4 :int*
    instr 57: load (56) :int
    instr 58: checknull (53) :dynamic
    instr 59: checktype (58) Integer_type#8 :Integer
    instr 60: add (59) value_offset#4 :int*
    instr 61: load (60) :int
    instr 62: mul (57) (61) :int
    instr 63: cmple (62) v#-12 :bool
    instr 64: blbs (63) [68]
    instr 65: sub i#-4 1 :int
    instr 66: move (65) j#-8
    instr 67: br [84]
    instr 68: add prime_base#32764 GP :List*
    instr 69: load (68) :List
    instr 70: checknull (69) :List
    instr 71: checkbounds (70) j#-8
    instr 72: add (70) 8 :dynamic*
    instr 73: mul j#-8 4 :int
    instr 74: add (72) (73) :dynamic*
    instr 75: load (74) :dynamic
    instr 76: checknull (75) :dynamic
    instr 77: checktype (76) Integer_type#8 :Integer
    instr 78: add (77) value_offset#4 :int*
    instr 79: load (78) :int
    instr 80: mod v#-12 (79) :int
    instr 81: cmpeq (80) 0 :bool
    instr 82: blbc (81) [84]
    instr 83: move i#-4 j#-8
    instr 84: add j#-8 1 :int
    instr 85: move (84) j#-8
    instr 86: br [36]
    instr 87: cmpeq j#-8 i#-4 :bool
    instr 88: blbc (87) [103]
    instr 89: add prime_base#32764 GP :List*
    instr 90: load (89) :List
    instr 91: checknull (90) :List
    instr 92: checkbounds (91) i#-4
    instr 93: add (91) 8 :dynamic*
    instr 94: mul i#-4 4 :int
    instr 95: add (93) (94) :dynamic*
    instr 96: new Integer_type#8 :Integer
    instr 97: add (96) value_offset#4 :int*
    instr 98: store v#-12 (97)
    instr 99: store (96) (95)
    instr 100: write v#-12
    instr 101: add i#-4 1 :int
    instr 102: move (101) i#-4
    instr 103: add v#-12 2 :int
    instr 104: move (103) v#-12
    instr 105: br [33]
    instr 106: wrl
    instr 107: ret 0
    instr 108: nop

