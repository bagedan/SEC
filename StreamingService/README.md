# cep-streaming

## steps to start streaming

#### step1 , start datastax :
    sudo dse cassandra -k 

#### step2, package jar file :
    cp workspace/SEC/StreamingService   
    mvn package -e

#### step3, submit two streaming to cluster:
    cp /home/ec2-user/workspace/SEC/StreamingService/target/StreamingService-0.0.1-SNAPSHOT.jar /home/ec2-user/workspace/SEC/StreamingService/target/user.jar
    
    cp /home/ec2-user/workspace/SEC/StreamingService/target/StreamingService-0.0.1-SNAPSHOT.jar /home/ec2-user/workspace/SEC/StreamingService/target/price.jar

    dse spark-submit --master spark://127.0.0.1:7077 --deploy-mode cluster --class com.cep.streaming.runner.PriceRunner /home/ec2-user/workspace/SEC/StreamingService/target/user.jar
    
    dse spark-submit --master spark://127.0.0.1:7077 --deploy-mode cluster --class com.cep.streaming.runner.PriceRunner /home/ec2-user/workspace/SEC/StreamingService/target/price.jar


## spark UI port : 7080 
