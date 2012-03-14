@('a', 'b', 'c', 'd')
@('a', 'b', 'pHEAR', 'd')
@('a', 'b', 'd')
@('a', 'b', '???', 'd')
0 => a
1 => b
2 => ???
3 => d
@('b', '???', 'd')
@('???', 'd')
@('a', '???', 'd')
@('???', 'd', 'phearz')
@('a', '???', 'd', 'phearz')
@('a', 'b', 'c', 'd', 'e', 'f', 'g', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p')
@('f', 'g', 'i')
@('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p')
@('f', 'g', 'h')
Warning: unsafe data modification: parent @array changed after &sublist creation at listops2.sl:42
