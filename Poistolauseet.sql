--########################################## Triggerin ja näkymien poisto


DROP TRIGGER keskuspaivitys ON d3.teoskappale;
DROP FUNCTION keskuspaivitys;

DROP VIEW hinnatLuokittain;
DROP VIEW ostotVuodessa;


-- Vaihtoehtoiset poistolauseet, jos yllä olevat eivät jostain syystä toimikkaan
-- DROP FUNCTION keskus.keskuspaivitys;
-- DROP VIEW keskus.hinnatLuokittain;
-- DROP VIEW keskus.ostotVuodessa;


--########################################## Taulujen poisto


DROP TABLE keskus.toimitus;
DROP TABLE keskus.postikulut;
DROP TABLE keskus.sijainti;
DROP TABLE keskus.tilaus;
DROP TABLE keskus.asiakas;
DROP TABLE keskus.yllapitaja;
DROP TABLE keskus.kayttaja;
DROP TABLE keskus.divari;
DROP TABLE keskus.teoskappale;
DROP TABLE keskus.teos;

DROP TABLE D1.teoskappale;
DROP TABLE D1.teos;

DROP TABLE D3.teoskappale;
DROP TABLE D3.teos;


--########################################## Tietokannan ja käyttäjän poisto (suoritettava yksi kerrallaan)


--DROP DATABASE Kirjakauppa;
--DROP USER harjoituskayttaja;