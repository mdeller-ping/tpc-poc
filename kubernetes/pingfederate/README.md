## Pre-Requisite

* envsubst
* generate [devops-secret](./devops-secret.yaml)

## Run 

This uses an environment variable to ensure clustering works in your namespace. 

Run this with:
```
kustomize build . | envsubst '${PING_IDENTITY_K8S_NAMESPACE}' | kubectl apply -f -
```

remove it with: 
```
kustomize build . | envsubst '${PING_IDENTITY_K8S_NAMESPACE}' | kubectl delete -f -
```

