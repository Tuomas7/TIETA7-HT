--########################################## Käyttäjän ja tietokannan luonti (Suoritettava yksi kerrallaan)


-- CREATE USER harjoituskayttaja WITH PASSWORD 'salasana';
-- CREATE DATABASE Kirjakauppa WITH OWNER harjoituskayttaja;


--########################################## Skeemojen luonnit ja alustukset


CREATE SCHEMA keskus;
SET search_path to keskus;
SHOW SEARCH_PATH;

CREATE SCHEMA D1;
CREATE SCHEMA D3;


--########################################## Keskuksen taulujen luonti


CREATE TABLE keskus.Divari (
	DivariID int NOT NULL,
	Nimi varchar(50) NOT NULL,
	Osoite varchar(80),
	Websivu varchar(50),
	itsenainen int NOT NULL,
	PRIMARY KEY (DivariID)

);

CREATE TABLE keskus.Kayttaja (
	KayttajaID int NOT NULL,
	Nimi varchar(50) NOT NULL,
	Salasana varchar(50) NOT NULL,
	Rooli varchar(20) DEFAULT 'Asiakas',
	PRIMARY KEY (KayttajaID)
);

CREATE TABLE keskus.Yllapitaja (
	YllapitajaID int NOT NULL,
	DivariID int NOT NULL,
	PRIMARY KEY (YllapitajaID, DivariID),
	FOREIGN KEY (YllapitajaID) REFERENCES keskus.Kayttaja(KayttajaID),
	FOREIGN KEY (DivariID) REFERENCES keskus.Divari(DivariID)
);

CREATE TABLE keskus.Asiakas (
	AsiakasID int NOT NULL,
	Etunimi varchar(50) NOT NULL,
	Sukunimi varchar(50) NOT NULL,
	Osoite varchar(80) NOT NULL,
	Sahkoposti varchar(50),
	Puhelin varchar(15),
	Saldo decimal(10,2) DEFAULT 0.00,
	PRIMARY KEY (AsiakasID),
	FOREIGN KEY (AsiakasID) REFERENCES keskus.Kayttaja(KayttajaID)
);

CREATE TABLE keskus.Teos (
	ISBN varchar(13) NOT NULL,
	Nimi varchar(50) NOT NULL,
	Tekija varchar(50),
	Vuosi int,
	Tyyppi varchar(30),
	Luokka varchar(30),
	Paino int NOT NULL,
	PRIMARY KEY (ISBN)
);

CREATE TABLE keskus.TeosKappale (
	KappaleID int NOT NULL,
	ISBN varchar(13) NOT NULL,
	Hinta decimal(10,2) NOT NULL,
	Ostohinta decimal(10,2) DEFAULT 0.00,
	MyyntiPvm date,
	Vapaus varchar(10) DEFAULT 'Vapaa',
	PRIMARY KEY (KappaleID),
	FOREIGN KEY (ISBN) REFERENCES keskus.Teos(ISBN)
);

CREATE TABLE keskus.Sijainti (
	DivariID int NOT NULL,
	KappaleID int NOT NULL,
	PRIMARY KEY (DivariID, KappaleID),
	FOREIGN KEY (DivariID) REFERENCES keskus.Divari(DivariID),
	FOREIGN KEY (KappaleID) REFERENCES keskus.TeosKappale(KappaleID)
);

CREATE TABLE keskus.Tilaus (
	DivariID int NOT NULL,
	AsiakasID int NOT NULL,
	KappaleID int NOT NULL,
	Tila varchar(10) DEFAULT 'Kaynnissa',
	PRIMARY KEY (DivariID, AsiakasID, KappaleID),
	FOREIGN KEY (AsiakasID) REFERENCES keskus.Asiakas(AsiakasID),
	FOREIGN KEY (DivariID) REFERENCES keskus.Divari(DivariID),
	FOREIGN KEY (KappaleID) REFERENCES keskus.TeosKappale(KappaleID)
);

CREATE TABLE keskus.Postikulut (
	Maksuluokka int NOT NULL,
	Maksu decimal(10,2) NOT NULL,
	PRIMARY KEY (Maksuluokka)
);

