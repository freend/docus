## Docker File 만들기

- 폴더를 만든다.

```shell
mkdir nginx
cd nginx
```

- create Dockerfile

```dockerfile
FROM nginx:latest
COPY index.html /usr/share/nginx/html/index.html
EXPOSE 80
CMD["nginx", "-g", "daemon off;"]    
```

- create index.html

```html
<h1>Hello, Docker!</h1>
```

- docker build

```shell
docker build -t my-nginx-image:latest . #한칸띄고 .을 꼭 찍어줘야 한다. 안그러면 오류발생
docker image
my-nginx-image            latest              15e4f9f7b7e9        9 seconds ago       127MB
```

- docker tag
  - 도커 파일을 도커 레포지토리에 그냥 푸시하려고 하면 안된다. 다음과 같이 tag로 복제 이미지를 만들어서 해야 한다.

```shell
# docker image tag [docker-image-name] [docker-id]/[tag-name]
docker image tag my-nginx-image freend/my-nginx-image
freend/my-nginx-image     latest              15e4f9f7b7e9        3 hours ago         127MB
```

- docker push to docker repository

```shell
docker login
docker push freend/my-nginx-image
The push refers to repository [docker.io/freend/my-nginx-image]
0f309d55c826: Pushed
22439467ad99: Pushed
b4a29beac87c: Pushed
488dfecc21b1: Mounted from library/nginx
latest: digest: sha256:65d73f7ac587343306fbc490e25d50442cb419d76b503a36178751a2a140af1f size: 1155
```

- docker commit

```shell
# 우분투를 실행해서 그곳에 apache2 설치 후 실행
docker run -it --name mywebserver -p 80:80 ubuntu:14.04 bash #ubuntu 14.04 실행
apt-get update
apt-get install apach2 -y
service apache2 satart
```

```shell
# commit을 하면 현재 컨테이너가 이미지로 된다.
docker container commit mywebserver ubuntu14-apache2
docker images
ubuntu14-apache2          1.0                 d4349e7851b8        7 hours ago         221MB
```