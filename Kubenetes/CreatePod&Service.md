## Kubernetes DashBoard

### Create Pod

- 전제조건
  - 문법에 다 맞는데 안되면 맨 처음부터 띄어쓰기를 한번씩 점검해주면 된다.
- Pod 만들기

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: k8s-nginx-pod # kubernetes name
  labels:
	app: hi-nginx
spec:
  containers:
  - name: nginx-container
	image: freend/my-nginx-image # docker repository
	ports:
	- containerPort: 80
```

- Service 만들기

```yaml
apiVersion: v1
kind: Service
metadata:
  name: hi-nginx-service
spec:
  selector:
	app: hi-nginx # pod app value
  ports:
	- port: 80 # service connect pore 여길 전부 복사 붙어넣기 하면 이게 4칸 이동한다 사실 2칸이다.
  	targetPort: 80 # pod의 containerPort
  externalIPs:
    - 192.168.0.128 # pod가 올라간 node-IP
```

- 서비스에 보면 hi-nginx-service라는 것이 보일 것이다. 이것의 외부 엔드 포인트를 누르면 해당 이미지가 실행되었음을 알 수 있다.
- 이렇게 해서 도커의 이미지를 Pod로 다운받고 service로 외부와 연결한 후 그것을 확인하는 과정까지 알아봤다.

### GCP에서의 Pod & Service

- Pod 만들기

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: k8s-nginx-pod # kubernetes name
  labels:
	app: hi-nginx
spec:
  containers:
  - name: nginx-container
	image: freend/my-nginx-image # docker repository
	ports:
	- containerPort: 80
```

- Service 만들기

```yaml
apiVersion: v1
kind: Service
metadata:
  name: hi-nginx-service
spec:
  selector:
    app: hi-nginx # pod app value
    type: LoadBalancer # 이 부분에 대한 설명은 하단에
  ports:
  - port: 80 # service connect pore
    targetPort: 80 # pod의 containerPort
  externalIPs:
  - 192.168.0.128 # pod가 올라간 node-IP
```

### Service Type

서비스는 IP주소 할당 방식과 뎐동 서비스등에 따라 크게 4가지로 구별할 수 있다.

- Cluster IP
- Load Balancer
- Node IP
- External name

#### Cluster IP

디폴트 설정으로, 서비스에 클러스터 IP(soqn IP)를 할당한다. 쿠버네티스 클러스터 내에서는 이 서비스에 접근이 가능하지만, 클러스터 외부에서는 외부 IP를 할당받지 못했기 때문에 접근이 불가능하다.

#### Load Balancer

보통 클라우드 벤더에서 제공하는 설정 방식으로, 외부 IP를 가지고 있는 로드밸런서를 할당한다. 외부 IP를 가지고 있기 때문에 클러스터 외부에서 접근이 가능하다.

#### Node Port

클러스터 IP로만 접근이 가능한것이 아니라, 모든 노드의 IP와 포트를 통해서도 접근이 가능하게 된다. 예를 들어 아래와 같이 hello-node-svc라는 서비스를 NodePort타입으로 선언을 하고, nodePort를 30036으로 설정하면, 아래 설정에 따라 클러스터 IP의 80포트로도 접근이 가능하지만, 모든 노드의 30036 포트로도 서비스를 접근할 수 있다.

```yaml
appVersion: v1
kind: Service
metadata:
  name: hello-node-svc
spec:
  selector:
    app: hello-node
  type: NodePort
  Ports:
  - name: http
    port: 80
    protocol: TCP
    targetPort: 8080
    nodePort: 30036
```

### Mongo DB 설치

- Pod

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: mongo-pod
  labels:
    app: hi-mongo
spec:
  containers:
  - name: mongodb-container
    image: mongo:latest
    ports:
    - containerPort: 27017
```

- Service

```yaml
apiVersion: v1
kind: Service
metadata:
  name: mongo-svc
spec:
  selector:
    app: hi-mongo
  ports:
    - port: 37017
      targetPort: 27017
  externalIPs:
    - 192.168.0.119
```

