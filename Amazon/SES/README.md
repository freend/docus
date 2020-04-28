## Amazon SES(Simple Email Service) 사용하기

### 사용계기

- 전에는 java mail sender를 통해서 메일 발송이 쉽게 되었는데 이번에 프로젝트를 진행하면서 메일 발송이 안되는 상황을 보게 되었다.
- 그래서 확인해 보니 Amazon Web Service에서 스팸메일 등의 대량 발송을 막기 위해 일반 발송 기능을 막은 것으로 추정하게 되었다.
  - 이렇게 생각한 이유는 로컬에서는 메일이 정상적으로 발송되나 ec2에서는 막았고 전에 안되는 이유를 찾다가 추정하게 된 내용이다.
- 그리하여 AWS에서 제공하는 메일 서비스를 이용하게 되었다.
- 이건 리전이 많지 않은 관계로 적당한 리전을 선택해서 사용하면 된다.

### 좋은점

- 발송 메일 주소를 여러개 등록할 수 있다.
- smtp 방식이 아니고 aws에서 발송을 해주고 보내는 사람만 등록된 이메일을 쓸수 있게 해주므로 비밀번호를 기입할 필요가 없다.
- 등록만 되어 있으면 쉽게 이메일 주소를 변경할 수 있다.

### 사용방법

- 이건 솔직히 인터넷에 많기 때문에 [jojoldu님의 ses사용하기](https://jojoldu.tistory.com/246)를 링크 걸테니 이것을 참조하라.
- 위의 링크를 이용해서 로컬에서 메일 보내는 것은 성공했다.
- 여기서는 위의 링크 외에 사용하면서 생긴 문제에 대해 적어보겠다.

### 그외의 문제점

- 저 방법을 사용하면 local이나 ec2에서 jar형식으로 사용하는 서버에는 문제가 없다. 하지만 나는 docker를 이용(개발, 운영)하고 운영서버는 ECS를 사용한다.

- 특히나 ECS에서는 EC2를 사용하는게 아니라 Fargate를 사용한다. 그럼 문제가 무엇이냐. 바로 AWS Credentials 을 적용하기가 힘들어 진다는 문제가 생긴다.

- docker는 별도의 가상화 시스템이기 때문에 서버의 ~/.aws/credentials 이 경로를 알지 못한다. 즉 해놔 봤자 credentials를 찾지 못해서 발송을 못한다는 에러문구만 열심히 보게 될 것이다.(개발서버에서 이랬다)

- 해결을 위해 인터넷을 뒤지던중 다음의 방법을 알게되었다.

  > docker 와 server의 volume을 공유하세요

- 위의 방법 좋다. 하지만 저 방법은 바로 쓸수 없다는걸 알게 되었다. 운영서버는 Fargate를 사용한다니까

- 그럼 다른 방법을 찾아보자.. 어차피 한글로는 못찾는다 영어로 찾자. 다시 찾아서 링크를 걸려니 못찾겠다.

- 그 당시 찾은 방법은 이것이었다.

  ```shell
  -e "AWS_ACCESS_KEY_ID= XXXXXXXXXXXXXXXXXXXX" -e "AWS_SECRET_ACCESS_KEY= XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
  ```

- environment를 사용하면 된다고 한다. 오케이 저 방법으로 가보자 하고 개발에서 도커 실행시 위와 같이 실행했다.

- 자 메일 발송~~~ 하니 바로 Credentials을 못찾는다고 에러가 발생했다.

- 뭐지 된다며? 하고 또 인터넷 서치를 시작했다. 그런데 쉽게 답이 나오지 않는다. 이제 개발자 문서를 통해 문제를 해결할 때였다. 소스를 보던 중 다음의 클래스를 주시했다.

  ```java
  ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
  ```

  - 그렇다 provider가 profile credentials를 사용한다는 의미였다.
  - 그래서 바로 'ProfileCredentialsProvider'로 검색해보니 개발자 문서에 다음과 같이 나온다.

  ```
  All Implemented Interfaces:
  AWSCredentialsProvider
  ```

  - 즉 모든 자격공급자는 'AWSCredentialsProvider'를 상속받는다는 이야기. 바로 'AWSCredentialsProvider'를 선택했다.
  - 거기에서 내 눈에 띈 클래스가 있다. 바로 'EnvironmentVariableCredentialsProvider'
  - 공급자를 이것으로 변경한후 도커에서 실행하니 정상적으로 작동한다.

- 이제 마지막 문제다. 이것을 ECS에 적용하는 것. 그것은 [Environment설정](../ECS/ECS-Fargate.md) 을 참조하자.