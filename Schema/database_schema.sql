
CREATE TABLE Docs_URL (
ID int NOT NULL IDENTITY(1,1),
Title varchar(50), 
URL varchar(2085) NOT NULL UNIQUE,
CONSTRAINT DOCS_ID UNIQUE(URL),
Visited int,
linkRank int,
primary key(ID)
);

INSERT into Docs_URL (URL,Visited)
VALUES ('https://www.theguardian.com',0);

CREATE TABLE Words
(   Doc_ID int NOT NULL,
	Word_ID int NOT NULL ,
   	 Stemmed_Word nvarchar(20) Not NULL, 
   	 Word nvarchar(20) Not NULL, 
	Rankw bigint Not Null,
	PRIMARY KEY (Doc_ID,Word_ID),    
CONSTRAINT FK_worddoc FOREIGN KEY (Doc_ID) REFERENCES Docs_URL(ID)   
    ON DELETE CASCADE    
    ON UPDATE CASCADE    
);  


	CREATE TABLE WordPostions
(   Doc_ID int NOT NULL,
	Word_ID int NOT NULL,
	Pos bigint Not NULL, 
	
	PRIMARY KEY (Doc_ID,Word_ID,Pos),    
CONSTRAINT FK_PosID FOREIGN KEY (Doc_ID,Word_ID)
    REFERENCES Words(Doc_ID,Word_ID)    
    ON DELETE CASCADE    
    ON UPDATE CASCADE    
);  

CREATE TABLE DocText(
	[Doc_ID] [int] NOT NULL,
	[Dtext] [text] NOT NULL,
	PRIMARY KEY (Doc_ID),    
CONSTRAINT FK_TextDoc FOREIGN KEY (Doc_ID)
    REFERENCES Docs_URL(ID)    
    ON DELETE CASCADE    
    ON UPDATE CASCADE
	);
