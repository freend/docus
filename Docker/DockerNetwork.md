## Docker Network 만들기

### 기본 브리지에서 연결하기

- 스프링 부트 jpa는 서버 실행시 mysql에 연결되지 않으면 종료가 된다. 네트워크 테스트를 위해서는 아주 쉬운 방법이라 생각하고 또한 컨테이너의 오류 여부를 확인하기 위해서도 꼭 필요한 방법이다.

- 로컬에서 도커의 포트 하나를 오픈한 후 다른 도커에서 동일한 포트를 열면 유일성 위배로 인해 도커가 실행되지 않는다. 이에 도커를 네트워크로 연결하는걸 해 보겠다.
- 물론 docker compose등을 통하면 네트워크 이름을 임의로 만든수 있지만 여기서는 그러지 않겠다.
- 지금 여기의 순서는 DataBase(port:3306) - api(port:8080) - web(port:3000)으로 연결한다.
- 우선 mysql docker로 실행한다.

```shell
docker run --name mysql-sample -p 3306:3306 -e MYSQL_ROOT_PASSWORD=“toor” -d mysql:5.7
```

- 그 후 mysql-sample이 어떤 네트워크에서 실행되었는지 확인하자

```shell
docker inspect mysql-sample -f "{{json .NetworkSettings.Networks }}"
```

```json
{"bridge":{"IPAMConfig":null,"Links":null,"Aliases":null,"NetworkID":"4e28ef8e0cf96ae6b065a620024804a5b24638ddf122e99e754162d4a203dee3","EndpointID":"d0a1a7a91a5d6a2ff779c6a69fe0ea306d4d6d20ec7fdd405a62073caebb3223","Gateway":"172.17.0.1","IPAddress":"172.17.0.2","IPPrefixLen":16,"IPv6Gateway":"","GlobalIPv6Address":"","GlobalIPv6PrefixLen":0,"MacAddress":"02:42:ac:11:00:02","DriverOpts":null}}
```

- NetworkID 앞의 글자를 Network List에서 찾아보자

```shell
docker network ls
NETWORK ID          NAME                   DRIVER              SCOPE
4e28ef8e0cf9        bridge                 bridge              local
3edea438fbec        host                   host                local
4b1a5bab6c74        none                   null                local
```

- 브리지에 연결되 있으며 해당 내부아이피는 172.17.0.2라는 걸 알 수 있다.
- 이제 api서버를 저 네트워크로 연결해 보자

```shell
docker run -p 8080:8080 --rm --network bridge -e -Dspring.profiles.active=local --name api-server api:0.0.1-SNAPSHOT
```

- 이러면 될 줄 알았는데 안되었다. 도커 문서에 보니 다음과 같은 설명이 있다.

  >  레거시 --link플래그를 사용하여 연결되어 있지 않으면 IP 주소로만 통신 할 수 있습니다 .
  >
  > **경고** : `--link`플래그는 Docker의 레거시 기능입니다. 결국 제거 될 수 있습니다. 

  이 이야기는 사용하지 말란 이야기다.

- 그래서 mysql의 접속주소를 172.17.0.2로 바꾼후 진행하니 정상적으로 작동됨을 확인했다.

### 그럼 내가 별도로 만든 network에서는 어떻게 될까?

도커 공식문서를 보면 가능하면 별도의 network를 사용하는 것을 권장하는 듯 하다. 공식문서에는 다음과 같이 적혀있다.

- **사용자 정의 브리지는 컨테이너간에 자동 DNS 확인을 제공합니다 .**
  레거시로 간주되는 --link옵션 을 사용하지 않으면 기본 브리지 네트워크의 컨테이너는 IP 주소를 통해서만 서로 액세스 할 수 있습니다 . 사용자 정의 브리지 네트워크에서 컨테이너는 이름 또는 별칭으로 서로를 확인할 수 있습니다.
- **사용자 정의 브리지는 더 나은 분리를 제공합니다** 
- **컨테이너는 사용자 정의 네트워크에서 즉시 연결 및 분리 할 수 있습니다** .
- **각 사용자 정의 네트워크는 구성 가능한 브리지를 만듭니다** .
- **기본 브리지 네트워크의 연결된 컨테이너는 환경 변수를 공유합니다.**

