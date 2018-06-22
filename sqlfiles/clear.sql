drop trigger if exists function anuluj_usluge on usl_rez;
drop function if exists function anuluj_usluge();

drop trigger if exists dodaj_cene_za_usluge on usl_rez;
drop function if exists dodaj_cene_za_usluge();

drop trigger if exists sprawdz_poprawnosc on rezerwacje_pokoje;
drop function if exists sprawdz_poprawnosc();

drop function if exists usun_sprzet(integer);

drop trigger if exists dodaj_kare on rezerwacje_pokoje;
drop function if exists dodaj_kare();

drop function if exists oblicz_znizke(integer, numeric, date);

drop table if exists usl_rez;
drop table if exists uslugi_dod;
drop table if exists kary;
drop table if exists platnosci;
drop table if exists pokoje_wyposazenie;
drop table if exists wyposazenie;
drop table if exists rodzaje_wyposazenia;
drop table if exists rezerwacje_pokoje cascade;
drop table if exists rezerwacje_goscie;
drop table if exists goscie;
drop table if exists pokoje;