CREATE TABLE keskus.Toimitus (
   ToimitusID int NOT NULL,
	AsiakasID int NOT NULL,
	Paino int NOT NULL,
   Maksuluokka int NOT NULL,
	PRIMARY KEY (ToimitusID, AsiakasID),
	FOREIGN KEY (AsiakasID) REFERENCES keskus.Asiakas(AsiakasID),
	FOREIGN KEY (Maksuluokka) REFERENCES keskus.Postikulut(Maksuluokka)
);


GRANT ALL PRIVILEGES ON TABLE keskus.divari TO harjoituskayttaja;
GRANT ALL PRIVILEGES ON TABLE keskus.asiakas TO harjoituskayttaja;
GRANT ALL PRIVILEGES ON TABLE keskus.kayttaja TO harjoituskayttaja;
GRANT ALL PRIVILEGES ON TABLE keskus.teos TO harjoituskayttaja;
GRANT ALL PRIVILEGES ON TABLE keskus.teoskappale TO harjoituskayttaja;
GRANT ALL PRIVILEGES ON TABLE keskus.tilaus TO harjoituskayttaja;
GRANT ALL PRIVILEGES ON TABLE keskus.postikulut TO harjoituskayttaja;
GRANT ALL PRIVILEGES ON TABLE keskus.toimitus TO harjoituskayttaja;
GRANT ALL PRIVILEGES ON TABLE keskus.sijainti TO harjoituskayttaja;
GRANT ALL PRIVILEGES ON TABLE keskus.yllapitaja TO harjoituskayttaja;
GRANT ALL PRIVILEGES ON SCHEMA keskus TO GROUP harjoituskayttaja;


--########################################## Divarin D1 taulujen luonti


CREATE TABLE D1.Teos (
	ISBN varchar(13) NOT NULL,
	Nimi varchar(50) NOT NULL,
	Tekija varchar(50),
	Vuosi int,
	Tyyppi varchar(30),
	Luokka varchar(30),
	Paino int NOT NULL,
	PRIMARY KEY (ISBN)
);

CREATE TABLE D1.TeosKappale (
	KappaleID int NOT NULL,
	ISBN varchar(13) NOT NULL,
	Hinta decimal(10,2) NOT NULL,
	Ostohinta decimal(10,2) DEFAULT 0.00,
	MyyntiPvm date,
	Vapaus varchar(10) DEFAULT 'Vapaa',
   Paivitetty boolean DEFAULT false,
	PRIMARY KEY (KappaleID),
	FOREIGN KEY (ISBN) REFERENCES D1.Teos(ISBN)
);


GRANT ALL PRIVILEGES ON TABLE D1.teos TO harjoituskayttaja;
GRANT ALL PRIVILEGES ON TABLE D1.teoskappale TO harjoituskayttaja;
GRANT ALL PRIVILEGES ON SCHEMA D1 TO GROUP harjoituskayttaja;


--########################################## Divarin D3 taulujen luonti


CREATE TABLE D3.Teos (
	ISBN varchar(13) NOT NULL,
	Nimi varchar(50) NOT NULL,
	Tekija varchar(50),
	Vuosi int,
	Tyyppi varchar(30),
	Luokka varchar(30),
	Paino int NOT NULL,
	PRIMARY KEY (ISBN)
);

CREATE TABLE D3.TeosKappale (
	KappaleID int NOT NULL,
	ISBN varchar(13) NOT NULL,
	Hinta decimal(10,2) NOT NULL,
	Ostohinta decimal(10,2) DEFAULT 0.00,
	MyyntiPvm date,
	Vapaus varchar(10) DEFAULT 'Vapaa',
	PRIMARY KEY (KappaleID),
	FOREIGN KEY (ISBN) REFERENCES D3.Teos(ISBN)
);


GRANT ALL PRIVILEGES ON TABLE D3.teos TO harjoituskayttaja;
GRANT ALL PRIVILEGES ON TABLE D3.teoskappale TO harjoituskayttaja;
GRANT ALL PRIVILEGES ON SCHEMA D3 TO GROUP harjoituskayttaja;


--########################################## Triggerin lisäys (päivittää divariin D3 lisättävät kappaleet keskustietokantaan)


