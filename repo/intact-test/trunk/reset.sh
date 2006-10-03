#!/bin/sh
rm -rf target
mkdir target
cp temp/db-with-cvobjects.tar.gz target
cd target
tar xfz db-with-cvobjects.tar.gz
cd ..
rm target/db-with-cvobjects.tar.gz
