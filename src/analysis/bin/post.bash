#!/usr/bin/env bash

# get build filename
FINALNAME=$(cat target/classes/de/flashpixx/rrd_antlr4/configuration.properties | grep -i finalname)
IFS='='
read -ra PARTS <<< "$FINALNAME"
BIN=$(echo ${PARTS[1]}".jar")



# moving data
git checkout master
mv target/site /tmp
mv target/$BIN /tmp


# Executable Build with Update Binary Branch
git checkout binary-master
mv -f circle.yml /tmp
mv -f .gitignore /tmp
git checkout master
git push origin :binary-master
git branch -D binary-master
git checkout --orphan binary-master
rm -Rf *
rm -f .bowerrc
mv -f /tmp/.gitignore .
mv -f /tmp/circle.yml .
mv /tmp/$BIN .
echo "# Railroad Diagram Generator for ANTLR 4 - Standalone Binary" > readme.md
git add --all .
git commit -m "binaries master branch"
git push origin binary-master


# Documentation Build with Update to GH-Pages Branch
git checkout gh-pages
mv -f circle.yml /tmp
mv -f .gitignore /tmp
git checkout master
git push origin :gh-pages
git branch -D gh-pages
git checkout --orphan gh-pages
rm -Rf *
rm -f .bowerrc
mv -f /tmp/.gitignore .
mv -f /tmp/circle.yml .
mv /tmp/site/* .
echo "# Railroad Diagram Generator for ANTLR 4 - Documentation" > readme.md
git add --all .
git commit -m "current documentation"
git push origin gh-pages