CREATE OR REPLACE FUNCTION keskuspaivitys() RETURNS trigger AS $keskuspaivitys$
   DECLARE
      id integer;
   BEGIN
      SELECT MAX(KappaleID) INTO id FROM keskus.teosKappale;
      id = id + 1;
      INSERT INTO keskus.TeosKappale VALUES (id, NEW.ISBN, NEW.Hinta,NEW.Ostohinta, null, NEW.Vapaus);
      INSERT INTO keskus.Sijainti VALUES (3, id);
      UPDATE D3.TeosKappale SET KappaleID=id WHERE KappaleID=NEW.KappaleID;
      RETURN NEW;
   END;
$keskuspaivitys$ LANGUAGE plpgsql;

CREATE TRIGGER keskuspaivitys AFTER INSERT ON D3.TeosKappale
FOR EACH ROW EXECUTE PROCEDURE keskuspaivitys();


--########################################## Näkymien (raporttien) luontilauseet


CREATE VIEW hinnatLuokittain AS 
   SELECT luokka, SUM(hinta) AS kokonaishinta, CAST(AVG(hinta) AS DECIMAL(10,2)) AS keskihinta 
   FROM keskus.teos NATURAL JOIN keskus.teoskappale 
   WHERE vapaus='Vapaa' 
   GROUP BY luokka
   ORDER BY kokonaishinta;
   
CREATE VIEW ostotVuodessa AS 

   SELECT etunimi,sukunimi, count(kappaleID) AS ostot 
   FROM keskus.asiakas NATURAL JOIN keskus.tilaus NATURAL JOIN keskus.teosKappale 
   WHERE (myyntipvm + interval '12 months') > current_date 
   GROUP BY etunimi,sukunimi 
   ORDER BY ostot DESC;


GRANT ALL PRIVILEGES ON table keskus.hinnatLuokittain TO testuser;
GRANT ALL PRIVILEGES ON table keskus.ostotVuodessa TO testuser;

########################################## Keskusdivarin tietojen asetus
=======
   SELECT etunimi, sukunimi, count(kappaleID) AS ostot 
   FROM keskus.asiakas NATURAL JOIN keskus.tilaus NATURAL JOIN keskus.teosKappale 
   WHERE (myyntipvm + interval '12 months') > current_date 
   GROUP BY etunimi, sukunimi
   ORDER BY ostot DESC;


GRANT ALL PRIVILEGES ON hinnatLuokittain TO harjoituskayttaja;
GRANT ALL PRIVILEGES ON ostotVuodessa TO harjoituskayttaja;
   
--########################################## Keskusdivarin tietojen asetus



INSERT INTO keskus.divari VALUES('1','Lassen Lehti','Lehtikatu 31, Tampere','www.lassenlehti.fi',1);
INSERT INTO keskus.divari VALUES('2','Galleinn Galle','Kallenkuja 4, Vaasa','www.galleinngalle.fi',0);
INSERT INTO keskus.divari VALUES('3','Antikvariaari Herätin','Triggerintie 58, Helsinki',null,1);
INSERT INTO keskus.divari VALUES('4','Puolirakenteinen Kirjakauppa','Entiteetinpolku 19, Oulu',null,1);


INSERT INTO keskus.kayttaja VALUES(100,'admin1','admin1','Yllapitaja');
INSERT INTO keskus.kayttaja VALUES(200,'admin2','admin2','Yllapitaja');
INSERT INTO keskus.kayttaja VALUES(300,'admin3','admin3','Yllapitaja');
INSERT INTO keskus.kayttaja VALUES(400,'admin4','admin4','Yllapitaja');
INSERT INTO keskus.kayttaja VALUES(1, 'superuser', 'superuser', 'Superuser');

INSERT INTO keskus.yllapitaja VALUES(100,1);
INSERT INTO keskus.yllapitaja VALUES(200,2);
INSERT INTO keskus.yllapitaja VALUES(300,3);
INSERT INTO keskus.yllapitaja VALUES(400,4);


