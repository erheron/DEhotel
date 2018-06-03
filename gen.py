import psycopg2
from pprint import pprint
from faker import Faker


#dbname = raw_input("Enter current database name: ")
#user = raw_input("Enter user: ")
#password = raw_input("Enter user password to connect: ")

#TODO: problem with standart input and connecting
try:
	conn = psycopg2.connect("dbname = 'erheron' user = 'erheron' password ='erheron'")
except:
	print('Unable to connect')

cur = conn.cursor()

fake = Faker()

fake.name()

fake.text()

class maker:
	sql_insert="""INSERT INTO {}({columns}) VALUES({val});"""
	
	def insert_names(self, table_name, col, k):
		for i in range(k):
			random_name, random_surname = fake.name().split();
			cur.execute(self.sql_insert.format(table_name, columns = col, val=str(i) + ",'" + random_name + "', '" + random_surname + "'"))

		conn.commit()

maker1 = maker()
#rows = conn.fetchall()
#for row in rows:
#	print row
