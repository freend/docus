## Docker 파일 공유하기

### Docker와 호스트 파일 공유하기

- 호스트와 docker간의 파일을 공유하기 위해서는 `-v` 명령을 사용한다

```shell
# docker run -v <호스트 경로>:<컨테이너 내 경로>:<권한>
$ docker run -it --name data_share -v ~/Documents/data_dir:/data_dir ubuntu /bin/bash
```

- 권한은 ro(readonly), rw(readwrite)가 있다. 기본값은 ro이다.

### Docker의 Container Volume공유

- volumes-from을 사용하면 동일한 volume를 선언한 모든 컨테이너에서 공유하게 된다.

```shell
docker run -it --name data_share -v ~/Documents/data_dir:/data_dir ubuntu /bin/bash
docker run -it --name data_share_c_A --volumes-from data_share ubuntu /bin/bash
docker run -it --name data_share_c_B --volumes-from data_share ubuntu /bin/bash
```

- 위와같이 하면 data_share, data_share_c_A, data_share_c_B 3개의 컨테이너가 작동한다
- 이때 Documents/data_dir에 임의의 파일을 넣고 각 컨테이너의 data_dir 을 보면 동일한 파일이 들어가 있다.