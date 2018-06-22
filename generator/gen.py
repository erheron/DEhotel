#changed Python to python3(default 3.5.2 on my laptop)
# -*- coding: utf-8 -*-

#from __future__ import unicode_literals
#import json
import psycopg2
import datetime
import time
import calendar
import random
import decimal
from pprint import pprint
from faker import Faker
from random import randint
from datetime import timedelta

#to generate random phone number
def random_with_N_digits(n):
    range_start = 10**(n-1)
    range_end = (10**n)-1
    return randint(range_start, range_end)

dbname="hotel"
user = input("Enter user: ")
password = input("Enter user password to connect: ")

try:
	conn = psycopg2.connect(dbname=dbname, user=user, password=password)
except:
	print('Unable to connect')

cur = conn.cursor()

f_pl = Faker('pl_PL')
f_us = Faker()

class maker:
	sql_insert="""INSERT INTO {}({columns}) VALUES({val});"""
	
	def __init__(self):
		self.total_email_set=set()

	def hash_email(self, email):
		if (email in self.total_email_set):
			return -1
		self.total_email_set.add(email)
		h=0
		p=31
		P=1
		mod=1000000009
		for i in email:
			h = h + (ord(i) * P)
			P *= p
			h = h % mod
			P = P % mod
		return h
			
	def insert_names(self, table_name, col, k, faker):
		for i in range(k):
			random_name, random_surname = faker.first_name(), faker.last_name();
			
			#generating pseudo-random email
			email= faker.email()
			hash_ = self.hash_email(email)
			if(hash_ == -1): 
				continue
			tel_number='0' + str(random_with_N_digits(8))
			cur.execute(self.sql_insert.format(table_name, columns = col, val="'" + random_name + "', '" + random_surname + "', '" + tel_number + "', '"
			+ email + "', " + str(hash_)))

		conn.commit()

	def insert_rooms_static(self):
#insert all rooms that our hotel have
#prices was taken as some kind of average (in zł) on 2018/06/07 in Kraków
		for i in range(17):
		  cur.execute(self.sql_insert.format('pokoje', columns = 'cena_podstawowa, max_liczba_osob, typ', val="140.00, 1,\'single standard\'"))	
		for i in range(8):
			cur.execute(self.sql_insert.format('pokoje', columns = 'cena_podstawowa, max_liczba_osob, typ', val="231.00, 1,\'single superior\'"))	
		for i in range(4):
			cur.execute(self.sql_insert.format('pokoje', columns = 'cena_podstawowa, max_liczba_osob, typ', val="300.00, 1,\'single deluxe\'"))	

#twin - dwa pojedynczych lozka
		for i in range(10):
			cur.execute(self.sql_insert.format('pokoje', columns = 'cena_podstawowa, max_liczba_osob, typ', val="298.00, 2,\'twin standard\'"))	
		for i in range(8):
			cur.execute(self.sql_insert.format('pokoje', columns = 'cena_podstawowa, max_liczba_osob, typ', val="410.00, 2,\'twin superior\'"))	
		for i in range(3):
			cur.execute(self.sql_insert.format('pokoje', columns = 'cena_podstawowa, max_liczba_osob, typ', val="600.00, 2,\'twin deluxe\'"))	

#double - pokoje z jednym duzym lozkiem
		for i in range(10):
			cur.execute(self.sql_insert.format('pokoje', columns = 'cena_podstawowa, max_liczba_osob, typ', val="298.00, 2,\'double standard\'"))	
		for i in range(9):
			cur.execute(self.sql_insert.format('pokoje', columns = 'cena_podstawowa, max_liczba_osob, typ', val="410.00, 2,\'double superior\'"))	
		for i in range(3):
			cur.execute(self.sql_insert.format('pokoje', columns = 'cena_podstawowa, max_liczba_osob, typ', val="600.00, 2,\'double deluxe\'"))	

		for i in range(8):
			cur.execute(self.sql_insert.format('pokoje', columns = 'cena_podstawowa, max_liczba_osob, typ', val="382.00, 3,\'triple standard\'"))	
		for i in range(3):
			cur.execute(self.sql_insert.format('pokoje', columns = 'cena_podstawowa, max_liczba_osob, typ', val="500.00, 3,\'triple superior\'"))	
		for i in range(2):
			cur.execute(self.sql_insert.format('pokoje', columns = 'cena_podstawowa, max_liczba_osob, typ', val="800.00, 3,\'triple deluxe\'"))	

		conn.commit()
			
	
	def insert_equipment_categories_static(self):