INSERT INTO keskus.kayttaja VALUES ('436', 'tvirtane', 'salainensana', 'Asiakas');
INSERT INTO keskus.kayttaja VALUES ('783', 'aylisuva', 'hunter2', 'Asiakas');
INSERT INTO keskus.kayttaja VALUES ('216', 'svuolle', 'kukaaneiarvaa', 'Asiakas');
INSERT INTO keskus.kayttaja VALUES ('306', 'jkunnas', 'qwertyuiop', 'Asiakas');
INSERT INTO keskus.kayttaja VALUES ('137', 'jitala', 'password123', 'Asiakas');
INSERT INTO keskus.kayttaja VALUES ('861', 'ekorhone', 'parassalasana', 'Asiakas');
INSERT INTO keskus.kayttaja VALUES ('927', 'esupinen', 'x3m67n45y0q9afcz2x07c', 'Asiakas');
INSERT INTO keskus.kayttaja VALUES ('485', 'hharjula', '123456789', 'Asiakas');

INSERT INTO keskus.asiakas VALUES ('436', 'Tommi', 'Virtanen', 'Puistopolku 19, Halikko', 'virtanen93@email.com', '04085480', '50.00');
INSERT INTO keskus.asiakas VALUES ('783', 'Ari-Pekka', 'Ylisuvanto', 'Savelankatu 55, Vähäkyrö', null, null, '25.59');
INSERT INTO keskus.asiakas VALUES ('216', 'Senni', 'Vuolle', 'Pohjantie 48, Ylämaa', 'vuolle@email.com', '04067839', '11.12');
INSERT INTO keskus.asiakas VALUES ('306', 'Jukka', 'Kunnas', 'Kanervakatu 31, Kuopio', 'jkunnas@email.com', null, '0.00');
INSERT INTO keskus.asiakas VALUES ('137', 'Janina', 'Itälä', 'Eräkatu 5, Pyhäntä', null, '04002564', '33.46');
INSERT INTO keskus.asiakas VALUES ('861', 'Elisa', 'Korhonen', 'Rantatie 15, Enonkoski', 'korhonen89@email.com', '04089345', '4.87');
INSERT INTO keskus.asiakas VALUES ('927', 'Esa', 'Supinen', 'Kotikuja 9, Kylmäkoski', null, null, '0.00');
INSERT INTO keskus.asiakas VALUES ('485', 'Helena', 'Harjula', 'Lammenraitti 26, Anjalankoski', 'hharjula@email.com', null, '48.43');


INSERT INTO keskus.teos VALUES('9155430674','Elektran tytär','Madeleine Brent','1986','romaani','romantiikka','1467');
INSERT INTO keskus.teos VALUES('9156381451','Tuulentavoittelijan morsian','Madeleine Brent','1978','romaani','romantiikka','1356');
INSERT INTO keskus.teos VALUES('1230207500','Turms kuolematon','Mika Waltari','1995','romaani','historia','964');
INSERT INTO keskus.teos VALUES('9789510320990','Komisario Palmun erehdys','Mika Waltari','1940','romaani','dekkari','465');
INSERT INTO keskus.teos VALUES('9519201785','Friikkilän pojat Mexicossa','Shelton Gilbert','1989','sarjakuva','huumori','198');
INSERT INTO keskus.teos VALUES('9789510396230','Miten saan ystäviä, menestystä, vaikutusvaltaa','Dale Carnegien','1939','tietokirja','opas','736');

