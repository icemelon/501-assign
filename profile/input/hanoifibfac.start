    method Factorial@2: n#8:int
    method FibRec@17: n#8:int x#-4:int y#-8:int
    method MoveDisc@39: from#12:int to#8:int
    method MoveTower@49: from#20:int by#16:int to#12:int height#8:int
    method Hanoi@72: height#8:int
    method main@86:
    global a#32764:int
    global m#32760:int
    global q#32756:int
    global r#32752:int
    global count#32748:int
    global res#32744:int
    instr 1: nop
    instr 2: enter 0
    instr 3: cmpeq n#8 0 :bool
    instr 4: blbc (3) [8]
    instr 5: add res_base#32744 GP :int*
    instr 6: store 1 (5)
    instr 7: br [16]
    instr 8: sub n#8 1 :int
    instr 9: param (8)
    instr 10: call [2]
    instr 11: add res_base#32744 GP :int*
    instr 12: load (11) :int
    instr 13: mul n#8 (12) :int
    instr 14: add res_base#32744 GP :int*
    instr 15: store (13) (14)
    instr 16: ret 4
    instr 17: enter 8
    instr 18: cmple n#8 1 :bool
    instr 19: blbc (18) [23]
    instr 20: add res_base#32744 GP :int*
    instr 21: store 1 (20)
    instr 22: br [38]
    instr 23: sub n#8 1 :int
    instr 24: param (23)
    instr 25: call [17]
    instr 26: add res_base#32744 GP :int*
    instr 27: load (26) :int
    instr 28: move (27) x#-4
    instr 29: sub n#8 2 :int
    instr 30: param (29)
    instr 31: call [17]
    instr 32: add res_base#32744 GP :int*
    instr 33: load (32) :int
    instr 34: move (33) y#-8
    instr 35: add x#-4 y#-8 :int
    instr 36: add res_base#32744 GP :int*
    instr 37: store (35) (36)
    instr 38: ret 4
    instr 39: enter 0
    instr 40: write from#12
    instr 41: write to#8
    instr 42: wrl
    instr 43: add count_base#32748 GP :int*
    instr 44: load (43) :int
    instr 45: add (44) 1 :int
    instr 46: add count_base#32748 GP :int*
    instr 47: store (45) (46)
    instr 48: ret 8
    instr 49: enter 0
    instr 50: cmpeq height#8 1 :bool
    instr 51: blbc (50) [56]
    instr 52: param from#20
    instr 53: param to#12
    instr 54: call [39]
    instr 55: br [71]
    instr 56: param from#20
    instr 57: param to#12
    instr 58: param by#16
    instr 59: sub height#8 1 :int
    instr 60: param (59)
    instr 61: call [49]
    instr 62: param from#20
    instr 63: param to#12
    instr 64: call [39]
    instr 65: param by#16
    instr 66: param from#20
    instr 67: param to#12
    instr 68: sub height#8 1 :int
    instr 69: param (68)
    instr 70: call [49]
    instr 71: ret 16
    instr 72: enter 0
    instr 73: add count_base#32748 GP :int*
    instr 74: store 0 (73)
    instr 75: param 1
    instr 76: param 2
    instr 77: param 3
    instr 78: param height#8
    instr 79: call [49]
    instr 80: wrl
    instr 81: add count_base#32748 GP :int*
    instr 82: load (81) :int
    instr 83: write (82)
    instr 84: wrl
    instr 85: ret 4
    instr 86: entrypc
    instr 87: enter 0
    instr 88: add a_base#32764 GP :int*
    instr 89: store 16807 (88)
    instr 90: add m_base#32760 GP :int*
    instr 91: store 127 (90)
    instr 92: add m_base#32760 GP :int*
    instr 93: load (92) :int
    instr 94: mul (93) 256 :int
    instr 95: add (94) 255 :int
    instr 96: add m_base#32760 GP :int*
    instr 97: store (95) (96)
    instr 98: add m_base#32760 GP :int*
    instr 99: load (98) :int
    instr 100: mul (99) 256 :int
    instr 101: add (100) 255 :int
    instr 102: add m_base#32760 GP :int*
    instr 103: store (101) (102)
    instr 104: add m_base#32760 GP :int*
    instr 105: load (104) :int
    instr 106: mul (105) 256 :int
    instr 107: add (106) 255 :int
    instr 108: add m_base#32760 GP :int*
    instr 109: store (107) (108)
    instr 110: add m_base#32760 GP :int*
    instr 111: load (110) :int
    instr 112: add a_base#32764 GP :int*
    instr 113: load (112) :int
    instr 114: div (111) (113) :int
    instr 115: add q_base#32756 GP :int*
    instr 116: store (114) (115)
    instr 117: add m_base#32760 GP :int*
    instr 118: load (117) :int
    instr 119: add a_base#32764 GP :int*
    instr 120: load (119) :int
    instr 121: mod (118) (120) :int
    instr 122: add r_base#32752 GP :int*
    instr 123: store (121) (122)
    instr 124: param 7
    instr 125: call [2]
    instr 126: add res_base#32744 GP :int*
    instr 127: load (126) :int
    instr 128: write (127)
    instr 129: wrl
    instr 130: wrl
    instr 131: param 11
    instr 132: call [17]
    instr 133: add res_base#32744 GP :int*
    instr 134: load (133) :int
    instr 135: write (134)
    instr 136: wrl
    instr 137: wrl
    instr 138: param 3
    instr 139: call [72]
    instr 140: wrl
    instr 141: ret 0
    instr 142: nop

