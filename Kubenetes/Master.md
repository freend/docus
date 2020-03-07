## 마스터

**주의 : 무조건 마스터 설치완료 전에 node 건들지 마라 안그러면 dash board가 node1에 설치되기도 한다. 사실 그 이후 확인은 못했지만 가능하면 대시보드 까지 마스터에 있는게 낳지 않을까 싶다.**

- 우선 vm을 두개 복제하자 복제할때 ip, mac 별도로 설정하고 추가적으로 완전한 복제를 하자
- 쿠버어드민을 시작하자.

```shell
[root@k8s-master ~] swapoff -a # 이건 뭐 필수다.
[root@k8s-master ~] kubeadm init
```

- 여기서 Warning, Error이 발생하면 무조건 init을 실패한다. 주로 다음의 경우였다

  - swapoff -a을 안한 경우
  - [ERROR Port-2380] : Port 2380 is in use : kubeadm reset으로 초기화 시켜야한다.
  - cpu 2 more... : virtual machine의 cpu를 2개로 변경한다
  
  - Initial timeout of 40s passed 이건 머 여러가지 경우가 있다. 
  - 포트 오픈 : 참고사이트에서 열라는대로 열었는데 안된다. 이유는 모르겠다. 그래서 전부 다 열었다.
    - hosts설정 : 설치의 hosts설정이라는 부분을 참고하자.
  
- 결국 init가 종료되고 다음과 같은 token이 나온다

```shell
kubeadm join 192.168.0.117:6443 --token q9884c.01f0tfhe2943axdk \
    --discovery-token-ca-cert-hash sha256:4a6d0c86ad583f26a3cfd477d37e1c0d9dc69d1bcd5b46c9131d85908ccfb75c
```

- 다음의 명령어를 실행해준다.

```shell
# 여기의 역활은 나중에 꼭 확인해보자
[root@k8s-master ~] mkdir -p $HOME/.kube
[root@k8s-master ~] cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
[root@k8s-master ~] chown $(id -u):$(id -g) $HOME/.kube/config

# calico와 coredns 관련 Pod의 Status가 Running인지 확인
[root@k8s-master ~] curl -O https://docs.projectcalico.org/v3.9/manifests/calico.yaml
[root@k8s-master ~] kubectl apply -f calico.yaml
[root@k8s-master ~] kubectl get pods --all-namespaces
```

- dashboard plugin 설치

```shell
# kubectl apply를 사용하여 하나 이상의 서비스를 적용하는 경우
kubectl apply -f https://raw.githubusercontent.com/kubetm/kubetm.github.io/master/sample/practice/appendix/gcp-kubernetes-dashboard.yaml
# 이렇게 적용한 서비스는 삭제할 수 있다.
# dash board가 계속 에러가 나서 삭제하고 다시 설치해보았다.
kubectl delete -f https://raw.githubusercontent.com/kubetm/kubetm.github.io/master/sample/practice/appendix/gcp-kubernetes-dashboard.yaml
```