INSERT INTO keskus.teoskappale VALUES('1','9155430674','22.50','11.00','2017-03-14','Myyty');
INSERT INTO keskus.teoskappale VALUES('2','9519201785','12.99','6.00',null,'Vapaa');
INSERT INTO keskus.teoskappale VALUES('3','1230207500','30.00','15.00','2018-01-28','Myyty');
INSERT INTO keskus.teoskappale VALUES('4','9156381451','9.99','5.00',null,'Vapaa');
INSERT INTO keskus.teoskappale VALUES('5','9789510396230','15.50','7.00','2017-09-12','Myyty');
INSERT INTO keskus.teoskappale VALUES('6','9155430674','16.80','8.00',null,'Vapaa');
INSERT INTO keskus.teoskappale VALUES('7','9519201785','7.30','0.00',null,'Vapaa');
INSERT INTO keskus.teoskappale VALUES('8','9789510320990','14.99','7.00','2016-12-21','Myyty');
INSERT INTO keskus.teoskappale VALUES('9','1230207500','10.00','5.00','2018-04-19','Myyty');
INSERT INTO keskus.teoskappale VALUES('10','9789510320990','8.90','0.00',null,'Vapaa');
INSERT INTO keskus.teoskappale VALUES('11','9789510396230','19.99','10.00','2017-08-05','Myyty');
INSERT INTO keskus.teoskappale VALUES('12','9156381451','13.50','6.00','2017-08-05','Myyty');
INSERT INTO keskus.teoskappale VALUES('13','9789510396230','22.00','11.00',null,'Vapaa');
INSERT INTO keskus.teoskappale VALUES('14','9155430674','17.99','9.00',null,'Vapaa');
INSERT INTO keskus.teoskappale VALUES('15','9789510320990','26.70','0.00','2018-02-15','Myyty');
INSERT INTO keskus.teoskappale VALUES('16','9156381451','14.00','7.00',null,'Vapaa');
INSERT INTO keskus.teoskappale VALUES('17','9519201785','18.50','10.00','2016-11-13','Myyty');
INSERT INTO keskus.teoskappale VALUES('18','1230207500','9.50','0.00',null,'Vapaa');
INSERT INTO keskus.teoskappale VALUES('19','9789510396230','25.00','12.00',null,'Vapaa');
INSERT INTO keskus.teoskappale VALUES('20','9155430674','11.30','5.00',null,'Vapaa');
INSERT INTO keskus.teoskappale VALUES('21','9789510320990','13.99','0.00','2016-05-26','Myyty');
INSERT INTO keskus.teoskappale VALUES('22','9156381451','9.80','5.00','2018-03-01','Myyty');
INSERT INTO keskus.teoskappale VALUES('23','9519201785','18.00','9.00','2017-07-18','Myyty');
INSERT INTO keskus.teoskappale VALUES('24','1230207500','8.80','0.00',null,'Vapaa');


INSERT INTO keskus.sijainti VALUES('2', '1');
INSERT INTO keskus.sijainti VALUES('2', '2');
INSERT INTO keskus.sijainti VALUES('2', '3');
INSERT INTO keskus.sijainti VALUES('2', '4');
INSERT INTO keskus.sijainti VALUES('2', '5');
INSERT INTO keskus.sijainti VALUES('2', '6');
INSERT INTO keskus.sijainti VALUES('2', '7');
INSERT INTO keskus.sijainti VALUES('2', '8');
INSERT INTO keskus.sijainti VALUES('2', '9');
INSERT INTO keskus.sijainti VALUES('2', '10');
INSERT INTO keskus.sijainti VALUES('2', '11');
INSERT INTO keskus.sijainti VALUES('2', '12');
INSERT INTO keskus.sijainti VALUES('2', '13');
INSERT INTO keskus.sijainti VALUES('2', '14');
INSERT INTO keskus.sijainti VALUES('2', '15');
INSERT INTO keskus.sijainti VALUES('2', '16');
INSERT INTO keskus.sijainti VALUES('2', '17');
INSERT INTO keskus.sijainti VALUES('2', '18');
INSERT INTO keskus.sijainti VALUES('2', '19');
INSERT INTO keskus.sijainti VALUES('2', '20');
INSERT INTO keskus.sijainti VALUES('2', '21');
INSERT INTO keskus.sijainti VALUES('2', '22');
INSERT INTO keskus.sijainti VALUES('2', '23');
INSERT INTO keskus.sijainti VALUES('2', '24');

INSERT INTO keskus.tilaus VALUES('2', '306', '17', 'Suoritettu');
INSERT INTO keskus.tilaus VALUES('2', '485', '5', 'Suoritettu');
INSERT INTO keskus.tilaus VALUES('2', '306', '22', 'Suoritettu');
INSERT INTO keskus.tilaus VALUES('2', '783', '11', 'Suoritettu');
INSERT INTO keskus.tilaus VALUES('2', '306', '3', 'Suoritettu');
INSERT INTO keskus.tilaus VALUES('2', '861', '1', 'Suoritettu');
INSERT INTO keskus.tilaus VALUES('2', '485', '23', 'Suoritettu');
INSERT INTO keskus.tilaus VALUES('2', '137', '15', 'Suoritettu');
INSERT INTO keskus.tilaus VALUES('2', '783', '8', 'Suoritettu');
INSERT INTO keskus.tilaus VALUES('2', '861', '12', 'Suoritettu');
INSERT INTO keskus.tilaus VALUES('2', '436', '21', 'Suoritettu');
INSERT INTO keskus.tilaus VALUES('2', '306', '9', 'Suoritettu');


