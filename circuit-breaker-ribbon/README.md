# Installation
 
oc login
oc new-project ribbon
oc policy add-role-to-user cluster-reader -n $(oc project -q) -z default

# Build & deploy

cd server
mvn clean package fabric8:deploy
cd ../client
mvn clean package fabric8:deploy