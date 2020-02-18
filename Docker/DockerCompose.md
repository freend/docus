### Docker Compose

- 확장자 : *.yml, *.yaml
- 꼭 폴더를 만드록 거기서 yaml을 만들자

```shell
mkdir test_dir
cd test_dir
vi docker-compose.yml
```

### Docker Compose Command

```shell
mkdir guestbook # 폴더를 만든다.
cd guestbook # 해당 폴도로 이동한다.
docker-compose.yaml # 파일을 만들어준다.
docker-compose up -d # 실행해준다. 이러면 이 안의 container들은 모두 하나의 network에 들어가게 된다.
docker stop guestbook_backend_1 guestbook_frontend_1 guestbook_mongodb_1 # 각 컨테이너 종료
docker-compose down # docker compose를 종료해준다.
```

- 조회 : docker-compose ps : db & wordpress
- 로그보기 : docker-compose logs

### 주의사항

- 띄어쓰기, 계층구조

- 내가 만들다 실수한 상황

  > 물론 아래 상황은 잘 안보이는 글씨 때문에 그런것도 있다.

```yaml
version: '2'
services: # 맨 뒤에 's'를 빼먹어서 오류발생
  db:
    image: mysql:5.7
    volumes:
      - ./mysql:/var/lib/mysql # -를 ~로 오인해서 오류발생
    restart: always
    ports:
      - "4000:3306"
    environment: # enviro'n'ment의 n을 빼먹어서 오류발생
      MYSQL_ROOT_PASSWORD: wordpress
      MYSQL_DATABASE: wordpress
      MYSQL_USER: wordpress
      MYSQL_PASSWORD: wordpress
  wordpress:
    image: wordpress:latest
    volumes:
      - ./wp:/var/www/html
    ports:
      - "8000:80"
    restart: always
    environment:
      WORDPRESS_DB_HOST: db:4000
      WORDPRESS_DB_PASSWORD: wordpress
```

