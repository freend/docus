## Kubenetes

- Pod : Container 관리 Container입장에서는 이것이 가상머신임
- Label : 각각을 key, value로 관리함.
- kubeadm init시 ip를 줄 수도 있다. 안하면 kubenetes에서 임의로 설정함.

### 시작하기 전에

#### 네트워크 구성

- master : 192.168.0.117
- Node1 : 192.168.0.128
- Node2 : 192.168.0.119

[버철 피씨에 센트 OS 설치하기](../Centos/InstallOs.md)

### 사전 설정

- 호스트 네임 설정

```
[root@localhost ~] hostnamectl set-hostname k8s-master
```

- update

```shell
[root@k8s-master ~] yum update
```

- 방화벽 해제

```shell
[root@k8s-master ~] systemctl disable firewalld #방화벽을 끄자. 일부만 허용해도 된다는데 난 계속 안되더라
# 방화벽은 아래 참고사이트에 또다른 설치방법을 설명하는 페이지에서 볼 수 있다.
```

- 여기서 잠깐!!! 뒤에 설치할때 오류가 발생 할 수도 있다. 이 곳을 한번 확인하라

```shell
[root@k8s-master ~] cat /etc/resolv.conf
# 이게 없다면 추가해주자.
nameserver 8.8.8.8 
nameserver 8.8.4.4
```

여기서 여러분들이 문제가 될 수 있는게 존재한다. 바로 네트워크가 바뀌면 네트워크에 따라서 아이피가 바뀐다는 문제이다. 특히나 마스터는 크게 문제가 될 수 있다 추후 노드를 연결할 때 마스터의 아이피를 가지고 조인하기 때문이다. 그래서 여기서 마스터의 아이피를 고정시켜야 한다. 이때 사용할 수 있는게 바로 NMTUI라는 아이피 설정 명령이다.

[아이피 설정하기](../Centos/nmtui.md)

### 설치하기

- hosts 설정

```shell
[root@k8s-master ~] nano /etc/hosts
127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4
::1         localhost localhost.localdomain localhost6 localhost6.localdomain6
192.168.0.117 k8s-master #이 부분을 추가했다. 나의 아이피에 k8s-master이라는 호스트 네임을 준다.
```

- k8s.conf 설정

```shell
[root@k8s-master ~] cat <<EOF >  /etc/sysctl.d/k8s.conf
> net.bridge.bridge-nf-call-ip6tables = 1
> net.bridge.bridge-nf-call-iptables = 1
> EOF
[root@k8s-master ~] sysctl --system
```

- Kubernetes repository 설정

```shell
[root@k8s-master ~] cat <<EOF > /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://packages.cloud.google.com/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=1
repo_gpgcheck=1
gpgkey=https://packages.cloud.google.com/yum/doc/yum-key.gpg 
https://packages.cloud.google.com/yum/doc/rpm-package-key.gpg
EOF
```

- Repo 설정

```shell
[root@k8s-master ~] cd /etc/yum.repos.d/
[root@k8s-master yum.repos.d] nano Daum.repo
[base]
name=CentOS-$releasever - Base
baseurl=http://ftp.daumkakao.com/centos/$releasever/os/$basearch/
gpgcheck=0
[updates]
name=CentOS-$releasever - Updates
baseurl=http://ftp.daumkakao.com/centos/$releasever/updates/$basearch/
gpgcheck=0
[extras]
name=CentOS-$releasever - Extras
baseurl=http://ftp.daumkakao.com/centos/$releasever/extras/$basearch/
gpgcheck=0
```

- 프로그램 설치

```shell
[root@k8s-master ~] yum install -y yum-utils device-mapper-persistent-data lvm2

[root@k8s-master ~] yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
```

- 도커 설치 및 설정

```shell
[root@k8s-master ~] yum -y install docker-ce-18.06.2.ce

[root@k8s-master ~] mkdir /etc/docker
[root@k8s-master ~] cat > /etc/docker/daemon.json <<EOF
{
  "exec-opts": ["native.cgroupdriver=systemd"],
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "100m"
  },
  "storage-driver": "overlay2",
  "storage-opts": [
    "overlay2.override_kernel_check=true"
  ]
}
> EOF
```

- docker daemon reload and service start

```shell
[root@k8s-master ~] systemctl daemon-reload 
[root@k8s-master ~] systemctl enable --now docker
```

- install Kubernetes

```shell
[root@k8s-master ~] yum install -y --disableexcludes=kubernetes kubeadm-1.15.5-0.x86_64 kubectl-1.15.5-0.x86_64 kubelet-1.15.5-0.x86_64
[root@k8s-master ~] systemctl enable --now kubelet
[root@k8s-master ~] swapoff -a
# system on auto start
[root@k8s-master ~] systemctl enable kubelet
```

- 여기서 1차 고생. 이렇게 했는데 kubelet이 안된다는 오류가 뜨기 시작했다. 인터넷으로 여기저거 해결책을 찾아봤는데 결국 이렇게 해서 해결했다. [관련링크](https://cloud.google.com/sdk/docs/downloads-interactive)

```shell
[root@k8s-master ~] tee -a /etc/yum.repos.d/google-cloud-sdk.repo << EOM
[google-cloud-sdk]
name=Google Cloud SDK
baseurl=https://packages.cloud.google.com/yum/repos/cloud-sdk-el7-x86_64
enabled=1
gpgcheck=1
repo_gpgcheck=1
gpgkey=https://packages.cloud.google.com/yum/doc/yum-key.gpg
       https://packages.cloud.google.com/yum/doc/rpm-package-key.gpg
EOM
[root@k8s-master ~] yum install google-cloud-sdk
```

- 다시 위의 install Kubernetes를 하면 정상 설치한다.
- 그리고 뒤에 언급하겠지만 나는 대시보드를 설치하면서 하루 종일 고생했다. 가능하면 여기서(버철pc를 쓴다는 가정하에) 스냅샷을 찍어두자.
- [쿠버네티스 설치 참고사이트 1](http://www.cubrid.com/blog/3820603)
- [쿠버네티스 설치 참고사이트 2](https://waspro.tistory.com/506)
