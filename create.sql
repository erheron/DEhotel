BEGIN;
create table pokoje(
	id_pokoju serial not null primary key,
	typ varchar(20) not null,
	cena_podstawowa numeric not null,
	max_liczba_osob numeric(2) not null
);
/*create table email_hash(
email varchar(100) primary key,
	hash numeric(10),
);*/
create table goscie(
	id_goscia serial not null primary key,
	imie varchar not null,
	nazwisko varchar not null,
	nr_tel char(9) not null,
	email varchar(100),
	check (email like '%@%')
);

create table rezerwacje_goscie(
	id_rez_zbiorczej serial not null primary key,
	id_goscia integer not null references goscie
);

create table rezerwacje_pokoje(
	id_rez_zbiorczej integer references rezerwacje_goscie,
	id_rez_pojedynczej serial not null primary key,
	id_pokoju integer not null references pokoje,
	data_od date not null default current_date check (data_od>=current_date),
	data_do date not null default current_date + interval '1 day' check (data_od<data_do),
	cena numeric not null,
	typ_platnosci char(1) check (typ_platnosci='G' or typ_platnosci='P') not null, -- G-gotwka, P-przelew
	plan_liczba_osob numeric,
	anulowane_data date default null
);

create table rodzaje_wyposazenia(
  id_rodzaju_wyposazenia serial primary key,
	nazwa varchar not null,
	cena_przedmiotu numeric not null,
	liczba_przedmiotow numeric not null
);

create table wyposazenie(
	id serial not null primary key,
  id_rodzaju integer references rodzaje_wyposazenia
	--nazwa varchar not null references rodzaje_wyposazenia
);

create table pokoje_wyposazenie(
	id_pokoju integer not null references pokoje,
	id_wyposazenia integer not null references wyposazenie
);

create table platnosci(
	id_platnosci serial not null primary key,
	id_rez_zbiorczej integer references rezerwacje_goscie,
	data_platnosci date not null default current_date check (data_platnosci<=current_date),
	kwota numeric not null
);

create table kary(
	id_kary serial not null primary key,
	id_rez_zbiorczej integer references rezerwacje_goscie,
	data_kary  date not null default current_date check (data_kary<=current_date),
	kwota numeric not null
);

create table uslugi_dod(
	id_uslugi_dod serial not null primary key,
	nazwa varchar(20) not null,
	cena numeric not null
);

create table usl_rez(
	id_uslugi_dod integer not null references uslugi_dod,
	id_rez_pojedynczej integer references rezerwacje_pokoje,
	liczba numeric not null default 1,
	data_od date not null,
	data_do date not null,
	data_anulowania date default null
);
COMMIT;

