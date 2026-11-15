SET NAMES utf8mb4;
--
INSERT INTO 
	`persona_db`.`persona`(`cc`,`nombre`,`apellido`,`genero`,`edad`) 
VALUES
	(123456789,'Pepe','Perez','M',30),
	(987654321,'Pepito','Perez','M',null),
	(321654987,'Pepa','Juarez','F',30),
	(147258369,'Pepita','Juarez','F',10),
	(963852741,'Fede','Perez','M',18);
--
INSERT INTO 
	`persona_db`.`profesion`(`id`,`nom`,`des`) 
VALUES
	(1,'Ingeniero de Sistemas','Diseño y desarrollo de software'),
	(2,'Medico','Diagnóstico y tratamiento de enfermedades'),
	(3,'Abogado','Asesoría y representación legal'),
	(4,'Contador','Contabilidad y finanzas');
--
INSERT INTO 
	`persona_db`.`estudios`(`id_prof`,`cc_per`,`fecha`,`univer`) 
VALUES
	(1,123456789,'2015-12-15','Pontificia Universidad Javeriana'),
	(2,987654321,'2018-06-30','Universidad Nacional de Colombia'),
	(3,321654987,'2012-11-20','Universidad de los Andes'),
	(4,321654987,'2016-06-10','Universidad del Rosario');
--
INSERT INTO 
	`persona_db`.`telefono`(`num`,`oper`,`duenio`) 
VALUES
	('3001234567','Claro',123456789),
	('3109876543','Movistar',123456789),
	('3157778899','Claro',987654321),
	('3204567890','Tigo',321654987);
