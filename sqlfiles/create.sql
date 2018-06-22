BEGIN;
--TABELE
create table pokoje(
	id_pokoju serial not null primary key,
	typ varchar(20) not null,
	cena_podstawowa numeric not null,
	max_liczba_osob numeric(2) not null,
	check (cena_podstawowa > 0),
	check ( typ = 'single standard' OR typ = 'single superior' OR typ = 'single deluxe' OR
			typ = 'double standard' OR typ = 'double superior' OR typ = 'double deluxe' OR
			typ = 'twin standard' OR typ = 'twin superior' OR typ = 'twin deluxe' OR
			typ = 'triple standard' OR typ = 'triple superior' OR typ = 'triple deluxe'),
	check (	(typ LIKE 'single%' AND max_liczba_osob = 1) OR
			(typ LIKE 'double%' AND max_liczba_osob = 2) OR
			(typ LIKE 'twin%'   AND max_liczba_osob = 2) OR	
			(typ LIKE 'triple%' AND max_liczba_osob = 3))
);
create table goscie(
	id_goscia serial not null primary key,
	imie varchar not null,
	nazwisko varchar not null,
	nr_tel char(9) not null unique,
	email varchar(100) not null unique,
	check (email like '%@%'),
	hash numeric(10)
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
	check (cena > 0),
	plan_liczba_osob numeric,
	check (plan_liczba_osob >0),
	anulowane_data date default null,
	check (anulowane_data >= current_date)
);
create table rodzaje_wyposazenia(
 	id_rodzaju_wyposazenia serial primary key,
	nazwa varchar not null,
	cena_przedmiotu numeric not null,
	check (cena_przedmiotu > 0),
	liczba_przedmiotow numeric not null
	check (liczba_przedmiotow >= 0)
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
create table kary(
	id_kary serial not null primary key,
	id_rez_zbiorczej integer references rezerwacje_goscie,
	data_kary  date not null default current_date check (data_kary<=current_date),
	kwota numeric not null,
	check (kwota>0)
);
create table uslugi_dod(
	id_uslugi_dod serial not null primary key,
	nazwa varchar(30) not null,
	cena numeric not null,
	check (cena>0)
);
create table usl_rez(
	id_uslugi_dod integer not null references uslugi_dod,
	id_rez_pojedynczej integer references rezerwacje_pokoje,
	liczba numeric not null default 1,
	data_od date not null,
	data_do date not null,
	check (data_od<data_do),
	data_anulowania date default null
);
--TABELE, KONIEC

--FUNKCJE, TRIGGERY
--oblicza znizke na podstawie liczby dni spedzonych w hotelu w poprzednim roku
--[7,30) dni - 1% znizki
--[30,...) dni 5% znizki
--znizka jest obliczana tylko dla ceny pokoju
create or replace function oblicz_znizke(id integer, cena numeric, od_data date)
	returns numeric as
$$
declare
	suma_dni numeric;
begin
	suma_dni = coalesce((select sum(data_do - data_od)
			from  rezerwacje_pokoje natural join rezerwacje_goscie
			where id = id_goscia and data_od >= (od_data - interval '365 days')),0);
	if suma_dni < 7 then
		return round(cena,2);
	elsif suma_dni >=7 and suma_dni < 30 then
		return round(cena*0.99,2);
	else
		return round(cena*0.95,2);
	end if;
end;
$$
language 'plpgsql';

--dodaje karę w wysokośći 10% całkowitej ceny rezerwacji pokoju, jeśli anulowanie odbywa się "dzień przed"
create or replace function dodaj_kare()
    returns trigger as $dodaj_kare$
begin
    if(new.id_rez_zbiorczej <> old.id_rez_zbiorczej or new.id_rez_pojedynczej <> old.id_rez_pojedynczej or new.id_pokoju <> old.id_pokoju or new.data_od<>old.data_od or new.data_do <> old.data_do or new.plan_liczba_osob <> old.plan_liczba_osob) then
	raise exception 'nie mozna edytowac rezerwacji';
    --TODO = update ceny tylko z triggera, da sie to sprawdzic?
    elsif old.anulowane_data is not null then
		raise exception 'rezerwacja zostala juz anulowana';
    elsif new.anulowane_data is not null and new.anulowane_data > old.data_od then
		raise exception 'za pozno na rezygnacje z rezerwacji';
    elsif new.anulowane_data is not null and old.data_od <= new.anulowane_data + 1 then
        insert into kary values (default, old.id_rez_zbiorczej, current_date, old.cena*0.10);
	raise notice 'dodano oplate za anulowanie rezerwacji';
    end if;
    return new;

end;
$dodaj_kare$
language 'plpgsql';

create trigger dodaj_kare before update on rezerwacje_pokoje
for each row execute procedure dodaj_kare();

--usuwa zniszczony sprzet i dodaje do pokoju nowy
create or replace function usun_sprzet(id_sprzetu integer)
    returns void as
$$
declare
    pokoj numeric;
    sprzet integer;
begin
    pokoj = (select id_pokoju from pokoje_wyposazenie where id_wyposazenia = id_sprzetu);
    delete from pokoje_wyposazenie where id_wyposazenia = id_sprzetu;
    update wyposazenie set id = default where id=id_sprzetu;
    sprzet = (select id from wyposazenie order by 1 desc limit 1);
    insert into pokoje_wyposazenie values (pokoj, sprzet);
end;
$$
language 'plpgsql';

--sprawdza poprawnosc rezerwacji
create or replace function sprawdz_poprawnosc()
    returns trigger as $sprawdz_poprawnosc$
declare
    pokoj record;
    wynik numeric;
    gosc integer;
    cena_pokoju numeric;
begin
    cena_pokoju = (select cena_podstawowa from pokoje where id_pokoju = new.id_pokoju) * (new.data_do - new.data_od);
    gosc = (select distinct id_goscia from rezerwacje_pokoje natural join rezerwacje_goscie where id_rez_zbiorczej = new.id_rez_zbiorczej);
    wynik = (select id_pokoju from pokoje where id_pokoju = new.id_pokoju and id_pokoju in
    (select id_pokoju
	from rezerwacje_pokoje
	where
	((new.data_od < data_od and data_od < new.data_do) or
	(new.data_od < data_do and data_do < new.data_do) or
	(data_od <= new.data_od and new.data_do <= data_do )) and anulowane_data is null) limit 1);
    RAISE NOTICE 'Znalezniono pokoj(%)', wynik;
    if wynik is not null then
        raise exception 'pokoj jest zajety w wybranym terminie';
    elsif new.plan_liczba_osob > (select max_liczba_osob from pokoje where id_pokoju = new.id_pokoju) then
        raise exception 'za duzo gosci do pokoju';
    elsif new.cena <> (select oblicz_znizke(gosc, cena_pokoju, new.data_od)) then
	raise exception 'podano nieprawidlowa cene: %, prawidlowa cena to % ', new.cena, (select oblicz_znizke(gosc, cena_pokoju, new.data_od));
    end if;
    return new;
end;
$sprawdz_poprawnosc$
language 'plpgsql';

create trigger sprawdz_poprawnosc before insert on rezerwacje_pokoje
for each row execute procedure sprawdz_poprawnosc();

--uaktualnia cene po dodaniu uslugi
create or replace function dodaj_cene_za_usluge()
    returns trigger as $dodaj_cene_za_usluge$
declare
    --id_goscia integer;
    od_data date;
    do_data date;
    cena_pokoju numeric;
    cena_uslugi numeric;
    wynik numeric;
begin
    if (select anulowane_data from rezerwacje_pokoje where id_rez_pojedynczej = new.id_rez_pojedynczej) is not null then
	raise exception 'nie mozna dodac uslugi do anulowanej rezerwacji';
    end if;
    od_data = (select data_od from rezerwacje_pokoje where id_rez_pojedynczej = new.id_rez_pojedynczej);
    do_data = (select data_do from rezerwacje_pokoje where id_rez_pojedynczej = new.id_rez_pojedynczej);

    if (new.data_od < od_data or new.data_do > do_data) then
	raise exception 'data uslugi nie jest zgodna z data rezerwacji';
    elsif (select sum(liczba)
	from usl_rez u join rezerwacje_pokoje rp on u.id_rez_pojedynczej = rp.id_rez_pojedynczej
	where u.id_rez_pojedynczej = new.id_rez_pojedynczej and id_uslugi_dod = new.id_uslugi_dod and data_anulowania is null
	and ((new.data_od < u.data_od and u.data_od < new.data_do) or
			(new.data_od < u.data_do and u.data_do < new.data_do) or
			(u.data_od <= new.data_od and new.data_do <= u.data_do ))) > (select plan_liczba_osob from rezerwacje_pokoje where id_rez_pojedynczej = new.id_rez_pojedynczej) then
	raise exception 'nie mozna zamowic wybranej uslugi w wybranej ilosci';
    end if;

   -- id_goscia = (select rg.id_goscia from rezerwacje_goscie rg natural join rezerwacje_pokoje where id_rez_pojedynczej = new.id_rez_pojedynczej);
    raise notice 'dodaje cene za usluge';
    cena_pokoju = (select cena from rezerwacje_goscie natural join rezerwacje_pokoje where id_rez_pojedynczej = new.id_rez_pojedynczej);
    cena_uslugi = (select cena from uslugi_dod where id_uslugi_dod = new.id_uslugi_dod);
    update rezerwacje_pokoje set cena = cena_pokoju + cena_uslugi * new.liczba * (new.data_do - new.data_od);
    return new;
end;
$dodaj_cene_za_usluge$
language 'plpgsql';

create trigger dodaj_cene_za_usluge after insert on usl_rez
for each row execute procedure dodaj_cene_za_usluge();

--anulowanie uslugi dodatkowej

create or replace function anuluj_usluge()
    returns trigger as $anuluj_usluge$
begin
    if(new.id_uslugi_dod <> old.id_uslugi_dod or new.id_rez_pojedynczej <> old.id_rez_pojedynczej or new.liczba <> old.liczba or new.data_od<>old.data_od or new.data_do <> old.data_do) then
	raise exception 'nie mozna edytowac rezerwacji uslugi';
    elsif old.data_anulowania is not null then
	raise exception 'usluga zostala juz anulowana';
    elsif new.data_anulowania is not null and new.data_anulowania > old.data_od then
	raise exception 'za pozno na rezygnacje z uslugi';
    else
	update rezerwacje_pokoje set cena = ((select cena from rezerwacje_goscie natural join rezerwacje_pokoje where id_rez_pojedynczej = old.id_rez_pojedynczej) - (select cena from uslugi_dod where id_uslugi_dod = old.id_uslugi_dod) * old.liczba );
    end if;
    return new;

end;
$anuluj_usluge$
language 'plpgsql';

create trigger anuluj_usluge before update on usl_rez
for each row execute procedure anuluj_usluge();
--FUNKCJE, TRIGGERY, KONIEC

--USLUGI DODATKOWE
insert into uslugi_dod values
(default, 'Breakfast', 30),
(default, 'Dinner', 40),
(default, 'Evening meal', 35),
(default, 'Put your kids to sleep', 100),
(default, 'Wake up call', 50),
(default, 'Live with pet', 100);
--USLUGI DODATKOWE, KONIEC

COMMIT;