#prices and equipment were taken from original site: www.ikea.krakow.pl
#these chairs - for standard rooms, amount - 81 + 2 in reserve
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val='\'Krzeslo IKEA KAUSTBY\', 149.00, 83'))
#these chairs - for superior/deluxe rooms, amount as follows from total room size (times amount of persons in each room) - that is, 73 + 5 in reserve
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val='\'Krzeslo IKEA INGOLF\', 199.00, 81'))

#these armchairs - for deluxe rooms, amount -  22 + 0 in reserve
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Fotel IKEA STRADMON\', 799.00, 22"))

#these tables - for standard rooms, amount -  45 + 0 in reserve
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Stol IKEA LINNMON/ADILS\', 79.00, 45"))
#these tables - for superior rooms, amount -  28 + 0 in reserve
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Stol IKEA INGO\', 199.00, 28"))
#these tables - for deluxe rooms, amount -  12 + 0 in reserve
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Stol IKEA BJURSTA\', 499.00, 12"))

		cur.execute(self.sql_insert.format('rodzaje_wyposazenia',columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow',val="\'Lampa biurkowa IKEA TERTIAL\',39.00,45"))
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia',columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow',val="\'Lampa biurkowa IKEA FORSA\',59.99,28"))
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia',columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow',val="\'Lampa biurkowa IKEA HEKTAR\',179.00,12"))

		cur.execute(self.sql_insert.format('rodzaje_wyposazenia',columns='nazwa, cena_przedmiotu, liczba_przedmiotow',val="\'Lampa IKEA HEKTAR\', 109.00, 73"))
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia',columns='nazwa, cena_przedmiotu, liczba_przedmiotow',val="\'Lampa IKEA ARSTID\', 209.00, 12"))
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia',columns='nazwa, cena_przedmiotu, liczba_przedmiotow',val="\'Lampa podlogowa IKEA ARSTID\', 149.00, 12"))

		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Sofa IKEA ANGSTA\', 899.00, 4"))
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Sofa IKEA FRIHETEN\', 1499.00, 6"))
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Sofa IKEA VILASUND\', 2499.00, 3"))

		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Lozko IKEA UTAKER\', 500.00, 37"))
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Lozko IKEA OTEREN\', 899.00, 24"))
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Lozko IKEA MALM\', 1039.00, 10"))


		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Lozko podw. IKEA LAUVIK\', 1565.00,10"))
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Lozko podw. IKEA GVARV\', 1999.00, 9"))
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Lozko podw. IKEA DUNVIK\', 3473.00, 3"))

