Demo project for SEC 


java -cp events-producer-1.0-SNAPSHOT.jar com.cep.jms.Publisher NoReadMessages NoShareArticleArticles

will Publish read article and share article events with userId = "user" + random(10) and articleId = "article" + random(10)


java -cp events-producer-1.0-SNAPSHOT.jar com.cep.jms.Receiver

will receive and print all these event



Cassandra tables: 

 CREATE KEYSPACE cep_demo WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1} ;
Use cep_demo ;

1) Stocks data

CREATE TABLE stocks (stock_id text PRIMARY KEY, stock_name text) ;

COPY stocks FROM  'workspace/SEC/data/stocklist.csv' WITH HEADER = true;


1) Get stockIds by articleId

Create table article_tags (articleId text, stockId text, PRIMARY KEY(articleId, stockId));

COPY

TODO: 

1) Data generator that will generate article -> tags table and keeps it in cassandra (static table, generated once)

article1 -> stock1, stock2, stock5
.....

2) Data feed that push price data to queue "PRICE"
One message will have fields
String stockId  - somethign like "stock" + randomInt - or random item from long list
long timestamp
double price

3) UserUI - consol that will listen to user's queue and print out notifications that pushed by spark jobs 

4) Spark job should be able to save to cassandra all stuff that comming from EVENT and PRICE queues

5) Based on EVENT messages update count for each user for each tag

6) Based on PRICE - if delta is higher than some limit - query to find who is interested in this stock (value > some variable)

Second iteration

7) Based on PRICE - if delta is higher than some limit - query to find a user whose first most interested stock is chnaged one





