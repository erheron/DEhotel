--oblicza znizke na podstawie liczby dni spedzonych w hotelu
--[1,30) dni - 5% znizki
--[30,...) dni 15% znizki
create or replace function oblicz_znizke(id integer, cena numeric)
	returns numeric as
$$
declare
	suma_dni numeric;
begin
	suma_dni = coalesce((select sum(data_do - data_od)
			from  rezerwacje_pokoje natural join rezerwacje_goscie
			where id = id_goscia),0);
	if suma_dni < 1 then
		return round(cena,2);
	elsif suma_dni >=1 and suma_dni < 30 then
		return round(cena*0.95,2);
	else
		return round(cena*0.85,2); 
	end if;
end;
$$
language 'plpgsql';
