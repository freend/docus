## 하나의 EC2에서 여러개의 컨테이너 연결하기

[EKS, ECS 무엇을 사용할까?](Choice.md) 에서 저는 여러대의 컨테이너를 연결한 서비스를 운영할 거라고 했습니다. (Web, Api) 그런데 문제는 돈을 아끼기 위해 개발 서버는 한대의 EC2만을 이용한다는 문제가 있습니다. 이에 하나의 인스턴스에서 여러개의 도커를 연결하는 것을 알려드리겠습니다.

참고로 localhost:포트번호 로는 절대로 연결되지 않습니다. 간단히 말해서 각각의 컨테이너는 별개의 인스턴스라고 보시면 됩니다. 즉 localhost로 연결하면 컨테이너 안에서 찾기 때문에 연결이 되지 않는다는 거죠. 이것을 위해서 

1. Docker Compose 사용하기
2. 그냥 연결하기

1을 사용하면 편합니다. 컴포넌트를 다 pull 한 후 실행순서를 정하고 compose파일을 실행하면 됩니다.(sql, api, web) 그런데 이렇게 하면 새로 받을 때 마다 data base를 commit해서 업데이트하고 해야하는 등의 문제와 하나만 바꾸는 경우는 적용할 수 없다는 문제가 생깁니다.

그래서 저는 그냥 연결하기로 해결했습니다. 그냥 연결하기란 docker는 자체의 별도 네트워크를 가지고 있습니다. 다만 문제는 한번은 각 컨테이너의 아이피를 확인해야 한다는 것입니다(인스턴스 재부팅 등에도 네트워크 아이피는 유지됩니다). 확인하는 방법은 다음과 같습니다.

```shell
docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
7a88e619b74e        bridge              bridge              local
cb3164eb4e9f        docker-net          bridge              local
a66e1d2c59ca        host                host                local
6fb05937ba75        none                null                local
```

저기서 docker를 실행할 때 네트워크 옵션을 주지 않으면 일반적으로 bridge에 연결됩니다. 아래 docker-net은 제가 만든 브릿지 입니다.

```shell
#컨테이너의 아이피 확인하기
docker inspect [container-name] -f "{{json .NetworkSettings.Networks }}"
{"docker-net":{"IPAMConfig":null,"Links":null,"Aliases":["2a174775502c"],"NetworkID":"cb3164eb4e9f4688267dcce3f640aa53532bc10a479915eaa7e8210517946473","EndpointID":"e783a650da8744712d40cd11aa79c72787c498eabf033bb2c7919609e3e9aa0c","Gateway":"172.18.0.1","IPAddress":"172.18.0.2","IPPrefixLen":16,"IPv6Gateway":"","GlobalIPv6Address":"","GlobalIPv6PrefixLen":0,"MacAddress":"02:42:ac:12:00:02","DriverOpts":null}}
```

"IPAddress":"172.18.0.2" 이 부분이 컨테이너에 할당된 도커 네트워크의 아이피 입니다. 저는 db <-> api <-> web이렇게 연결을 합니다. 즉 spring boot로 만든 api는 db의 아이피 주소를 보면 되고 node로 만든 web은 api의 아이피 주소를 보면 됩니다.

```shell
# api server의 도커 실행문 입니다
docker run -d -p 8080:8080 --network docker-net -e "SPRING_PROFILES_ACTIVE=dev" -e --name api-server 000000000000.dkr.ecr.ap-northeast-2.amazonaws.com/api
# web server의 도커 실행문 입니다
docker run -d -p 80:80 --network docker-net -e NODE_ENV=dev --name web-server 000000000000.dkr.ecr.ap-northeast-2.amazonaws.com/web
```

이렇게 하면 개발 서버에서 여러대의 컨테이너를 실행시키며 동시에 각각의 네트워크 통신도 가능하게 됩니다.