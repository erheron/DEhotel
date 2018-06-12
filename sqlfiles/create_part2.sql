
--Creating skeleton for future table
BEGIN;
DROP TABLE IF EXISTS pokoje;
CREATE TABLE pokoje(
	id_pokoj numeric(11) PRIMARY KEY,
	liczba_pokoji int,
	cena_podstawowa numeric(10,2),
	max_liczba_osob int
);

DROP TABLE IF EXISTS wyposazenie;
CREATE TABLE wyposazenie(
	id_wyposazenia SERIAL PRIMARY KEY,
	opis varchar(100),
	cena numeric(10,2) NOT NULL,
	data_zakupu date NOT NULL
);
DROP TABLE IF EXISTS pokoje_wyposazenie;
CREATE TABLE pokoje_(
id_pokoju numeric(11) REFERENCES pokoje,
id_wyposazenia serial REFERENCES wyposazenie
);
DROP TABLE IF EXISTS pracownicy;
CREATE TABLE pracownicy(
	id_pracownika serial PRIMARY KEY,
	imie varchar(20) NOT NULL,
	nazwisko varchar(20) NOT NULL,
	email varchar(50),
	numer_telefonu char(11),
	numer_konta numeric(16)
);
DROP TABLE IF EXISTS etaty;
CREATE TABLE etaty(
	id_etatu serial PRIMARY KEY,
	nazwa varchar(50) NOT NULL
);
DROP TABLE IF EXISTS pracownicy_etaty;
CREATE TABLE pracownicy_etaty(
	id_pracownika numeric(11) REFERENCES pracownicy.id_pracownika,
	id_etatu serial REFERENCES etaty
);
COMMIT;