
use search_engine;

CREATE TABLE Docs_URL (
ID int NOT NULL IDENTITY(1,1),
Title varchar(50), 
URL varchar(2085) NOT NULL UNIQUE,
CONSTRAINT DOCS_ID UNIQUE(URL),
Visited int,
linkRank int,
pageRank float,
rankedBit bit,
primary key(ID)
);

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

INSERT into Docs_URL (URL,Visited,linkRank,pageRank, rankedBit)
VALUES ('https://www.theguardian.com',0,0,1,0);

INSERT into Docs_URL (URL,Visited,linkRank,pageRank, rankedBit)
VALUES ('http://www.bbc.com/news',0,0,1,0);

INSERT into Docs_URL (URL,Visited,linkRank,pageRank, rankedBit)
VALUES ('http://www.bbc.com/news/world',0,0,1,0);

INSERT into Docs_URL (URL,Visited,linkRank,pageRank, rankedBit)
VALUES ('http://www.bbc.com/news/business',0,0,1,0);

INSERT into Docs_URL (URL,Visited,linkRank,pageRank, rankedBit)
VALUES ('https://www.bbc.com/sport',0,0,1,0);

INSERT into Docs_URL (URL,Visited,linkRank,pageRank, rankedBit)
VALUES ('https://www.bbc.com/weather',0,0,1,0);

INSERT into Docs_URL (URL,Visited,linkRank,pageRank, rankedBit)
VALUES ('https://www.bbc.co.uk/music',0,0,1,0);

INSERT into Docs_URL (URL,Visited,linkRank,pageRank, rankedBit)
VALUES ('https://www.bbc.com/food',0,0,1,0);

INSERT into Docs_URL (URL,Visited,linkRank,pageRank, rankedBit)
VALUES ('https://www.theguardian.com/us/sport',0,0,1,0);

INSERT into Docs_URL (URL,Visited,linkRank,pageRank, rankedBit)
VALUES ('https://www.theguardian.com/us-news/us-politics',0,0,1,0);

INSERT into Docs_URL (URL,Visited,linkRank,pageRank, rankedBit)
VALUES ('https://www.theguardian.com/world',0,0,1,0);

INSERT into Docs_URL (URL,Visited,linkRank,pageRank, rankedBit)
VALUES ('https://edition.cnn.com',0,0,1,0);

INSERT into Docs_URL (URL,Visited,linkRank,pageRank, rankedBit)
VALUES ('https://edition.cnn.com/politics',0,0,1,0);

INSERT into Docs_URL (URL,Visited,linkRank,pageRank, rankedBit)
VALUES ('https://edition.cnn.com/sport',0,0,1,0);

INSERT into Docs_URL (URL,Visited,linkRank,pageRank, rankedBit)
VALUES ('https://edition.cnn.com/health',0,0,1,0);

INSERT into Docs_URL (URL,Visited,linkRank,pageRank, rankedBit)
VALUES ('https://en.wikipedia.org/wiki/Main_Page',0,0,1,0);

