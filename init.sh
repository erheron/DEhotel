#!/bin/bash
user=$USER
echo "Checking if database \"hotel\" already exists"
if psql -lqt | cut -d \| -f 1 | grep -qw hotel; then echo "OK, present"
else
	echo "Attempting to create database \"hotel\" as root, required sudo privilegies. You can also create it yourself"
	sudo su -l postgres -c "createdb hotel --owner='$name' --no-password" 2&> /dev/null
fi


#checking all project requirements before start
echo "Checking if python3 is installed..."
hash python3 2> /dev/null || { echo "Seems like you have no Python3 installed, aborting..."; exit 1; }

echo "We will also need pip3 package. Checking..."
hash pip3 2> /dev/null || { echo "No pip3 detected, aborting..."; exit 1; } 

#checking 'psycopg2'
echo "Checking ig 'psycopg2' is installed"
echo "" > log
pip3 show psycopg2 > log
if [ -s log ]; then echo "OK, installed" 
else 
	pip3 install psycopg2
	echo "Installed properly"
fi

echo "" > log
#checking 'faker'
echo "Installing third-party package 'Faker' "
pip3 show Faker > log
if [ -s log ]; then echo "OK, installed" 
else 
	pip3 install Faker
	echo "Installed properly"
fi

#main part
psql -d hotel < sqlfiles/clear.sql 2> /dev/null
psql -d hotel < sqlfiles/create.sql 2> /dev/null
python3 generator/gen.py
cp DEhotelApp/out/artifacts/DEhotelApp_jar/DEhotelApp.jar ./app.jar
echo "Assuming default JavaFX folder is /usr/share/openjfx..."
java --module-path /usr/share/openjfx/lib --add-modules=javafx.controls,javafx.fxml,javafx.base,javafx.media,javafx.web,javafx.swing -jar app.jar 2> /dev/null
