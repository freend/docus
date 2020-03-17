### Node 설정

- 복제한 각각의 hostname을 변경한다

```shell
[root@k8s-master ~] hostnamectl set-hostname k8s-node1
[root@k8s-master ~] hostnamectl set-hostname k8s-node2
```

- /etc/hosts를 설정해 준다

```shell
# node1
127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4
::1         localhost localhost.localdomain localhost6 localhost6.localdomain6
192.168.0.128 k8s-node1
[root@k8s-node1 ~] reboot # 재부팅 해주자
# node2
127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4
::1         localhost localhost.localdomain localhost6 localhost6.localdomain6
192.168.0.119 k8s-node2
[root@k8s-node2 ~] reboot # 재부팅 해주자
```

- swap off and kubelet 실행

```shell
[root@k8s-node* ~] swapoff -a
[root@k8s-node* ~] systemctl enable --now kubelet
[root@k8s-node* ~] echo '1' > /proc/sys/net/ipv4/ip_forward
```

- kubeadm join

```shell
kubeadm join 192.168.0.117:6443 --token lmn97r.xepa9j3tyfkvxv5j \
    --discovery-token-ca-cert-hash sha256:5898d0d1eaa56379486f80dfd2a4931d932389c69acc75af1c6933882017b88b
```

- 아래와 같은 오류 발생시 대처방법

```shell
[root@k8s-node1 ~] kubeadm join 192.168.0.117:6443 --token dvc2wf.30awkb7gl3aqraz8 \
    --discovery-token-ca-cert-hash sha256:e4e60855a35c527b34b9c25fc4fdd85ff7616ede7ae1f196ed853a17ae4befae
[preflight] Running pre-flight checks
error execution phase preflight: [preflight] Some fatal errors occurred:
	[ERROR FileAvailable--etc-kubernetes-kubelet.conf]: /etc/kubernetes/kubelet.conf already exists
	[ERROR FileAvailable--etc-kubernetes-bootstrap-kubelet.conf]: /etc/kubernetes/bootstrap-kubelet.conf already exists
	[ERROR Port-10250]: Port 10250 is in use
	[ERROR FileAvailable--etc-kubernetes-pki-ca.crt]: /etc/kubernetes/pki/ca.crt already exists
[preflight] If you know what you are doing, you can make a check non-fatal with `--ignore-preflight-errors=...`
# 위와 같은 오류 발생시 아래의 명령을 사용한다.
[root@k8s-node1 ~] kubeadm reset --force
```

- 솔직히 여기는 이후에 건드릴 일이 없다. 있다면 재부팅 했을때 정도인데. 꼭 swapoff -a을 해줘야 한다.