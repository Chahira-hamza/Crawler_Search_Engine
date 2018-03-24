
USE Indexer
GO

CREATE TABLE Docs_URL (
ID int NOT NULL IDENTITY(1,1),
Title varchar(50), 
URL varchar(255) NOT NULL,
CONSTRAINT DOCS_ID UNIQUE(URL),
primary key(ID)
);


CREATE TABLE Words
(   Doc_ID int NOT NULL,
	Word_ID int NOT NULL  Identity(1,1),
    word nvarchar(20) Not NULL, 
	Rankw INt Not Null,
	PRIMARY KEY (Doc_ID,Word_ID),    
CONSTRAINT FK_DOCID FOREIGN KEY (Doc_ID) REFERENCES Docs_URL(ID)   
    ON DELETE CASCADE    
    ON UPDATE CASCADE    
);  

CREATE TABLE DocText(
	[Doc_ID] [int] NOT NULL,
	[Dtext] [text] NOT NULL,
	PRIMARY KEY (Doc_ID),    
CONSTRAINT FK_TDOCID FOREIGN KEY (Doc_ID)
    REFERENCES Docs_URL(ID)    
    ON DELETE CASCADE    
    ON UPDATE CASCADE
	)
	