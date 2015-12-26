Demo project for SEC 


java -cp events-producer-1.0-SNAPSHOT.jar com.cep.jms.Publisher NumberReadMessages NumberShareArticleArticles

will Publish read article and share article events with userId = "user" + random(10) and articleId = "article" + random(5000)

*EventPublisher.ARTICLE_COUNT and USER_COUNT variables*


java -cp events-producer-1.0-SNAPSHOT.jar com.cep.jms.Receiver

will receive and print all these event



Cassandra tables: 

 CREATE KEYSPACE cep_demo WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1} ;
Use cep_demo ;

1) Stocks data

CREATE TABLE stocks (stock_id text PRIMARY KEY, stock_name text) ;

COPY stocks FROM  'workspace/SEC/data/stocklist.csv' WITH HEADER = true;


Create stockIds by articleId data

Create table article_tags (articleId text, stockId text, PRIMARY KEY(articleId, stockId));

java -cp target/data-generator-1.0NAPSHOT.jar com.cep.data.ArticleTagsGenerator <number of articles> 

ArticleTagsGenerator will generate N articles and assignt from 1 to 5 random tags to it. 
Default N is 5000.

2) User interest data 

CREATE TABLE user_interests ( userId text, stockId text, interest counter, PRIMARY KEY(userId, stockId));

3) Users interested in some stock

CREATE TABLE users_by_stock( stockId text, userId text,  interest counter, PRIMARY KEY(stockId, userId));


TODO: 

2) Data feed that push price data to queue "PRICE"
One message will have fields
String stockId  - somethign like "stock" + randomInt - or random item from long list
long timestamp
double price

3) UserUI - consol that will listen to user's queue and print out notifications that pushed by spark jobs 

4) Spark job should be able to save to cassandra all stuff that comming from EVENT and PRICE queues

5) Based on EVENT messages update count for each user for each tag - Update should go to two tables - user_interests and users_by_stock

 UPDATE user_interests SET interest = interest +1 where userid = 'user1' and stockid = 'stock1';
 
  UPDATE users_by_stock SET interest = interest +1 where userid = 'user1' and stockid = 'stock1';


6) Based on PRICE - if delta is higher than some limit - query to find who is interested in this stock (value > some variable)

Second iteration

7) Based on PRICE - if delta is higher than some limit - query to find a user whose first most interested stock is chnaged one







