# ECS에 서비스 올리기

## 클러스터 생성하기

### AWS Cli에서 클러스터 생성하기

- 클러스터의 이름은 ecs-cluster라고 칭하겠다.

```shell
# 클러스터 생성은 이거 하나면 된다.
aws ecs create-cluster --cluster-name ecs-cluster
```

- 위와 같이 클러스터를 생성하면 빈 클러스터가 생성된다.
- ami찾는 법

  - [다음 페이지로 간다](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ecs-optimized_AMI.html)
  - 아래쪽으로 가면 'View the AMI IDs on one of the following tabs, according to the variant you choose.' 메세지와 함께 AMI를 찾을 수 있는 부분이 있다.
  - 거기서 Amazon Linux2, Amazon Linux ami를 선택한 후 나에게 맞는 리전을 찾아서 옆의 링크를 클릭한다.
  - 맨 아래 value(값) 이라고 되어 있고 'ami-00c723a006da425f4' 이런식으로 표현된 게 있는데 이것이 ami이다.

### AWS Console에서 클러스터 생성하기

- AWS Console에서 ECS 선택 후 클러스터 만들기를 선택한다.
- 추후엔 Fargate를 쓸수 있으나 참고사이트의 설정을 따라가 보기로 했다. EC2-Linux + 네트워킹을 선택 후 아래 다음 선택
- 다음과 같이 설정했다.

  -  클러스터 이름 : ecs-cluster

  - EC2 인스턴스 유형 : T3.micro

  - 인스턴스 개수 : 2

  - EC2 AMI ID : Amazon Linux2 AMI(여기서 AMI를 확인해서 찾을 수 있었다)

  -  EBS 스토리지 : 22
  - 키 페어 : 내가 가지고 있는 키페어(혹시나 하는 마음에 넣었다)

  - VPC : Public VPC

  - 서브넷 : 서브넷은 있는 만큼에서 선택할 수 있다
    - Public VPC에 연결되어 있는 서브넷

  -  Secutiry Group : ClusterSecurity

  - IAM : ecsInstanceRole(새로 생성하면 이것이 생긴다 그후 IAM에서 EC2ContainerServiceAutoScaleRole을 추가해 주자)
- 한국 리전에서는 Amazon Linux2, Amazon Linux1을 지원한다.
- 다 만든 후 EC2를 보면 T3.micro의  2개의 인스턴스가 생성되고 실행됨을 확인할 수 있다.

## 태스크 디피니션

- 태스크를 실행할 때 컨테이너 네트워크 모드, 태스크 역할Task Role, 도커 이미지, 실행 명령어, CPU 제한, 메모리 제한 등 다수의 설정이 필요합니다. 컨테이너 오케스트레이션에서는 컨테이너가 필요에 따라서 자동적으로 실행되거나 종료될 수 있습니다. 따라서 매번 이러한 설정들을 지정하기보다는, 미리 설정들의 집합을 하나의 단위로 정의해놓고 사용합니다. 이 단위가 바로 태스크 디피니션입니다.

### AWS Console에서 태스크 디피니션 생성하기

- Amazon ECS -> 작업 정의 -> 새 작업 정의 생성
- 여기서도 EC2를 선택해야겠죠
- 다음과 같이 선택합니다.
  - 작업정의 이름 : ecs-web(여러분이 원하는 대로)
  - 역활 : 없음
  - 네트워크 모드 : 브리지
  - 메모리 1MIB, CPU : 512
  - 여기에 올리기 전에 메모리와 CPU는 `docker stats` 로 미리 확인해야 한다. 안그러면 오류가 나서 컨테이너가 실행되지 않게 된다.
  - 여기서 사용할 컨테이너를 선택합니다. 새로운 탭을 열고 ECR로 가서 레포지토리에 레포지토리 이름을 선택한 후 이미지 url을 복사합니다. 이러면 작업정의 생성 완료가 나옵니다.

## ELB 생성

