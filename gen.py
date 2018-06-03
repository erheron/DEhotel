import psycopg2
from pprint import pprint
from faker import Faker
from random import randint

#to generate random phone number
def random_with_N_digits(n):
    range_start = 10**(n-1)
    range_end = (10**n)-1
    return randint(range_start, range_end)



#dbname = raw_input("Enter current database name: ")
#user = raw_input("Enter user: ")
#password = raw_input("Enter user password to connect: ")

#TODO: problem with standart input and connecting
try:
	conn = psycopg2.connect("dbname = 'hotel' user = 'erheron' password ='erheron'")
except:
	print('Unable to connect')

cur = conn.cursor()

f_pl = Faker('pl_PL')
f_us = Faker()

class maker:
	sql_insert="""INSERT INTO {}({columns}) VALUES({val});"""
	
	def insert_names(self, table_name, col, k, faker):
		for i in range(k):
			random_name, random_surname = faker.first_name(), faker.last_name();
			#print random_name, random_surname
			
			#generating pseudo-random email
			email=faker.email()
			tel_number='0' + str(random_with_N_digits(8))
			cur.execute(self.sql_insert.format(table_name, columns = col, val="'" + random_name + "', '" + random_surname + "', '" + tel_number + "', '"
				+ email + "'"))

		conn.commit()

maker1 = maker()

maker1.insert_names('goscie', 'imie, nazwisko, nr_tel, email',10, f_pl) 
maker1.insert_names('goscie', 'imie, nazwisko, nr_tel, email',100, f_us) 
