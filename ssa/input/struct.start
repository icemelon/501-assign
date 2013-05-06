    type B: q#4:int r#8:int s#12:int
    type A: x#4:int y#8:int z#12:B
    method main@2: i#-4:int c#-8:A qq#-12:B
    global a#32764:A
    global b#32760:List
    instr 1: nop
    instr 2: entrypc
    instr 3: enter 12
    instr 4: new A_type#16 :A
    instr 5: add a_base#32764 GP :A*
    instr 6: store (4) (5)
    instr 7: add a_base#32764 GP :A*
    instr 8: load (7) :A
    instr 9: checknull (8) :A
    instr 10: add (9) z_offset#12 :B*
    instr 11: new B_type#16 :B
    instr 12: store (11) (10)
    instr 13: new A_type#16 :A
    instr 14: move (13) c#-8
    instr 15: checknull c#-8 :A
    instr 16: add (15) z_offset#12 :B*
    instr 17: new B_type#16 :B
    instr 18: store (17) (16)
    instr 19: new B_type#16 :B
    instr 20: move (19) qq#-12
    instr 21: newlist 3 :List
    instr 22: add b_base#32760 GP :List*
    instr 23: store (21) (22)
    instr 24: move 0 i#-4
    instr 25: cmplt i#-4 3 :bool
    instr 26: blbc (25) [50]
    instr 27: add b_base#32760 GP :List*
    instr 28: load (27) :List
    instr 29: checknull (28) :List
    instr 30: checkbounds (29) i#-4
    instr 31: add (29) 8 :dynamic*
    instr 32: mul i#-4 4 :int
    instr 33: add (31) (32) :dynamic*
    instr 34: new A_type#16 :A
    instr 35: store (34) (33)
    instr 36: add b_base#32760 GP :List*
    instr 37: load (36) :List
    instr 38: checknull (37) :List
    instr 39: checkbounds (38) i#-4
    instr 40: add (38) 8 :dynamic*
    instr 41: mul i#-4 4 :int
    instr 42: add (40) (41) :dynamic*
    instr 43: load (42) :dynamic
    instr 44: checknull (43) :dynamic
    instr 45: new B_type#16 :B
    instr 46: stdynamic (45) (44) z_offset#?
    instr 47: add i#-4 1 :int
    instr 48: move (47) i#-4
    instr 49: br [25]
    instr 50: checknull c#-8 :A
    instr 51: add (50) z_offset#12 :B*
    instr 52: load (51) :B
    instr 53: checknull (52) :B
    instr 54: add (53) r_offset#8 :int*
    instr 55: store 987654321 (54)
    instr 56: add a_base#32764 GP :A*
    instr 57: load (56) :A
    instr 58: checknull (57) :A
    instr 59: add (58) x_offset#4 :int*
    instr 60: store 1 (59)
    instr 61: add a_base#32764 GP :A*
    instr 62: load (61) :A
    instr 63: checknull (62) :A
    instr 64: add (63) y_offset#8 :int*
    instr 65: store 2 (64)
    instr 66: checknull c#-8 :A
    instr 67: add (66) x_offset#4 :int*
    instr 68: store 9 (67)
    instr 69: checknull c#-8 :A
    instr 70: add (69) y_offset#8 :int*
    instr 71: store 0 (70)
    instr 72: add b_base#32760 GP :List*
    instr 73: load (72) :List
    instr 74: checknull (73) :List
    instr 75: checkbounds (74) 0
    instr 76: add (74) 8 :dynamic*
    instr 77: mul 0 4 :int
    instr 78: add (76) (77) :dynamic*
    instr 79: load (78) :dynamic
    instr 80: checknull (79) :dynamic
    instr 81: new Integer_type#8 :Integer
    instr 82: add (81) value_offset#4 :int*
    instr 83: store 3 (82)
    instr 84: stdynamic (81) (80) x_offset#?
    instr 85: add b_base#32760 GP :List*
    instr 86: load (85) :List
    instr 87: checknull (86) :List
    instr 88: checkbounds (87) 0
    instr 89: add (87) 8 :dynamic*
    instr 90: mul 0 4 :int
    instr 91: add (89) (90) :dynamic*
    instr 92: load (91) :dynamic
    instr 93: checknull (92) :dynamic
    instr 94: new Integer_type#8 :Integer
    instr 95: add (94) value_offset#4 :int*
    instr 96: store 4 (95)
    instr 97: stdynamic (94) (93) y_offset#?
    instr 98: add a_base#32764 GP :A*
    instr 99: load (98) :A
    instr 100: checknull (99) :A
    instr 101: add (100) x_offset#4 :int*
    instr 102: load (101) :int
    instr 103: add b_base#32760 GP :List*
    instr 104: load (103) :List
    instr 105: checknull (104) :List
    instr 106: checkbounds (105) (102)
    instr 107: add (105) 8 :dynamic*
    instr 108: mul (102) 4 :int
    instr 109: add (107) (108) :dynamic*
    instr 110: load (109) :dynamic
    instr 111: checknull (110) :dynamic
    instr 112: new Integer_type#8 :Integer
    instr 113: add (112) value_offset#4 :int*
    instr 114: store 5 (113)
    instr 115: stdynamic (112) (111) x_offset#?
    instr 116: add a_base#32764 GP :A*
    instr 117: load (116) :A
    instr 118: checknull (117) :A
    instr 119: add (118) x_offset#4 :int*
    instr 120: load (119) :int
    instr 121: add b_base#32760 GP :List*
    instr 122: load (121) :List
    instr 123: checknull (122) :List
    instr 124: checkbounds (123) (120)
    instr 125: add (123) 8 :dynamic*
    instr 126: mul (120) 4 :int
    instr 127: add (125) (126) :dynamic*
    instr 128: load (127) :dynamic
    instr 129: checknull (128) :dynamic
    instr 130: new Integer_type#8 :Integer
    instr 131: add (130) value_offset#4 :int*
    instr 132: store 6 (131)
    instr 133: stdynamic (130) (129) y_offset#?
    instr 134: add a_base#32764 GP :A*
    instr 135: load (134) :A
    instr 136: checknull (135) :A
    instr 137: add (136) x_offset#4 :int*
    instr 138: load (137) :int
    instr 139: sub (138) 1 :int
    instr 140: add b_base#32760 GP :List*
    instr 141: load (140) :List
    instr 142: checknull (141) :List
    instr 143: checkbounds (142) (139)
    instr 144: add (142) 8 :dynamic*
    instr 145: mul (139) 4 :int
    instr 146: add (144) (145) :dynamic*
    instr 147: load (146) :dynamic
    instr 148: checknull (147) :dynamic
    instr 149: lddynamic (148) x_offset#? :dynamic
    instr 150: checknull (149) :dynamic
    instr 151: checktype (150) Integer_type#8 :Integer
    instr 152: add (151) value_offset#4 :int*
    instr 153: load (152) :int
    instr 154: sub (153) 1 :int
    instr 155: add b_base#32760 GP :List*
    instr 156: load (155) :List
    instr 157: checknull (156) :List
    instr 158: checkbounds (157) (154)
    instr 159: add (157) 8 :dynamic*
    instr 160: mul (154) 4 :int
    instr 161: add (159) (160) :dynamic*
    instr 162: load (161) :dynamic
    instr 163: checknull (162) :dynamic
    instr 164: new Integer_type#8 :Integer
    instr 165: add (164) value_offset#4 :int*
    instr 166: store 7 (165)
    instr 167: stdynamic (164) (163) x_offset#?
    instr 168: add a_base#32764 GP :A*
    instr 169: load (168) :A
    instr 170: checknull (169) :A
    instr 171: add (170) y_offset#8 :int*
    instr 172: load (171) :int
    instr 173: sub (172) 2 :int
    instr 174: add b_base#32760 GP :List*
    instr 175: load (174) :List
    instr 176: checknull (175) :List
    instr 177: checkbounds (176) (173)
    instr 178: add (176) 8 :dynamic*
    instr 179: mul (173) 4 :int
    instr 180: add (178) (179) :dynamic*
    instr 181: load (180) :dynamic
    instr 182: checknull (181) :dynamic
    instr 183: lddynamic (182) x_offset#? :dynamic
    instr 184: checknull (183) :dynamic
    instr 185: checktype (184) Integer_type#8 :Integer
    instr 186: add (185) value_offset#4 :int*
    instr 187: load (186) :int
    instr 188: sub (187) 1 :int
    instr 189: add b_base#32760 GP :List*
    instr 190: load (189) :List
    instr 191: checknull (190) :List
    instr 192: checkbounds (191) (188)
    instr 193: add (191) 8 :dynamic*
    instr 194: mul (188) 4 :int
    instr 195: add (193) (194) :dynamic*
    instr 196: load (195) :dynamic
    instr 197: checknull (196) :dynamic
    instr 198: new Integer_type#8 :Integer
    instr 199: add (198) value_offset#4 :int*
    instr 200: store 8 (199)
    instr 201: stdynamic (198) (197) y_offset#?
    instr 202: add a_base#32764 GP :A*
    instr 203: load (202) :A
    instr 204: checknull (203) :A
    instr 205: add (204) x_offset#4 :int*
    instr 206: load (205) :int
    instr 207: write (206)
    instr 208: add a_base#32764 GP :A*
    instr 209: load (208) :A
    instr 210: checknull (209) :A
    instr 211: add (210) y_offset#8 :int*
    instr 212: load (211) :int
    instr 213: write (212)
    instr 214: wrl
    instr 215: move 0 i#-4
    instr 216: cmplt i#-4 3 :bool
    instr 217: blbc (216) [252]
    instr 218: add b_base#32760 GP :List*
    instr 219: load (218) :List
    instr 220: checknull (219) :List
    instr 221: checkbounds (220) i#-4
    instr 222: add (220) 8 :dynamic*
    instr 223: mul i#-4 4 :int
    instr 224: add (222) (223) :dynamic*
    instr 225: load (224) :dynamic
    instr 226: checknull (225) :dynamic
    instr 227: lddynamic (226) x_offset#? :dynamic
    instr 228: checknull (227) :dynamic
    instr 229: checktype (228) Integer_type#8 :Integer
    instr 230: add (229) value_offset#4 :int*
    instr 231: load (230) :int
    instr 232: write (231)
    instr 233: add b_base#32760 GP :List*
    instr 234: load (233) :List
    instr 235: checknull (234) :List
    instr 236: checkbounds (235) i#-4
    instr 237: add (235) 8 :dynamic*
    instr 238: mul i#-4 4 :int
    instr 239: add (237) (238) :dynamic*
    instr 240: load (239) :dynamic
    instr 241: checknull (240) :dynamic
    instr 242: lddynamic (241) y_offset#? :dynamic
    instr 243: checknull (242) :dynamic
    instr 244: checktype (243) Integer_type#8 :Integer
    instr 245: add (244) value_offset#4 :int*
    instr 246: load (245) :int
    instr 247: write (246)
    instr 248: wrl
    instr 249: add i#-4 1 :int
    instr 250: move (249) i#-4
    instr 251: br [216]
    instr 252: checknull c#-8 :A
    instr 253: add (252) x_offset#4 :int*
    instr 254: load (253) :int
    instr 255: write (254)
    instr 256: checknull c#-8 :A
    instr 257: add (256) y_offset#8 :int*
    instr 258: load (257) :int
    instr 259: write (258)
    instr 260: wrl
    instr 261: checknull c#-8 :A
    instr 262: add (261) z_offset#12 :B*
    instr 263: load (262) :B
    instr 264: checknull (263) :B
    instr 265: add (264) r_offset#8 :int*
    instr 266: load (265) :int
    instr 267: write (266)
    instr 268: wrl
    instr 269: ret 0
    instr 270: nop

