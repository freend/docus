## ALB를 이용해서 연결하자

ECS를 이용하는 이유중에 하나가 오케스트레이션으로 그것은 오토스케일링을 통해서 컨테이너의 증감을 유연하게 해주는 것이다. 그런데 이런경우 ip주소를 가지고 연결할 수는 없다. 왜? 앞에서 아이피 주소를 자동 할당 하기 때문이다. 그래서 ECS에서는 서비스와 도메인에 연결된 alb를 통해서 증감된 컨테이너에 호출을 할 수 있게 만들어 줘야 합니다.

### 로드 밸런서 만들기

- EC2의 로드밸런서를 선택합니다.
- 로드 밸런서 생성을 클릭합니다.
- 로드 밸런서 유형에서 Application Load Balancer를 선택하여 생성합니다.
- 이름을 정한 후 체계는 인터넷 연결을 선택합니다.
- 로드 밸런서의 프로토콜은 HTTP 포트는 80을 선택하고 가용영역엔 우리가 만든 클러스터의 VPC를 선택하고 두개의 가용영역을 다 선택해 줍니다.
- 보안 그룹을 설정해 줍니다.
- 라우팅 그룹으로 가서 새 대상 그룹에 대상그룹의 이름을 입력합니다.
- 대상유형은 IP를 선택하고 대상 등록을 클릭합니다.
- Fargate를 사용할 경우 별도의 대상등록은 하지 않아도 되므로 검토 후 생성합니다.

### 서비스 생성

이제 로드 밸런서와 작업을 연결하기 위한 서비스를 생성하자

- 생성된 클러스터의 서비스 탭에서 생성을 클릭합니다.
- 서비스 유형에 Fargate를 선택하고 작업정의 및 버전을 선택합니다.
- 서비스 이름을 정해주고 작업개수는 1로 선택합니다.
- Deployments는 컨테이너를 한번에 바꿀지 블루 그린 배포를 해서 변경할지를 나타내는데 여긴 기본을 합니다.
- vpc, 서브넷, 보안그룹, 자동할당 퍼블릭 ip를 ENABLED로 선택한 후 로드 밸런서 유형은 Application Load Balancer를 선택합니다.
- 로드 밸런서 이름, 컨테이너를 선택해 줍니다. 그후 리스너 포트를 선택하고 평가순서를 정해줍니다. 여기는 1이상이므로 1을 선택해 줍니다.
- Auto Scaling을 선택할 수 있습니다. 처음엔 조정하지 않아도 됩니다.

### 접속 확인

- EC2의 로드 밸런서를 클릭하면 DNS이름의 url이 나오는데 이것을 주소창에 붙어넣기 해서 접속이 되는지 확인해 봅니다.