# Installation
 
oc login
oc new-project ribbon

oc policy add-role-to-user view -n $(oc project -q) -z default
oc policy add-role-to-user view system:serviceaccount:$(oc project -q):default -n $(oc project -q)
oc policy add-role-to-user cluster-reader -n $(oc project -q) -z default


# Build

mvn clean package fabric8:resource fabric8:build

# Deploy

cd it
mvn fabric8:deploy
