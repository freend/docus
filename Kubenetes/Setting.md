- 마스터로 가서 연결을 확인하자

```shell
[root@k8s-master ~] kubectl get nodes
NAME         STATUS   ROLES    AGE     VERSION
k8s-master   Ready    master   33m     v1.15.5
k8s-node1    Ready    <none>   17m     v1.15.5
k8s-node2    Ready    <none>   5m39s   v1.15.5
[root@k8s-master ~] kubectl get nodes -o wide
NAME         STATUS   ROLES    AGE     VERSION   INTERNAL-IP     EXTERNAL-IP   OS-IMAGE                KERNEL-VERSION                CONTAINER-RUNTIME
k8s-master   Ready    master   35m     v1.15.5   192.168.0.110   <none>        CentOS Linux 7 (Core)   3.10.0-1062.12.1.el7.x86_64   docker://18.6.2
k8s-node1    Ready    <none>   19m     v1.15.5   192.168.0.111   <none>        CentOS Linux 7 (Core)   3.10.0-1062.12.1.el7.x86_64   docker://18.6.2
k8s-node2    Ready    <none>   8m19s   v1.15.5   192.168.0.112   <none>        CentOS Linux 7 (Core)   3.10.0-1062.12.1.el7.x86_64   docker://18.6.2
```

- 백그라운드로 실행되게 설정한다

```shell
[root@k8s-master ~] nohup kubectl proxy --port=8001 --address=192.168.0.117 --accept-hosts='^*$' /dev/null 2>&1 &
# 이러고 바로 다음 명령을 해도 된다.
# 만약 Exit하고 나가버리게 되면 아이피 주소를 잘못 적은 것이다.
```

- Dash Board의 포트 상태를 확인한다

```shell
[root@k8s-master ~] netstat -nlp | grep 8001
tcp        0      0 192.168.0.110:8001      0.0.0.0:*               LISTEN      5734/kubectl
```

- Dash Board에 접속해 보자

```shell
http://192.168.0.117:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/
```

- 모든 Pod를 보는 방법

```shell
kubectl get pods --all-namespaces
NAMESPACE     NAME                                       READY   STATUS     RESTARTS   AGE
kube-system   calico-kube-controllers-56cd854695-5t7bx   1/1     Running            0          14m
kube-system   calico-node-lm4st                          1/1     Running            0          9m57s
kube-system   calico-node-lnp4h                          1/1     Running            0          14m
kube-system   calico-node-vj46p                          1/1     Running            0          11m
kube-system   coredns-5c98db65d4-lk4kw                   1/1     Running            0          17m
kube-system   coredns-5c98db65d4-rsdsc                   1/1     Running            0          17m
kube-system   etcd-k8s-master                            1/1     Running            0          16m
kube-system   kube-apiserver-k8s-master                  1/1     Running            0          16m
kube-system   kube-controller-manager-k8s-master         1/1     Running            0          16m
kube-system   kube-proxy-p8kpt                           1/1     Running            0          17m
kube-system   kube-proxy-vzz2k                           1/1     Running            0          9m57s
kube-system   kube-proxy-zlvl8                           1/1     Running            0          11m
kube-system   kube-scheduler-k8s-master                  1/1     Running            0          16m
# 문제있는 경우
kube-system   kubernetes-dashboard-6b8c96cf8c-st5cs      0/1     CrashLoopBackOff   6          11m
# 정상인 경우
kube-system   kubernetes-dashboard-6b8c96cf8c-526f8      1/1     Running   1          81s
# dash board의 로그를 볼수 있다.
[root@k8s-master ~] kubectl -n kube-system logs -f kubernetes-dashboard-6b8c96cf8c-st5cs
2020/02/21 12:15:49 Starting overwatch
2020/02/21 12:15:49 Using in-cluster config to connect to apiserver
2020/02/21 12:15:49 Using service account token for csrf signing
2020/02/21 12:16:19 Error while initializing connection to Kubernetes apiserver. This most likely means that the cluster is misconfigured (e.g., it has invalid apiserver certificates or service account's configuration) or the --apiserver-host param points to a server that does not exist. Reason: Get https://10.96.0.1:443/version: dial tcp 10.96.0.1:443: i/o timeout
Refer to our FAQ and wiki pages for more information: https://github.com/kubernetes/dashboard/wiki/FAQ
```

- 여기서 잠깐

```shell
# 위의 대시보드 로그에 보면 다음과 같은 메세지가 뜬다.
Reason: Get https://10.96.0.1:443/version: dial tcp 10.96.0.1:443: i/o timeout
```

 사실 저건 인터넷을 하루 종일 봤는데도 안되더라 그냥 네트워크가 달라서 그런 오류? 라고 알고 있는데 나는 해결 못하고 다시 설치했다.(이것을 위해 kudeadm init을 하기전에 스크린샷을 남겨놓길 추천한다.)

