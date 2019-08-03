#  redis-idempotency-app

Add instructions for project developers here.

Requirements

Docker  -> Redis Images
Java    -> Jdk 8
Maven   -> v3.6.1


# Redis Docker Local

docker run --name redis --rm -d -p 6379:6379 --network=host redis redis-server --appendonly yes


# App Java Local

mvn clean package -DskipTests camel:run -Dperiod=60

Or

java -jar -Dperiod=60 target/redis-idempotency-app-1.0-SNAPSHOT.jar


# App React.js Local

sudo npm install
sudo npm build
sudo npm start

# App Java in Docker (Depends Redis running Local)

docker build -t redis-idempotency-app .
docker run -d --rm --name redis-idempotency redis-idempotency-app:latest


# Redis Docker Local

docker run --name redis --rm -d -p 6379:6379 --network=host redis redis-server --appendonly yes


# Minishift command
minishift start --vm-driver virtualbox --show-libmachine-logs

#Postman link to K8s (Ingress)

http://redis-idempotency.192.168.99.100.nip.io/idempotency/v1/list

# Useful links
https://www.devcon5.ch/en/blog/2017/10/25/optimizing-docker-images-for-java/
http://whitfin.io/speeding-up-maven-docker-builds/