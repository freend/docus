## 인스턴스에 개발환경 구성하기

### 시작하기

이것도 과거에 작성한 문서에다 도커를 도입한 개발서버를 올려서 사용하는 방법을 나눠서 설명하려 한다.

1. EC2에 docker로 컨테이너 연결하기(1 인스턴스 2 server 1db)
2. EC2에 node 개발환경 구성하기(1 인스턴스 1 server)

### EC2에 docker로 컨테이너 연결하기

사실 EC2 인터넷을 보면 EC2에 node 개발환경 구성하기 이 내용이 대부분이긴 허나 요즘은 docker를 쓰는게 대부분이고 개발서버의 운영비를 줄이기 위해서라도

무료 요금제인데요에 속지말자 **요금계산법**

| 인스턴스 대수 | 시간 =24 * 30 | 결과 | 금액 |
| ------------- | ------------- | ---- | ---- |
| 1             | 720           | 720  | 무료 |
| 2             | 720           | 1440 | 유료 |

따라서 아무리 무료 인스턴스를 돌린다 하더라도 2대만 돌리면 그 순간 1440 - 750 = 720 * 0.0116 = 8.136 USD가 소비된다.전에 어느 개발자분이 쓰지도 않는 인스턴스를 고비용으로 마구 만들어 놓고 그것을 알지 못하여 큰 돈이 깨진적이 있다.

그리고 중요한 문제는 각각의 인스턴스를 동일한 환경으로 세팅을 해야하는 문제가 있는데 (개발, 운영) 컨테이너를 사용하게 되면 환경 설정에는 크게 필요성을 갖지 않아도 된다.

#### Container만들기 

[컨테이너 만들기](ECS/CreateDocker.md)

#### Container 를 ECR에 올리기

[ECR에 Container를 올려보자](ECS/ECR.md)

#### 이렇게 만든 Container를 한대의 인스턴스에서 사용하자.

[아마존 EC2에서 docker 연결하기](ECS/EC2ConnectDocker.md)

### EC2에 node 개발환경 구성하기

#### OS

Amazon Linux 2 AMI (HVM), SSD Volume Type - ami-0a2de1c3b415889d2

전반적으로 Amazon Linux와 Ubuntu의 큰 차이는 없으나 설치 명령이 다를 수 있다.

#### Node Version Manager을 사용하게 된 계기

node는 초기 설정에서 변경이 불편한 경우가 많다. 그래서 초기 설치시 nvm을 설치하고 관리하도록 하겠습니다.
nvm은 node version manager의 약어 입니다. 이것을 사용하게 된 계기는 초기에 node version 8을 사용하다
express4가 node version 10을 사용해야 해서 고생한 적이 있어서 이것을 막고자 nvm을 사용하게 되었습니다.

[nvm 공식 사이트](https://github.com/creationix/nvm#install-script)

#### Nvm 설치하기

공식사이트를 참고해서 우리는 curl로 설치하였다.
`
curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.33.11/install.sh | bash
`
.bash_profile에 반영하자.
`source .bash_profile`

ubuntu의 경우는 다음과 같다

`source .profile`

node를 설치하자
`nvm install version`
이제 사용하면 된다.

#### Git 설치하기

다음의 명령으로 git을 설치한다.
`sudo yum install git-core`

#### Node Clone 하기

`git clone [리모트 주소][로컬 폴더]`
으로 폴더를 생성하며 그곳으로 clone을 해 준다. 해당 폴더로 이동을 하저
만약 master브런치가 아닌 경우는 다음의 명령으로 checkout해준다.
`git checkout -b [로컬 브런치 이름] [리모트 브런치 이름]`

> 현재 ico-client가 사용하고 있는 branch는 ieo이다.

#### SSL 업로드

현재 ico-client는 https 통신을 위해 ssl을 사용하고 있다. 하지만 이것 또한 리모트 브런치에 없다.

이 부분은 amazon web service에서 elb와 elb내에 ssl설정이 되어 있지 않은 경우 사용한다.
또한 Node의 세팅도 자체 ssl을 쓰는 상황에 맞게 되어 있어야 한다.

> 현재 elb를 사용하지 않기 때문에 node의 자체 ssl설정이 되어 있는 걸 사용한다.

https를 사용하게 하기 위해서 인증서를 업로드 해야 한다.

> 이 부분은 현재 ico-client만 적용되는 부분이다.

#### Node Module 설치하기

git에 node module는 현재 gitignore에 의해 저장소에 위치하지 않으므로 다시 내려받아야 한다.

```
npm i
Aws sdk : 별도로 설치해야 한다.
```

이제 Node를 실행하면 다음과 같은 메세지가 나온다.
`Port 443 requires elevated privileges(443 port는 상위 권한이 필요합니다.)`
즉 루트 권한이 필요하다는 이야기임. 따라서 sudo를 넣어서 실행하자.
`sudo node command not found`
노드를 찾지 못한다고 한다. 
이것을 해결하기 위해서 다음의 작업과정이 필요하다. 

> ubuntu는 skip

[참조사이트](https://www.digitalocean.com/community/tutorials/how-to-install-node-js-with-nvm-node-version-manager-on-a-vps#-installing-nodejs-on-a-vps)

다음 명령을 터미널에서 실행한다

```shell
n=$(which node);
n=${n%/bin/node};
chmod -R 755 $n/bin/*;
sudo cp -r $n/{bin,lib,share} /usr/local
```

그후 보면 경로가 나눠져 있음을 볼 수 있다

```
[ec2-user@ip-xxx-xxx-xxx-xxx node]$ which node
~/.nvm/versions/node/v10.8.0/bin/node
[ec2-user@ip-xxx-xxx-xxx-xxx node]$ sudo -s
[root@ip-xxx-xxx-xxx-xxx node]# which node
/bin/node
```

이제 sudo node bin/www로 실행시키면 정상적으로 진행됨을 알 수 있다.

#### PM2 설치

Pm2는 Node server의 실행, 모니터링, 제어등을 담당하는 npm의 미들웨어 이다.
이것에 대한 설명은 pm2.md를 참조하기 바란다. 이것을 가지고 실행하면 된다.

명령은 다음과 같다. `pm2 start bin/www` 이게 pm2의 가장 기초적인 명령이다.
하지만 이렇게 하면 `Port 443 requires elevated privileges(443 port는 상위 권한이 필요합니다.)` 이 메세지를 볼 수 있다.

pm2 또한 sudo명령이 필요하다. 그래서 sudo로 실행을 하면 `sudo pm2 command not found` 를 볼 수 있다.

그래서 `pm2 start` 명령을 바꿔야 한다.

package.json을 열어보면 다음과 같은 부분을 볼 수 있다.

```json
"scripts": {
    "start": "node ./bin/www",
    "build": "babel"
  }
```

저기서 start 부분은 npm을 이용해서 

```shell
pm2 start --name client npm -- start
```

과 같이 해주게 되면 pm2를 sudo 명령없이 사용할 수 있다.

[Ubuntu 18.04 install java](https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-on-ubuntu-18-04)

