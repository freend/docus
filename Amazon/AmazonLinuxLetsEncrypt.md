# 인스턴스에 SSL 적용하기

### 시작하기

원래 해당 문서는 EC2에 ssl 적용하기 였는데 alb와 ECS를 쓰게 되면서 그에 맞춰서 내용을 변경하였다. 그래서 2개의 내용으로 나뉜다.

1. [alb에 ssl적용하기](ECS/ECS-SSL.md)
2. EC2에 ssl적용하기

### EC2에 ssl적용하기

회사에서 운영하는 내부 사이트에 ssl을 적용하게 되었다. ssl을 사용할 때 일반적으로 유료로 사용을 하였는데 이번엔 금액을 아끼기 위해 무료 ssl인 let's encryption을 사용하게 되었다.

ubuntu에서 적용하는 방법은 인터넷에서 쉽게 찾을 수 있지만 
Amazon Linux에서 사용하는 방법은 많지 않았다. 이에 인터넷에서 다음의 방법을 적용할 수 있음을 알게 되었다.

바로 아래에는 서론이 왜 복잡한지에 대한 설명인 Amazon Linux와 Ubuntu의 차이를 간단히 정리했다.

**주의사항 : AWS에 Amazon Linux가 Amazon Linux와 Amazon Linux 2의 두가지가 있는데 Amazon Linux 2에서는 let's Encrypt의 의존성 설치에서 에러가 난다(1월 31, 2019 현재) 따라서 Amazon Linux 로 작업하길 권한다.**

## Amazon Linux와 Ubuntu를 구분하는 방법

- Amazon Linux

  ```shell
         __|  __|_  )
         _|  (     /   Amazon Linux AMI
        ___|\___|___|
  
  https://aws.amazon.com/amazon-linux-ami/2017.09-release-notes/
  33 package(s) needed for security, out of 56 available
  Run "sudo yum update" to apply all updates.
  Amazon Linux version 2018.03 is available.
  [ec2-user@ip-XXX-XX-XXX-XX ~]$
  ```

  - Amazon Linux AMI라는 표기가 나온다.
  - 기본적인 사용자가 ec2-user로 되어있다.
  - 인터넷에서 Amazon Linux보단 AMI(Amazon Machine Image)로 검색하면 더 많은 결과를 얻을 수 있다.

- Ubuntu

  ```shell
  Welcome to Ubuntu 16.04.5 LTS (GNU/Linux 4.4.0-1074-aws x86_64)
  
   * Documentation:  https://help.ubuntu.com
   * Management:     https://landscape.canonical.com
   * Support:        https://ubuntu.com/advantage
  
    Get cloud support with Ubuntu Advantage Cloud Guest:
      http://www.ubuntu.com/business/services/cloud
  
  10 packages can be updated.
  0 updates are security updates.
  
  
  Last login: Fri Jan 25 18:56:54 2019 from 39.7.59.70
  ubuntu@ip-172-31-29-109:~$
  ```

  - Welcome to Ubuntu 라는 메세지가 나온다.
  - 기본적인 사용자가 ubuntu로 되어있다.

같은 linux이지만 ubuntu는 Debian계열 Amazon Linux는 Red Hat계열이다. 그 이야기는 시조만 동일할 뿐 바로 분리된 2개의 다른 가문의 자식이라고 보면된다. 

## Let's Encrypt 적용하기

공통된 부분을 기준으로 진행하나 나눠지는게 필요한 부분은 ami, ubuntu로 분기 표기한다.
화면 표기는 ami 기준으로 한다.

- git과 python의 설치를 확인한다.

  ```shell
  [ec2-user@ip-XXX-XXX-XXX-XXX ~]$ python --version
  Python 2.7.13
  [ec2-user@ip-XXX-XXX-XXX-XXX ~]$ git --version
  git version 2.14.4
  ```

####1. Let's Encrypt를 설치한다.

/home/ec2-user 폴더에 git으로 letsencrypt를 clone 한다.

```shell
[ec2-user@ip-XXX-XXX-XXX-XXX ~]$ git clone https://github.com/letsencrypt/letsencrypt
```

letsencrypt폴더로 이동한다.

```shell
[ec2-user@ip-XXX-XXX-XXX-XXX ~]$ cd letsencrypt
```

letsencrypt의 dependency를 설치하기 위해 다음의 명령을 실행한다.

