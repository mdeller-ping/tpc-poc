# tpc-poc
  
Framework for a high performance PingFederate cluster.

There will be 1 admin console and 2 or more runtime engines.

## prerequisites

Kubernetes stuff

## Clone this repo

```text
$ git clone https://github.com/mdeller-ping/tpc-poc.git
```

## Create console-variables configmap
Customize kubernetes/console-variables with your environment specific information and then create the configmap

```text
$ kubectl create configmap console-variables --from-env-file=pkubernetes/console-variables
```

## Create engine-variables configmap
Customize kubernetes/engine-variables with your environment specific information and then create the configmap

```text
$ kubectl create configmap engine-variables --from-env-file=pkubernetes/engine-variables
```

## Apply

```text
$ kubectl apply -f cluster.yaml
```

## See the things

```text
$ kubectl get all
```

```text
NAME                                      READY   STATUS    RESTARTS   AGE
pod/pf-cluster-console-xxx                1/1     Running   0          94m
pod/pf-cluster-engine-xxx                 1/1     Running   0          94m
pod/pf-cluster-engine-xxx                 1/1     Running   0          94m

NAME                             TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)    AGE
service/pf-admin-loadbalancer    ClusterIP   192.168.1.5      <none>        9999/TCP   94m
service/pf-cluster-dns           ClusterIP   None             <none>        7600/TCP   94m
service/pf-engine-loadbalancer   ClusterIP   192.168.1.8      <none>        9031/TCP   94m

NAME                                 READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/pf-cluster-console   1/1     1            1           94m
deployment.apps/pf-cluster-engine    2/2     2            2           94m

NAME                                            DESIRED   CURRENT   READY   AGE
replicaset.apps/pf-cluster-console-xxx          1         1         1       94m
replicaset.apps/pf-cluster-engine-xxx           2         2         2       94m
```

## See your pods

```text
$ kubectl get pods -o wide
```

```text
NAME                                  READY   STATUS    RESTARTS   AGE    IP               NODE                                           NOMINATED NODE   READINESS GATES
pf-cluster-console-6d868fcf7d-chvdh   1/1     Running   0          108m   192.168.54.124   ip-192-168-41-213.us-east-2.compute.internal   <none>           <none>
pf-cluster-engine-7b668d769f-bqskb    1/1     Running   0          108m   192.168.9.12     ip-192-168-21-89.us-east-2.compute.internal    <none>           <none>
pf-cluster-engine-7b668d769f-njlqw    1/1     Running   0          108m   192.168.50.125   ip-192-168-41-213.us-east-2.compute.internal   <none>           <none>
```

## Manually scale up or down

```text
$ kubectl scale deployments/pf-cluster-engine --replicas=10
deployment.extensions/pf-cluster-engine scaled

$ kubectl get pods

NAME                                  READY   STATUS              RESTARTS   AGE
pf-cluster-console-6d868fcf7d-chvdh   1/1     Running             0          110m
pf-cluster-engine-7b668d769f-8phvr    1/1     Running             0          3s
pf-cluster-engine-7b668d769f-bpqjp    0/1     ContainerCreating   0          3s
pf-cluster-engine-7b668d769f-bqskb    1/1     Running             0          110m
pf-cluster-engine-7b668d769f-csfs8    1/1     Running             0          3s
pf-cluster-engine-7b668d769f-fdcfs    1/1     Running             0          3s
pf-cluster-engine-7b668d769f-hrm8v    1/1     Running             0          3s
pf-cluster-engine-7b668d769f-lfxbx    1/1     Running             0          3s
pf-cluster-engine-7b668d769f-mbl98    1/1     Running             0          3s
pf-cluster-engine-7b668d769f-njlqw    1/1     Running             0          110m
pf-cluster-engine-7b668d769f-pt869    1/1     Running             0          3s

$ kubectl scale deployments/pf-cluster-engine --replicas=2
deployment.extensions/pf-cluster-engine scaled

$ kubectl get pods

NAME                                  READY   STATUS        RESTARTS   AGE
pf-cluster-console-6d868fcf7d-chvdh   1/1     Running       0          112m
pf-cluster-engine-7b668d769f-8phvr    0/1     Terminating   0          116s
pf-cluster-engine-7b668d769f-bpqjp    0/1     Terminating   0          116s
pf-cluster-engine-7b668d769f-bqskb    1/1     Running       0          112m
pf-cluster-engine-7b668d769f-csfs8    0/1     Terminating   0          116s
pf-cluster-engine-7b668d769f-fdcfs    0/1     Terminating   0          116s
pf-cluster-engine-7b668d769f-hrm8v    0/1     Terminating   0          116s
pf-cluster-engine-7b668d769f-lfxbx    0/1     Terminating   0          116s
pf-cluster-engine-7b668d769f-mbl98    0/1     Terminating   0          116s
pf-cluster-engine-7b668d769f-njlqw    1/1     Running       0          112m
pf-cluster-engine-7b668d769f-pt869    0/1     Terminating   0          116s
```

## View a log file

```text
kubectl logs -f pod/pf-cluster-console-xxx
```
