# Installation

```
oc login
oc new-project ribbon
oc policy add-role-to-user cluster-reader -n $(oc project -q) -z default
```

# Build & deploy
```
mvn clean package fabric8:deploy
```

# Call the Client

```
export CLIENT=$(oc get route/client -o yaml | grep -m 1 'host:' | cut -c9-)
curl http://$CLIENT/hi
```

# Report

- Test using only Ribbon + Loadbalancing is working if you increase the number of replica of the `backend` before to start the `client`
- Test fails using @HystrixCommand + @EnableCircuitBreaker annotations. This error is reported - https://gist.github.com/cmoulliard/7617874e05de2b455f0edf4515a13ed7