### AWS Console에서 ELB 생성하기

- 로드 밸런서 생성

- 로드 밸런서 생성 유형은 Application Load Balancer를 선택합니다.

  > 참고사이트 2의 설명
  >
  > **ALB의 경우** HTTP, HTTPS Protocol만 지원한는 L7 LoadBalancer로 Application의 유연한 기능에 초점이 맞추어져 있습니다.
  >
  > **NLB의 경우** 초고성능, 대규모 TLS 연결 및 종료, 중앙 집중화된 인증서 배포 및 고정 IP 주소가 필요한 경우 사용합니다. NLB의 경우 Layer5 네트워크 계층을 사용하여 모든 TCP Protocol을 지원합니다.

  - 이름을 정해 줍니다.
  - 로드 밸런서의 가용영역을 정해줍니다. 위의 서브넷 설정을 그대로 적용하였습니다.
  - 보안 설정 구성이라는 부분이 있는데 이부분은 그대로 다음을 눌러서 넘어간다.
  - 보안 그룹을 설정해 줍니다. 보안그룹중 80번 포트가 모두 허용인 것을 사용하면 됩니다.
  - 라우팅 구성에서도 라우팅 이름만 설정하고 그래도 넘어갑니다.
  - 대상등록은 ECS-Instance2개를 선택한 후 추가해 주었습니다.
  - 그럼 로드 발란서가 생성된 것을 확인할 수 있습니다.

- 완료된 후 보안그룹을 다시한번 확인해보기 바란다.

## 서비스 생성

- Service는 태스크를 지속적으로 관리하는 단위입니다. 서비스는 클러스터 내에 태스크가 지정된 수 만큼, 지속적으로 실행될 수 있도록 관리합니다. 또한 AWS ELB에 자동으로 등록, 제거하는 역할을 담당합니다.

### AWS Console에서 서비스 생성하기

- 기존에 생성했던 ecs-cluster를 선택 후 서비스 생성을 클릭합니다

  - 시작 유형 : EC2
  - 서비스 이름 : ecs-web-service
  - 서비스 유형 : Replica
  - 작업 수량 : 1
  - 나머지는 기본값으로 하겠습니다.

- 네트워크 구성

  - 상태검사 유예기간이라는 표기가 0이 아닐 것이다. 하단의 로드 밸런서 유형을 Application Load Balancer로 설정하면 된다.
  - 클러스터 VPC는 하나만 있으므로 그것을 선택하면 된다.
  - 로드 밸런싱 할 컨테이너를 로드 밸런서에 추가해 줍니다.
    - **프로덕션 리스너 포트** : 80
    - 경로패턴 : /web* (현재 여기를 어떻게 처리할지 고민이다. 참고사이트에 보니 해당 부분을 호출하는데가 존재하기 때문이다. default로 갈까 생각중이다.)
    - 평가순위 : 1
    - 헬스 체크 경로 : 여러분들이 정한 경로(/checker로 함)

- 여기서 서비스가 실행되지 않아서 매우 많이 고생했다. 아직 해결중이지만 하나 알수 있던건 테스크 데피니션을 실행하기 전에 꼭 `docker stats` 를 실행해서 해당 컨데이터가 실행되는 메모리의 크기를 확인하도록 한다. 그러지 않으면 서비스가 바로 죽어버린다. 이거 해결을 하기 위해 하루 이상을 허비했다.

  ```shell
  Error response from daemon: OCI runtime create failed: container_linux.go:345: starting container process caused "process_linux.go:297: getting the final child's pid from pipe caused \"read init-p: connection reset by peer\"": unknown
  ```

  이러한 메세지 등을 보며 도커의 컨테이너가 실행되지 않음을 볼 수 있다.

### 참고사이트

- [참고사이트1](https://www.44bits.io/ko/post/container-orchestration-101-with-docker-and-aws-elastic-container-service)
- [참고사이트2](https://waspro.tistory.com/428)