위의 내용으로 봐선 별도의 네트워크를 구성하면 localhost로 그냥 연결이 되는 듯 하다.

#### 그럼 확인을 해보자

- 네트워크를 생성하자

```shell
docker network create my-net
```

- 네트워크가 정상적인지 확인해보자(옵션)

```shell
docker network ls
NETWORK ID          NAME                   DRIVER              SCOPE
4e28ef8e0cf9        bridge                 bridge              local
3edea438fbec        host                   host                local
c47a0823d82c        my-net                 bridge              local
4b1a5bab6c74        none                   null                local
```

- 정상적으로 생성되었다. 이제 mysql을 실행해서 연결하도록 하겠다.

```shell
docker run -p 3306:3306 --network my-net -d --name sample -e MYSQL_ROOT_PASSWORD=“toor” mysql:5.7
```

- 이제 mysql이 재대로 실행되었는지 확인해보자.

```shell
docker inspect sample -f "{{json .NetworkSettings.Networks }}"
{"my-net":{"IPAMConfig":null,"Links":null,"Aliases":["687d194b4803"],"NetworkID":"c47a0823d82caba0292da0384e483934e7ca437b8537e52b7e3dbcb4dedc9634","EndpointID":"94bd4d5410cc5c124e9e6d6ca8cf76272c217080e918b498f98cbdc1d9974a19","Gateway":"172.19.0.1","IPAddress":"172.19.0.2","IPPrefixLen":16,"IPv6Gateway":"","GlobalIPv6Address":"","GlobalIPv6PrefixLen":0,"MacAddress":"02:42:ac:13:00:02","DriverOpts":null}}
```

- my-net network에 연결되어 있음을 확인하였다.
- 이제 해당 네트워크에 spring boot프로젝트를 붙여보겠다

```shell
docker run -p 3306:3306 --network my-net -d --name sample -e MYSQL_ROOT_PASSWORD=“toor” mysql:5.7
docker inspect sample -f "{{json .NetworkSettings.Networks }}"
docker run --rm -d -p 8080:8080 --network my-net -e -Dspring.profiles.active=local --name api-server api:0.0.1-SNAPSHOT
docker run --rm -d -p 3000:3000 --network my-net --name web-server web-server:1.0
```

- 위와같이 진행하려 했는데 api-server가 죽었다. 이것은 결국 localhost를 사용하지 못했다는 이야기이다.
- 그래서 하나씩 실행할 때 마다 아이피를 확인하고 거기에 더하는 과정을 추가하였다.

```shell
docker run -p 3306:3306 --network --restart always my-net -d --name sample -e MYSQL_ROOT_PASSWORD=“toor” mysql:5.7
# 아이피 주소의 변경이 있는지 확인하자
docker inspect sample -f "{{json .NetworkSettings.Networks }}"
# 변경이 없는 것을 확인하였다.
# 이제 ECR에 접속하자
aws ecr get-login --region ap-northeast-2 --no-include-email
# db 접속주소를 localhost에서 172.19.0.2로 변경하였다.
# 이러면 ECR접속 주소가 나온다 그걸 복사해서 붙어넣기 후 실행하면 된다.
# api server를 pull받자
docker pull 000000000000.dkr.ecr.ap-northeast-2.amazonaws.com/api-server
docker run --network my_net --restart always -d -p 8080:8080 -e "SPRING_PROFILES_ACTIVE=dev" --name api-server 000000000000.dkr.ecr.ap-northeast-2.amazonaws.com/api-server
docker inspect api-server -f "{{json .NetworkSettings.Networks }}"
# api 접속주소를 localhost에서 172.19.0.3으로 변경하였다.
docker run --network my_net --restart always -d -p 80:80 -e NODE_ENV=DEVELOP --name web-server 000000000000.dkr.ecr.ap-northeast-2.amazonaws.com/web-server
```

- 우선 이렇게 각각 실행하면 정상적으로 작동은 한다. 그런데 다음과 같은 문제가 생긴다
  - 재부팅시 다 자동 재시작이므로 정상적으로 시작은한다. 그런데 우선순위가 없어서 아이피가 꼬인다. 그래서 순서대로 다시 실행해줘야 정상적으로 작동한다.
  - docker compose에는 우선순위가 있는데 ㅋㅋ 
