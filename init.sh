#!/bin/bash
user=$USER
#echo "Attempting to create database \"hotel\" as root, required sudo privilegies. You can crete it yourself"
#sudo su -l postgres -c "createdb hotel --owner='$name' --no-password" 2&> /dev/null


#checking all project requirements before start
echo "Checking if python3 is installed..."
hash python3 2> /dev/null || { echo "Seems like you have no Python3 installed, aborting..."; exit 1; }

echo "We will also need pip3 package. Checking..."
hash pip3 2> /dev/null || { echo "No pip3 detected, aborting..."; exit 1; } 

#checking 'psycopg2'
echo "Checking ig 'psycopg2' is installed"
try=$(pip3 show faker)
if [ try != "" ]; then echo "Already installed" 
else 
	pip3 install psycopg2
	echo "Installed properly"
fi

#checking 'faker'
echo "Installing third-party package 'Faker' "
try=$(pip3 show faker)
if [ try != "" ]; then echo "Already installed" 
else 
	pip3 install Faker
	echo "Installed properly"
fi

#main part
psql -d hotel < sqlfiles/clear.sql
psql -d hotel < sqlfiles/create.sql
python3 generator/gen.py
