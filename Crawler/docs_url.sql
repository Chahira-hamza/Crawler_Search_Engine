CREATE TABLE Docs_URL (
ID int NOT NULL IDENTITY(1,1),
Title varchar(50), 
URL varchar(2085) NOT NULL,
Visited int,
primary key(URL)
);

INSERT into Docs_URL (URL,Visited)
VALUES ('https://www.theguardian.com/international',0);