#to standard except triple
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Szafa IKEA BRIMNES\', 699.00, 37"))
#to superior except those triple
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Szafa IKEA HEMNES\', 899.00, 25"))
#to triple rooms and deluxe
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Szafa IKEA PAX 3m\', 2155.00, 2"))
#to triple standard and 2/1deluxe, and triple superior
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Szafa IKEA PAX 2.5m\', 1370.00, 21"))

		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Przescieradlo IKEA ULLVIDE\', 59.00, 81"))
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Przescieradlo IKEA SOMNTUTA\', 90.00, 51"))
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Przescieradlo IKEA NATTJASMIN\', 129.00, 22"))
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Poduszka IKEA AXAG\', 10.00, 81"))
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Poduszka IKEA HAMPDAN\', 40.00, 51"))
		cur.execute(self.sql_insert.format('rodzaje_wyposazenia', columns = 'nazwa, cena_przedmiotu, liczba_przedmiotow', val="\'Poduszka IKEA HYLLE\', 50.00, 22"))

		conn.commit()
		
	def fill_given_room_equipment(self, room_type, equipment_names_count, amount_of_people):
		cur.execute("SELECT id_pokoju FROM pokoje WHERE typ like %s AND max_liczba_osob = %s;", (room_type, amount_of_people))
		rooms=cur.fetchall()
		rlen=len(rooms)
		#for every room which matched to our criteria do the following:
		#go through list ob objects, and (depending on value of 'cnt') insert as much info into 'wyposazenie' as we need
		#next match last inserted object with current room
		cur.execute('SELECT COALESCE(MAX(id),0) FROM wyposazenie;')
		maxid=cur.fetchall()
		maxid=maxid[0][0]	
		for roomset in rooms:
			room=roomset[0]
			for eq in equipment_names_count:#list of pairs
				cnt=eq[1]
				eqname=eq[0]
				cur.execute("SELECT id_rodzaju_wyposazenia FROM rodzaje_wyposazenia WHERE nazwa = %s;", (eqname,))
				fkey=cur.fetchall()
				fkey=fkey[0]
				for i in range(cnt):
					cur.execute("INSERT INTO wyposazenie (id_rodzaju) VALUES (%s);",(fkey,))
					maxid = maxid + 1
					cur.execute("INSERT INTO pokoje_wyposazenie VALUES(%s, %s);", (room, maxid))
		conn.commit()

	def fill_standard_rooms(self):
		eq_names=[['Krzeslo IKEA KAUSTBY', 1] , ['Lampa IKEA HEKTAR', 1], ['Lampa biurkowa IKEA TERTIAL', 1], ['Stol IKEA LINNMON/ADILS', 1]]
		self.fill_given_room_equipment("%standard", eq_names, 1)
		eq_names=[['Krzeslo IKEA KAUSTBY', 2] , ['Lampa IKEA HEKTAR', 1], ['Lampa biurkowa IKEA TERTIAL', 1], ['Stol IKEA LINNMON/ADILS', 1]]
		self.fill_given_room_equipment("%standard", eq_names, 2)
		eq_names=[['Krzeslo IKEA KAUSTBY', 3] , ['Lampa IKEA HEKTAR', 1], ['Lampa biurkowa IKEA TERTIAL', 1], ['Stol IKEA LINNMON/ADILS', 1]]
		self.fill_given_room_equipment("%standard", eq_names, 3)
	
	def fill_superior_rooms(self):
		eq_names=[['Krzeslo IKEA INGOLF', 1], ['Lampa IKEA HEKTAR', 1], ['Lampa biurkowa IKEA FORSA', 1], ['Stol IKEA INGO', 1]]
		self.fill_given_room_equipment("%superior", eq_names, 1)
		eq_names=[['Krzeslo IKEA INGOLF', 2], ['Lampa IKEA HEKTAR', 1], ['Lampa biurkowa IKEA FORSA', 1], ['Stol IKEA INGO', 1]]
		self.fill_given_room_equipment("%superior", eq_names, 2)
		eq_names=[['Krzeslo IKEA INGOLF', 3], ['Lampa IKEA HEKTAR', 1], ['Lampa biurkowa IKEA FORSA', 1], ['Stol IKEA INGO', 1]]
		self.fill_given_room_equipment("%superior", eq_names, 3)
			
	def fill_deluxe_rooms(self):
		eq_names=[['Krzeslo IKEA INGOLF', 1], ['Fotel IKEA STRADMON', 1], ['Lampa IKEA ARSTID', 1], ['Lampa biurkowa IKEA HEKTAR', 1], 
		['Stol IKEA BJURSTA', 1],['Lampa podlogowa IKEA ARSTID', 1],['Sofa IKEA ANGSTA', 1]]
		self.fill_given_room_equipment("%deluxe", eq_names, 1)
		eq_names=[['Krzeslo IKEA INGOLF', 2], ['Fotel IKEA STRADMON', 2], ['Lampa IKEA ARSTID', 1], ['Lampa biurkowa IKEA HEKTAR', 1], 
		['Stol IKEA BJURSTA', 1],['Lampa podlogowa IKEA ARSTID', 1], ['Sofa IKEA FRIHETEN', 1]]
		self.fill_given_room_equipment("%deluxe", eq_names, 2)
		eq_names=[['Krzeslo IKEA INGOLF', 3], ['Fotel IKEA STRADMON', 3], ['Lampa IKEA ARSTID', 1], ['Lampa biurkowa IKEA HEKTAR', 1], 
		['Stol IKEA BJURSTA', 1], ['Sofa IKEA VILASUND', 1],['Lampa podlogowa IKEA ARSTID', 1]]
		self.fill_given_room_equipment("%deluxe", eq_names, 3)

	def fill_bed_all_rooms(self):
		eq_names=[['Lozko IKEA UTAKER', 1], ['Przescieradlo IKEA ULLVIDE', 1], ['Poduszka IKEA AXAG', 1]]
		self.fill_given_room_equipment("%standard", eq_names, 1)
		eq_names[0][1]=2
		eq_names[1][1]=2
		eq_names[2][1]=2
		self.fill_given_room_equipment("twin standard", eq_names, 2)
		eq_names[0][1]=3
		eq_names[1][1]=3
		eq_names[2][1]=3
		self.fill_given_room_equipment("%standard", eq_names, 3)
		eq_names=[['Lozko IKEA OTEREN', 1], ['Przescieradlo IKEA SOMNTUTA', 1], ['Poduszka IKEA HAMPDAN', 1]]
		self.fill_given_room_equipment("%superior", eq_names, 1)
		eq_names[0][1]=2
		eq_names[1][1]=2
		eq_names[2][1]=2
		self.fill_given_room_equipment("twin superior", eq_names, 2)
		eq_names=[['Lozko IKEA MALM', 1], ['Przescieradlo IKEA NATTJASMIN', 1], ['Poduszka IKEA HYLLE', 1]]
		self.fill_given_room_equipment('%deluxe', eq_names, 1)
		eq_names[0][1]=2
		eq_names[1][1]=2
		eq_names[2][1]=2
		self.fill_given_room_equipment('twin deluxe', eq_names, 2)
		eq_names[0][1]=3
		eq_names[1][1]=3
		eq_names[2][1]=3
		self.fill_given_room_equipment('%deluxe', eq_names, 3)
		eq_names=[['Lozko podw. IKEA LAUVIK', 1], ['Przescieradlo IKEA ULLVIDE', 1, ['Poduszka IKEA AXAG', 2]]]
		self.fill_given_room_equipment('double standard', eq_names, 2)
		eq_names=[['Lozko podw. IKEA GVARV', 1], ['Przescieradlo IKEA SOMNTUTA', 1], ['Poduszka IKEA HAMPDAN', 2]]
		self.fill_given_room_equipment('double superior', eq_names, 2)
		eq_names=[['Lozko podw. IKEA DUNVIK', 1], ['Przescieradlo IKEA NATTJASMIN', 1], ['Poduszka IKEA HYLLE', 2]]
		self.fill_given_room_equipment('double deluxe', eq_names, 2)


	def fill_equipment_join_with_rooms(self):
		self.fill_standard_rooms()
		self.fill_superior_rooms()
		self.fill_deluxe_rooms()
		self.fill_bed_all_rooms()
	def add_some_random_reservations(self):
		random.seed(a=None, version=2)
		cur.execute('SELECT COALESCE(MAX(id_rez_zbiorczej),0) FROM rezerwacje_goscie;')
		rows = cur.fetchall()
		res_id = rows[0][0]
		for i in range(100):
			res_id = res_id + 1
			guest=random.randint(1, 200)
			cur.execute('INSERT INTO rezerwacje_goscie(id_goscia) VALUES (%s);', (guest,))
			date_from=f_us.date_between(start_date="today", end_date="+30d")
			date_to=date_from + timedelta(random.randint(1,10))
			timeDiff=(date_to - date_from)/timedelta(days=1)
			date_from=date_from.isoformat()
			date_to=date_to.isoformat()
			people=random.randint(1,3)
			cur.execute('SELECT MIN(id_pokoju) FROM pokoje p1 WHERE max_liczba_osob = %s AND NOT EXISTS (SELECT * FROM rezerwacje_pokoje rp WHERE rp.id_pokoju = p1.id_pokoju AND NOT(rp.data_od >= %s::date OR rp.data_do <= %s::date));',(people, date_to, date_from)) 
			rows=cur.fetchall()
			room_id=rows[0][0]
			cur.execute('SELECT MIN(cena_podstawowa) FROM pokoje WHERE id_pokoju = %s;',(room_id,));
			rows=cur.fetchall()
			price=rows[0][0]
			print('price podstawowa' + str(price))
			price=price*(decimal.Decimal(timeDiff))
			print('date_from ' + date_from + '  date_to ' + date_to + ' price:  ' + str(price))
			cur.execute('SELECT oblicz_znizke(%s, %s, %s);', (guest, price, date_from))
			rows=cur.fetchall()
			price=rows[0][0]
			print("price after   " + str(price))
			cur.execute('INSERT INTO rezerwacje_pokoje(id_rez_zbiorczej, id_pokoju, data_od, data_do, cena,  plan_liczba_osob) VALUES (%s, %s, %s, %s, %s, %s);',
			(res_id, room_id, date_from, date_to, price, people))
			conn.commit()


#filling tables
mainMaker = maker()


print("Filling guests...........")
mainMaker.insert_names('goscie', 'imie, nazwisko, nr_tel, email, hash',100, f_pl) 
mainMaker.insert_names('goscie', 'imie, nazwisko, nr_tel, email, hash',100, f_us) 
print(">Done")

print("Filling rooms............")
mainMaker.insert_rooms_static()
print(">Done")

print("Filling equipment..............")
mainMaker.insert_equipment_categories_static()
print(">Done")

print("Joining equipment with rooms............")
mainMaker.fill_equipment_join_with_rooms()
print(">Done")

print("And some 'random' reservations.....")
mainMaker.add_some_random_reservations()
print(">Done")
print("Have fun :)")