- 사실 reset도 해볼걸 그랬나 보다. 혹 해보신 분이 있으면 알려주시려나?

- 상세보기

```shell
# 상세 출력을 위한 Describe 커맨드
kubectl describe nodes my-node 
kubectl describe pods my-pod -n [pods name]
```

- 재설정 및 삭제 : 단 이것은 성공적으로 연결되었으면 하지 말아라(kubeadmi init때 정말 많이 함)

```shell
# 재설정
[root@k8s-master ~] kubeadm reset
[root@k8s-node1 ~] kubeadm reset
[root@k8s-node2 ~] kubeadm reset
[root@k8s-master ~] systemctl restart kubelet
[root@k8s-node1 ~] systemctl restart kubelet
[root@k8s-node2 ~] systemctl restart kubelet

# dashboard 삭제
[root@k8s-master ~] kubectl delete pod -n kube-system kubernetes-dashboard-6b8c96cf8c-9n55b
```

- Install GUI Dash Board

```shell
[root@k8s-master ~] kubectl apply -f https://raw.githubusercontent.com/kubetm/kubetm.github.io/master/sample/practice/appendix/gcp-kubernetes-dashboard.yaml
#url
http://127.0.0.1:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/#!/pod?namespace=default
```

- 사용자를 추가하는 부분이다.

```shell
[root@k8s-master ~] cat <<EOF | kubectl create -f -
 apiVersion: v1
 kind: ServiceAccount
 metadata:
   name: admin-user
   namespace: kube-system
EOF
serviceaccount/admin-user created
```

```shell
[root@k8s-master ~] cat <<EOF | kubectl create -f -
 apiVersion: rbac.authorization.k8s.io/v1
 kind: ClusterRoleBinding
 metadata:
   name: admin-user
 roleRef:
   apiGroup: rbac.authorization.k8s.io
   kind: ClusterRole
   name: cluster-admin
 subjects:
 - kind: ServiceAccount
   name: admin-user
   namespace: kube-system
EOF
clusterrolebinding.rbac.authorization.k8s.io/admin-user created
```

```
[root@k8s-master ~] kubectl -n kube-system describe secret $(kubectl -n kube-system get secret | grep admin-user | awk '{print $1}')
Name:         admin-user-token-jzqxv
Namespace:    kube-system
Labels:       <none>
Annotations:  kubernetes.io/service-account.name: admin-user
              kubernetes.io/service-account.uid: d77859a7-19b6-4943-a8ff-fc1eb00bc234

Type:  kubernetes.io/service-account-token

Data
====
ca.crt:     1025 bytes
namespace:  11 bytes
token:      eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJhZG1pbi11c2VyLXRva2VuLWp6cXh2Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6ImFkbWluLXVzZXIiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiJkNzc4NTlhNy0xOWI2LTQ5NDMtYThmZi1mYzFlYjAwYmMyMzQiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZS1zeXN0ZW06YWRtaW4tdXNlciJ9.HLcFWSA_kBFOEStR4QyNYzzcz0Z10hJRTF951wB3UO3ZHyVVkpmk816N9YUVZX_sDoDhtGX_8Ift_g6VqfZB9P8EmdaOoytZ4WRXvQnlR0eu1H0Cx0QPtkbtpEFJJa5u1rb87o6tQE-xuvhe-OeT5z5HMf9tbEi-DmjSKqhpqAtZ7RBP063lKe3g7zbhRy-kF2gXEOmxGR2TBsmvw-atnRMnxFcVWtQnHkob5i74j7Way1b9vuUPKn8P6eH0QepKdpgSj0agrFmWzvHwqh31dd6vzkbJ2n9Ab1grMFksddnQaoQTSI2hEFOfEwlBxdsp3byHEA1wa1rqZo2PX8peRQ
```



- 참고자료

[kubernetes token](https://sarc.io/index.php/cloud/1383-join-token)

- kubelet 이 오류인 경우 : journalctl -u kubelet 으로 살펴봐라
- 인증서 갱신

```shell
# create token
kubeadm token create
# hash value
openssl x509 -pubkey -in /etc/kubernetes/pki/ca.crt | openssl rsa -pubin -outform der 2>/dev/null | openssl dgst -sha256 -hex | sed 's/^.* //'
# node join
kubeadm join <Kubernetes API Server:PORT> --token <2. Token 값> --discovery-token-ca-cert-hash sha256:<3. Hash 값>
```

