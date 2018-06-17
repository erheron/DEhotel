--oblicza znizke na podstawie liczby dni spedzonych w hotelu w poprzednim roku
--[7,30) dni - 1% znizki
--[30,...) dni 5% znizki
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