```shell
[ec2-user@ip-XXX-XXX-XXX-XXX letsencrypt]$ ./letsencrypt-auto --debug
```

설치를 하면 꽤 많은 부분의 업데이트를 볼 수 있다.
설치 완료 후 --help 옵션을 보면 lets encrypt 설치에 대한 여러가지 설정을 알 수 있다.

#### 2. letsencrypt 인증서 발급

여기서는 certbot을 사용하지 않고 수동으로 인증서만 받을 것이다.

```shell
[ec2-user@ip-XXX-XXX-XXX-XXX letsencrypt]$ ./letsencrypt-auto certonly --standalone -d admin.remiit.io #certification domain name
```

1. 이메일을 등록하라는 화면이 나온다.

   ```shell
   Enter email address (used for urgent renewal and security notices) (Enter 'c' to
   cancel): email@remiit.io #정상적인 메일주소로 인증이 되야 한다.
   ```

2. 약관 동의 화면이 나온다.

   ```shell
   - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
   Please read the Terms of Service at
   https://letsencrypt.org/documents/LE-SA-v1.2-November-15-2017.pdf. You must
   agree in order to register with the ACME server at
   https://acme-v02.api.letsencrypt.org/directory
   - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
   (A)gree/(C)ancel: A
   ```

3. 이메일 주소로 뉴스레터를 보내는데 동의하냐는 항목이 나온다.

   ```shell
   - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
   Would you be willing to share your email address with the Electronic Frontier
   Foundation, a founding partner of the Let's Encrypt project and the non-profit
   organization that develops Certbot? We'd like to send you email about our work
   encrypting the web, EFF news, campaigns, and ways to support digital freedom.
   - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
   (Y)es/(N)o: Y
   ```

4. 최종결과가 나온다

   ```
   Obtaining a new certificate
   Performing the following challenges:
   http-01 challenge for admin.remiit.io
   Waiting for verification...
   Cleaning up challenges
   
   IMPORTANT NOTES:
    - Congratulations! Your certificate and chain have been saved at:
      /etc/letsencrypt/live/admin.remiit.io/fullchain.pem
      Your key file has been saved at:
      /etc/letsencrypt/live/admin.remiit.io/privkey.pem
      Your cert will expire on 2019-04-28. To obtain a new or tweaked
      version of this certificate in the future, simply run
      letsencrypt-auto again. To non-interactively renew *all* of your
      certificates, run "letsencrypt-auto renew"
    - Your account credentials have been saved in your Certbot
      configuration directory at /etc/letsencrypt. You should make a
      secure backup of this folder now. This configuration directory will
      also contain certificates and private keys obtained by Certbot so
      making regular backups of this folder is ideal.
    - If you like Certbot, please consider supporting our work by:
   
      Donating to ISRG / Let's Encrypt:   https://letsencrypt.org/donate
      Donating to EFF:                    https://eff.org/donate-le
   ```

#### 3. Greenlock을 이용한 인증서 작업

lets encrypt의 인증서를 node에서 사용하기 위해서 middle ware를 설치해주는데 이때 사용되는 것이 green lock이다.

다음 3개의 미들웨어를 설치해준다.

```shell
[ec2-user@ip-XXX-XXX-XXX-XXX letsencrypt]$ npm install greenlock-express --save
```

#### 4. node app.js 구성 변경

```javascript
// greenlock-express의 설정하는 부분입니다.
const lex = require('greenlock-express').create({
	version: 'v02',
	configDir: '/etc/letsencrypt',
	server: 'production',
	approveDomains: (opts, certs, cb) => {
		if (certs) {
			opts.domains = ['admin.remiit.io', 'www.admin.remiit.io'];
		} else {
			opts.email = 'email@remiit.io';
			opts.agreeTos = true;
		}
		cb(null, {options: opts, certs});
	},
	renewWithin: 81 * 24 * 60 * 60 * 1000,
	renewBy: 80* 24 * 60 * 60 * 1000
});
//https middle ware를 사용합니다.
const https = require('https');
```

이와 같이 설정을 한 후 서버를 다시 실행하면 ssl이 적용된다.

### 참고 사이트

[한준케이 블로그](http://hanjoonkblog.blogspot.com/2016/11/nodejs-lets-encrypt-https.html)

https://kr.minibrary.com/353/