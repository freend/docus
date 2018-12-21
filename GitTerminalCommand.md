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

사실 git에서 리비전이란 말을 들어 본 적은 없다. SVN에 익숙한 사람들을 위해서 ‘리비전’이라고 제목에 적었고, 사실 특정 커밋이라고 해야 말이 될 거다. 여튼, 특정 커밋으로 어떻게 돌아갈까?

아래와 같은 로그가 있다고 치자.

```
commit bd32ba7c2c1bf1e793c6c951856e35ec7f397daa
Author: 안형우 <mytory@gmail.com>
Date:   Tue May 7 08:04:36 2013 +0900

    커밋3

commit 8553f2530e01cbd66d135d43e11d1d2f9366b5f8
Author: 안형우 <mytory@gmail.com>
Date:   Tue May 7 08:04:02 2013 +0900

    커밋2

commit 5fa1c73e90b5b14a4cab49031afa0c9bdea1c587
Author: 안형우 <mytory@gmail.com>
Date:   Tue May 7 08:03:45 2013 +0900

    커밋1
```

세 개의 커밋이다. 각각 commit 해시값이 있다. 졸라 이상한 알파벳과 숫자 조합 말이다. 이게 해당 커밋의 고유번호다. 그리고 이 고유번호의 앞 6자리만 적어 주면 알아서 식별을 한다. 그래서, 커밋2로 돌아가고 싶다면?

```
git checkout 8553f2
```

이렇게 적어 주면 된다. 그리고 최신 버전으로 돌아오고 싶다면 언제든

```
git checkout master
```

이렇게 적어 주면 된다.

끝!

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