# tpc-poc
  
Framework for a high performance PingFederate cluster.

There will be 1 admin console and 2 or more runtime engines.

## prerequisites

Kubernetes stuff

## Clone this repo
```text
git clone https://github.com/mdeller-ping/tpc-poc.git
```

## Create configmap
This file is not part of the repo.  It contains your Ping Identity DevOps keys and should be kept secure.

```text
kubectl create configmap private-variables --from-env-file=private-variables
```

## Apply

```text
kubectl apply -f pingfederate-cluster.yaml
```