INSERT INTO keskus.postikulut VALUES('50', '1.40');
INSERT INTO keskus.postikulut VALUES('100', '2.10');
INSERT INTO keskus.postikulut VALUES('250', '2.80');
INSERT INTO keskus.postikulut VALUES('500', '5.60');
INSERT INTO keskus.postikulut VALUES('1000', '8.40');
INSERT INTO keskus.postikulut VALUES('2000', '14.00');


--########################################## Divarin D1 tietojen asetus


INSERT INTO d1.teos VALUES('9155430674','Elektran tytär','Madeleine Brent','1986','romaani','romantiikka','1467');
INSERT INTO d1.teos VALUES('9156381451','Tuulentavoittelijan morsian','Madeleine Brent','1978','romaani','romantiikka','1356');
INSERT INTO d1.teos VALUES('9789510393741','Ajan lyhyt historia','Stephen Hawking','1988','tietokirja','tiede','356');
INSERT INTO d1.teos VALUES('0618391118','The Silmarillion','J. R. R. Tolkien','1977','kokoelma','fantasia','528');

INSERT INTO d1.teoskappale VALUES('1','9155430674','21.00','10.00',null,'Vapaa');
INSERT INTO d1.teoskappale VALUES('2','9156381451','11.99','6.00',null,'Vapaa');
INSERT INTO d1.teoskappale VALUES('3','9789510393741','18.50','9.00',null,'Vapaa');
INSERT INTO d1.teoskappale VALUES('4','9155430674','15.70','7.00',null,'Vapaa');
INSERT INTO d1.teoskappale VALUES('5','0618391118','14.99','8.00',null,'Vapaa');
INSERT INTO d1.teoskappale VALUES('6','9156381451','13.00','6.00',null,'Vapaa');
INSERT INTO d1.teoskappale VALUES('7','9789510393741','22.50','11.00',null,'Vapaa');
INSERT INTO d1.teoskappale VALUES('8','0618391118','9.99','5.00',null,'Vapaa');
INSERT INTO d1.teoskappale VALUES('9','0618391118','59.99','1.00',null,'Vapaa');
INSERT INTO d1.teoskappale VALUES('10','9155430674','8.90','4.00',null,'Vapaa');
INSERT INTO d1.teoskappale VALUES('11','9789510393741','12.99','6.00',null,'Vapaa');
INSERT INTO d1.teoskappale VALUES('12','9156381451','15.00','7.00',null,'Vapaa');


--########################################## Divarin D3 tietojen asetus (Siirtyvät automaattisesti keskustietokantaan triggerin kautta)


INSERT INTO d3.teos VALUES('9519201785','Friikkilän pojat Mexicossa','Shelton Gilbert','1989','sarjakuva','huumori','198');
INSERT INTO d3.teos VALUES('9789510396230','Miten saan ystäviä, menestystä, vaikutusvaltaa','Dale Carnegien','1939','tietokirja','opas','736');

INSERT INTO d3.teoskappale VALUES('1','9789510396230','14.50','7.00',null,'Vapaa');
INSERT INTO d3.teoskappale VALUES('2','9519201785','8.99','4.00',null,'Vapaa');
INSERT INTO d3.teoskappale VALUES('3','9789510396230','11.20','6.00',null,'Vapaa');
INSERT INTO d3.teoskappale VALUES('4','9519201785','22.90','11.00',null,'Vapaa');
INSERT INTO d3.teoskappale VALUES('5','9519201785','17.99','9.00',null,'Vapaa');
INSERT INTO d3.teoskappale VALUES('6','9789510396230','14.30','7.00',null,'Vapaa');


--##########################################