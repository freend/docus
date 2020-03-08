## Git Tool









### Source Tree

현재 git gui tool로 회사 업무의 기본이다.
[source tree의 사용법]()

## Git work flow

- commit(작업내용의 로컬저장)은 기본적으로 작업단위이다. 즉 기능 업데이트 작업이 완료되면 그곳에서 커밋을 진행한다는 의미이다.
- push(로컬저장 내용의 리모트저장)은 굳이 작업단위로 하지 않아도 된다.
- commit만 해둔 상태에서는 SquashCommit(커밋 합치기) 등을 사용할 수 있다. 하지만 서버로 push한 뒤에는 변경불가능 하다.
- pull(내려받기)는 타인의 작업 내용을 적용할 때 사용하는 부분이다.

- git은 기본적으로 pull이 push보다 우선이다. 타인이 작업해서 올린 작업이 아직 리모트에 적용되지 않은 내 작업물보다 우선이기 때문이다.

## Git Terminal Command

로컬에서는 위에 명시된 source tree를 사용하는게 훨씬 편하다. 하지만 서버는 거의 terminal환경에서 작업해야 하므로 아래의 커멘드를 익히는 것이 좋다.

### Git clone all branch
clone은 remote repository에 있는 모든 내용을 local로 복제하는 명령으로 진행중인 작업에 합류할 때 가장 기본 기능이다.
```shell
#폴더를 생성하며 그곳에 클론
git clone {remote URL} {local Folder name}
#현재 위치에 클론
git clone {remote URL}
```
### Git Single Clone

하나의 브런치만 clone하는 기능으로 optional한 기능이다.

```shell
git clone -b {branch_name} --single-branch {저장소 URL}
```

### Git check out
Check out은 로컬에 없고 리모트에만 존재하는 브런치를 받는 기능이라고 생각하면 된다. '-b' 옵션은 해당 리모트 브런치의 작업내역까지 내려받는 명령으로 필수 명령이다.

```shell
git checkout -b [LocalBranchName] [RemoteBranchName]
```

### Git의 현재 커밋의 위치 알아보기

```shell
git rev-parse HEAD
```

### 다시 돌아오는 방법

```shell
git checkout master
```

마스터 브랜치로 복귀하는 게 돌아오는 명령어다. master 브랜치가 아닌 다른 브랜치명을 적어도 상관없다. 해당 브랜치로 복귀하게 된다.

### 특정 커밋을 콕 찝어서 돌아가기

각각의 commit은 commit 해시값이 있다. 알파벳과 숫자 조합으로 되어 있는 코드다. 이게 해당 커밋의 고유번호다. 그리고 이 고유번호의 앞 6자리만 적어 주면 알아서 식별을 한다. 그래서, 특정 커밋으로 돌아가고 싶다면?

```shell
git checkout hashcode 6자리
```

최신 버전으로 이동하고 싶으면

```shell
git checkout master
```

### Git의 Pull이 실패하는 경우

- 증상

다음과 같은 에러 메시지 발생하고 pull 실패

```shell
You have not concluded your merge (MERGE_HEAD exists).
Please, commit your changes before you can merge.
```

- 원인

바로 직전 pull 시 머지를 시도했지만 conflict 가 발생하여 충돌 상태일 경우 발생함.

- 처리
   + 머지를 취소하고 다시 pull 을 받음

   ```shell
   git merge --abort
   ```

   + 충돌 해결 후 다시 pull 받음
   + 변경 내역을 커밋하고 다시 pull

### Git remote site command

#### 원격저장소 연결하기

`Github`의 레포지토리를 기준으로 설명하였다. 웹에서 레포지토리를 생성한 후 레포지토리 주소를 터미널에서 연결하기 원하는 로컬 저장소 폴더에 등록해준다.

```shell
#git remote add origin <repository주소>
$ git remote add origin https://github.com/githubId/repository.git
```

#### 하나의 로컬저장소에 원격저장소 추가로 연결하기

레포지토리는 여러 개 등록할 수도 있으나 원격 저장소의 이름을 다른 것으로 해준다.

```shell
#git remote add other <other_repository>
$ git remote add other https://github.com/githubId/other_repository.git
```

이 때는 푸시할 때 원격 저장소 이름을 구분하여 버전관리를 해준다.

```shell
#origin 원격저장소에 푸시
$ git push origin master

#other 원격저장소에 푸시
$ git push other master
```

#### 원격저장소 연결 확인하기

`-v` 옵션을 통해 연결한 저장소의 상태를 체크할 수 있다.

```shell
$ git remote -v
origin	https://github.com/githubId/***********.repository.git (fetch)
origin	https://github.com/githubId/***********.repository.git (push)
```

위에서 `fetch`는 파일을 불러오는 저장소를 의미하며, `push` 파일을 저장하는 저장소를 의미한다.

## Git Hook

hook을 사용하려 했는데 최근 버전의 git에는 hook이 보이지 않았다.
그래서 조회해보니 npm에 hook이 있었다.

```shell
https://www.npmjs.com/package/git-hookshttps://help.github.com/articles/allowing-people-to-fork-private-repositories-in-your-organization/)
```

## Git Change name, E mail

- 계정, 이메일 확인

```shell
git config user.name

git config user.email
```

- 변경해 주기

```shell
git config --global user.name 신계정

git config --global user.email 신이메일
```

- 그 외에 ssh 설정등의 기능이 있는데 작성자의 주소를 링크걸었으니 가서 확인해보기 바란다.

[바로가기]: https://meaownworld.tistory.com/78



