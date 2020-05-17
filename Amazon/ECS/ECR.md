## ECR 사용하기

우선 ECS를 사용하기 위해서 ECR을 먼저 사용하기로 함.

> ECR이란 개발자가 Docker 컨테이너 이미지를 손쉽게 저장, 관리 및 배포할 수 있게 해주는 완전관리형 Docker 컨테이너 레지스트리입니다. (공식 홈페이지에서 발췌)

우선 aws cli에 접속할 수 있는 계정과 aws cli는 설치했다는 가정하에 시작하겠습니다.

#### ECR 요금제

- 스토리지 사용요금과 데이터 송신 요금이 부과됨
  - 스토리지 요금은 월별 GB당 0.10 USD입니다.
  - 데이터 송신은 아무리 봐도 9.999TB아래일 듯 싶어서 보니 월 GB당 0.12 USD임.
- 우선 내가 사용할 시스템의 ECR요금은 예상잡아 3GB정도로 예상함.
  - Node : 1GB, Java : 1GB
  - Storage : 2 * 0.1 = 0.2USD
  - 우선은 월간 ECR사용 요금은 0.2USD로 예상
  - 같은 리전에 있는 Amazon Elastic Container Registry와 Amazon EC2 간의 데이터 전송 요금은 무료(즉, GB당 0.00 USD)입니다
  - 여기서 사용하는 데이터 전송량은 60(2 * 30)GB * 0.12 = 7.2USD
  - ECR의 월 예상액은 7.5USD로 책정함.

### ECR 접속하기

- Amazon Web Service에 접속한 후 ECR을 선택합니다.
- 원하는 리전에 Create Repository로 저장소를 생성합니다.
- 그러면 내가 생성한 이름으로 저장소가 추가됨을 확인할 수 있습니다.
- 내가 생성한 저장소로 들어가면 푸시 명령 보기라고 있습니다.
- 누르면 다음과 같은 4가지 명령이 나오게 됩니다.
- 저장소 이름은 sample-repo로 정의하겠습니다.

```shell
# 레지스트리에 도커 컨테이너 인증하기
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 000000000000.dkr.ecr.ap-northeast-2.amazonaws.com/sample-repo
# 도커 빌드하기 이건 위의 web, api 서버를 도커로 만들자 로 대체해도 됩니다.
docker build -t sample-repo .
# 태그 하기
docker tag sample-repo:latest 000000000000.dkr.ecr.ap-northeast-2.amazonaws.com/sample-repo:latest
# 푸시하기
docker push 000000000000.dkr.ecr.ap-northeast-2.amazonaws.com/sample-repo:latest
```

- 이후 레포지토리에 보면 컨테이너가 있는 것을 확인 할 수 있습니다.

- repository 이름은 서비스에 연관있게 만들면 될 거 같습니다.

- 즉 나의 서비스를 기준으로 repository는 2개가 현재 사용됩니다

  - 000000000000.dkr.ecr.ap-northeast-2.amazonaws.com/web

  - 000000000000.dkr.ecr.ap-northeast-2.amazonaws.com/api


### 데이터 전송량 이란?

첨에 데이터 전송량별 요금이라는 부분이 나와서 열심히 살펴보고 알게 된 내용을 여기에 적어놨다.

- 데이터 전송량은 Amazon EC2, Amazon Elastic Container Registry, Amazon EBS, Amazon S3, Amazon Glacier, Amazon RDS, Amazon SimpleDB, Amazon SQS, Amazon SNS, Amazon DynamoDB, AWS Storage Gateway 및 Amazon VPC 전체에 걸친 아웃바운드 데이터 전송량을 합하여 계산합니다.
- 즉 ECR에서 EC2로 내려받는건 무료 Storage, DB등 서비스간의 데이터는 전송료가 있다는 의미인 듯.
- AWS의 프리 티어를 사용하는 신규 AWS 고객은 모든 AWS 서비스를 합산해 1년 동안 매달 15GB의 데이터 전송을 무료로 제공받게 됩니다.
- 데이터 전송요금은 별도로 예상해야 함.(즉 조회결과 등도 최적화 해서 보내는게 좋다는)

### EC2에서 ECR의 Container를 Pull하자

- 이제 아마존의 ECR에 container를 올리는 거 까지는 성공했으니 이제 내려받기를 시도해보자.
- 모든 내용은 ssh를 사용하여 EC2 인스턴스에 접속했음을 가정한다.
- EC2에 아마존 클라이언트가 설치되어 있어야 한다.

```shell
#AWS ECR에 접속하자
aws ecr get-login --region ap-northeast-2 --no-include-email
# 다음과 같은 메세지가 나온다. 이것을 전체 복사해서 붙어넣기 후 실행하자.
docker login -u AWS -p ~~~~~~~~~~~ https://000000000000.dkr.ecr.ap-northeast-2.amazonaws.com
# 다음과 같은 메세지가 나온다
WARNING! Using --password via the CLI is insecure. Use --password-stdin.
WARNING! Your password will be stored unencrypted in /home/ubuntu/.docker/config.json.
Configure a credential helper to remove this warning. See
https://docs.docker.com/engine/reference/commandline/login/#credentials-store

Login Succeeded
# 이제 이러면 ECR에서 해당 컨테이너를 다운받을 수 있다.
docker pull 000000000000.dkr.ecr.ap-northeast-2.amazonaws.com/sample-repo:latest
```

### 참고사이트

- [참고사이트1](https://sarc.io/index.php/aws/1326-aws-ecr-docker-push)
- [참고사이트2](https://bluese05.tistory.com/51)