#!/bin/bash

echo -e "Please rate the following 20 movies from 1.0 to 5.0. If you haven't heard of the movie, enter a 0.0\n"

read -p "Toy Story " a
read -p "Jumanji " b
read -p "Heat " c
read -p "GoldenEye " d
read -p "The American President " e
read -p "Nixon " f
read -p "Sense and Sensibility " g
read -p "Othello " h
read -p "Babe " i
read -p "Clueless " j
read -p "Mortal Kombat " k
read -p "Pochahontas " l
read -p "The Usual Suspects " m
read -p "Les Miserables " n
read -p "Apollo 13 " o
read -p "Batman Forever " p
read -p "Die Hard: With a Vengeance " q
read -p "Free Willy 2 " r
read -p "Mallrats " s
read -p "Pulp Fiction " t


echo -e "1,69879,"$a "\n2,69879,"$b "\n6,69879,"$c "\n10,69879,"$d "\n11,69879,"$e "\n14,69879,"$f "\n17,69879,"$g "\n26,69879,"$h "\n34,69879,"$i "\n39,69879,"$j "\n44,69879,"$k "\n48,69879,"$l "\n50,69879,"$m "\n73,69879,"$n "\n150,69879,"$o "\n153,69879,"$p "\n165,69879,"$q "\n169,69879,"$r "\n180,69879,"$s "\n296,69879,"$t >> data/movielens.data
