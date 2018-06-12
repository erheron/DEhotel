#!/bin/bash
#TODO
name=$USER
echo "Attempting to create database \"hotel\" as root, required sudo privilegies. You can crete it yourself"
#sudo su -l postgres -c "createdb hotel --owner='$name' --no-password" 2&> /dev/null


#checking all project requirements before start
echo "Checking if python3 is installed..."
python3installed=false
#TODO
echo "Seems like you have no Python3 installed, proceed and try to install?"

python3pipinstalled=false
echo "We will also need python3-pip package"
#TODO
echo "Installed properly"

echo "Installing third-party package 'Faker' from github:..."
#python3 -m pip install Faker
echo "Installed properly"

#main part
psql -d hotel < clear.sql
psql -d hotel < create.sql
python3 gen.py
