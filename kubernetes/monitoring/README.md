This section should be run with:

`kustomize build . | envsubst '${PING_IDENTITY_K8S_NAMESPACE}' | kubectl apply -f -`

> note: if your engine container is in a fail loop: [troubleshoot](#troubleshooting)

### Verify
A `kubectl get all` will return nearly the same as if you are just deploying the PF cluster, but the engine `READY` field should show `2/2`

This indicates `telegraf is successfully running next to pingfed.

### Explore
To view the metrics pulled from Telegraf, navigate to: https://chronograf.anydevops.com/sources/5000/chronograf/data-explorer

Sample: You can create a query to see the PF Pod CPU by clicking: `pingfed-tpc.autogen` > `cpu` > `host` > {your-pod-name} > `usage_user`


### troubleshooting

If the PF engine gets caught in a fail loop, it may be because of envsubst incorrectly substituting. 
To verify if this is the issue, run:
```
echo '$PING_IDENTITY_K8S_NAMESPACE $FOO' | envsubst '${PING_IDENTITY_K8S_NAMESPACE}'
```
This should return similar to: 
```
ping-cloud-devops-eks-<user> $FOO
```

if you don't see `$FOO`, you need to reinstall envsubst:
```
brew reinstall gettext
brew link --overwrite --force gettext
```