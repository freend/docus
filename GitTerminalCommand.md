## Terminal Git Check out

```
git checkout -b [localbranchname] [remotebranchname]
```

## Git Single Clone

```
git clone -b {branch_name} --single-branch {저장소 URL}
```



## Git의 현재 커밋의 위치 알아보기

```
git rev-parse HEAD
```

### 다시 돌아오는 방법

```
git checkout master
```

마스터 브랜치로 복귀하는 게 돌아오는 명령어다. master 브랜치가 아닌 다른 브랜치명을 적어도 상관없다. 해당 브랜치로 복귀하게 된다.

## 특정 커밋을 콕 찝어서 돌아가기

각각의 commit은 commit 해시값이 있다. 알파벳과 숫자 조합으로 되어 있는 코드다. 이게 해당 커밋의 고유번호다. 그리고 이 고유번호의 앞 6자리만 적어 주면 알아서 식별을 한다. 그래서, 특정 커밋으로 돌아가고 싶다면?

```
git checkout hashcode 6자리
```

최신 버전으로 이동하고 싶으면

```
git checkout master
```

## Git의 Pull이 실패하는 경우

- 증상

다음과 같은 에러 메시지 발생하고 pull 실패

```
You have not concluded your merge (MERGE_HEAD exists).
Please, commit your changes before you can merge.
```

- 원인

바로 직전 pull 시 머지를 시도했지만 conflict 가 발생하여 충돌 상태일 경우 발생함.

- 처리
   + 머지를 취소하고 다시 pull 을 받음

   ```
   git merge --abort
   ```

   + 충돌 해결 후 다시 pull 받음
   + 변경 내역을 커밋하고 다시 pull

## Git remote site command

### 원격저장소 연결하기

`Github`의 레포지토리를 기준으로 설명하였다. 웹에서 레포지토리를 생성한 후 레포지토리 주소를 터미널에서 연결하기 원하는 로컬 저장소 폴더에 등록해준다.

```
# git remote add origin <repository주소>
$ git remote add origin https://github.com/githubId/repository.git
```

### 하나의 로컬저장소에 원격저장소 추가로 연결하기

레포지토리는 여러 개 등록할 수도 있으나 원격 저장소의 이름을 다른 것으로 해준다.

```
# git remote add other <other_repository>
$ git remote add other https://github.com/githubId/other_repository.git
```

이 때는 푸시할 때 원격 저장소 이름을 구분하여 버전관리를 해준다.

```
# origin 원격저장소에 푸시
$ git push origin master

# other 원격저장소에 푸시
$ git push other master
```

### 원격저장소 연결 확인하기

`-v` 옵션을 통해 연결한 저장소의 상태를 체크할 수 있다.

```
$ git remote -v
origin	https://github.com/githubId/***********.repository.git (fetch)
origin	https://github.com/githubId/***********.repository.git (push)
```

위에서 `fetch`는 파일을 불러오는 저장소를 의미하며, `push` 파일을 저장하는 저장소를 의미한다.

## Git Hook

hook을 사용하려 했는데 최근 버전의 git에는 hook이 보이지 않았다.
그래서 조회해보니 npm에 hook이 있었다.

```
https://www.npmjs.com/package/git-hooks
```

