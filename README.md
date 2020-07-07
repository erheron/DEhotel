# DEhotel
It's a semester project for Data Engineering course on TCS department, 2017/18 academic year. It includes simple database, dedicated to manage one hotel, and javafx application, providing user/admin interface along with registration form and other functionalities

## Prerequizites

You should have the following programs on your computer to use this app:

1. Python 2.7 
2. PostgreSQL installed
3. Java compiler version 9+ (build 53.0+)  
4. **Faker**: https://github.com/joke2k/faker, one simple Python package  
5. **psycopg2**, another Python package  

There is a dedicated script, which will check whether these requirements are satisfied, and install them if necessary, then launch the app (`init.sh`).



### Initialize database and run application (linux)


First, prepare a file named `db-connection.config` in main folder.
Fill it as in example:
```
jdbc:postgresql://localhost:5432/hotel # don't change this line, except You have postgresql on other port than 5432
user      # your user for psql
password  # password for psql
```

After You clone the repo, there should be file named `out/artifacts/DEhotelApp/DehotelApp.jar`. This file would be used in later script.

Run `./init.sh` in main folder. Application should start normally.
After that, in main folder there would be `app.jar` file, which You could run as jar file.



#### Authors
* **Vladyslav Rachek**
* **Katarzyna Król**
