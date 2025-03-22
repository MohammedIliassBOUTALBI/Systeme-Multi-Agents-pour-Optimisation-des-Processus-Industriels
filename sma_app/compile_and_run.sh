#!/bin/bash

# Créer le répertoire bin s'il n'existe pas
mkdir -p bin

# Compiler tous les fichiers Java
echo "Compilation des fichiers Java..."
javac -cp "lib/jade.jar:." -d bin src/**/*.java src/*.java

# Exécuter le programme
echo "Lancement du programme..."
java -cp "bin:lib/jade.jar" Main
