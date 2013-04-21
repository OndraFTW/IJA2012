IJA project 2012:

Petri net simulator
===================

Authors
-------

Vojtěch Šimša, xsimsa01@stud.fit.vutbr.cz
Ondřej Šlampa, xslamp01@stud.fit.vutbr.cz

Ant instuctions
---------------

ant compile - compiles project and generates documentation
ant clean - deletes files creted by ant compile
ant doc - generates documentation
ant server - starts server in background
ant client - starts client in foreground

Editor instructions
-------------------

a) Adding place:
Tokens are numbers separated by comma.

Examples (separated by new line):
1,2,3
1, 2, 3
4
-5, 6,+7, 8,-9


b) Adding transition:
First line of guard is a condition. Second line is assigment command.

Examples (separated by empty line):
a==3

a < 5 & b == c
d = 4

foo1>5 & foo2<0 & foo3!=0 & foo4>=foo5
bar = foo1+foo2+foo3+foo4+foo5


c)Adding arc:
Arc varname is a name of a variable.

Examples (separated by new line):
a
bar4
number_of_coins_in_fountaine

