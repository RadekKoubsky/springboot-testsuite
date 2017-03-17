# Installation

* Log to OpenShift platform and create a new project

```
oc login
oc new-project ribbon
oc policy add-role-to-user cluster-reader -n $(oc project -q) -z default
```
* Git clone the project 


# Build & deploy
```
mvn clean package fabric8:deploy
```

# Call the Client

```
export CLIENT=$(oc get route/client -o yaml | grep -m 1 'host:' | cut -c9-)
curl http://$CLIENT/hi
```