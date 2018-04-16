-- Tapahtuma 6: Triggeri, joka päivittää keskustietokannan automaattisesti,
-- kun divariin 3 tuodaan uusi myyntikappale. Tapahtumassa oletetaan, että
-- teoksen yleiset tiedot löytyvät jo valmiiksi molemmista tietokannoista.

CREATE FUNCTION keskuspaivitys() RETURNS trigger AS $keskuspaivitys$
   BEGIN
      INSERT INTO keskus.TeosKappale VALUES (NEW.KappaleID, NEW.ISBN, NEW.Hinta,NEW.Ostohinta, null, NEW.Vapaus);
      INSERT INTO keskus.Sijainti VALUES (3, NEW.KappaleID);
      RETURN NEW;
   END;
$keskuspaivitys$ LANGUAGE plpgsql;

CREATE TRIGGER keskuspaivitys BEFORE INSERT ON D3.TeosKappale
FOR EACH ROW EXECUTE PROCEDURE keskuspaivitys();