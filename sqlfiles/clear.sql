drop trigger if exists anuluj_usluge on usl_rez cascade;
drop function if exists anuluj_usluge() cascade;

drop trigger if exists dodaj_cene_za_usluge on usl_rez cascade;
drop function if exists dodaj_cene_za_usluge() cascade;

drop trigger if exists sprawdz_poprawnosc on rezerwacje_pokoje cascade;
drop function if exists sprawdz_poprawnosc() cascade;

drop function if exists usun_sprzet(integer) cascade;

drop trigger if exists dodaj_kare on rezerwacje_pokoje cascade;
drop function if exists dodaj_kare() cascade;

drop function if exists oblicz_znizke(integer, numeric, date) cascade;

drop table if exists pokoje cascade;
drop table if exists goscie cascade;
drop table if exists rezerwacje_goscie cascade;
drop table if exists rezerwacje_pokoje cascade;
drop table if exists rodzaje_wyposazenia cascade;
drop table if exists wyposazenie cascade;
drop table if exists pokoje_wyposazenie cascade;
drop table if exists kary cascade;
drop table if exists uslugi_dod cascade;
drop table if exists usl_rez cascade;
