#!/bin/bash
name=$USER
sudo su -l postgres -c "createdb hotel --owner='$name' --no-password" 2&> /dev/null
exit
psql -c "\c hotel"
psql -d "hotel < create.sql"

