CREATE TABLE Docs_URL (
ID int NOT NULL IDENTITY(1,1),
Protocol varchar(25),
Title varchar(50), 
URL varchar(255) NOT NULL,
primary key(URL)
);
