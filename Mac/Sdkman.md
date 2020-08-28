## SDKMAN

### 계기

Gradle을 깔려고 보니 이것도 특정버전만 설치할 수 있는게 있을거 같아서 찾다가 발견함. 

그것의 이름은 바로 SDK MAN

설치가능한 것은 대충 다음과 같다

ant, gradle, groovy, maven, open-jdk

아래 사용법은 다 mac임을 밝힙니다

### 사용법

- 설치하기

  ```shell
  # 설치하기
  $ curl -s "https://get.sdkman.io" | bash
  # 적용하기
  $ source "$HOME/.sdkman/bin/sdkman-init.sh"
  # 버전확인
  $ sdk version
  ==== BROADCAST =================================================================
  * 2020-08-25: Gradle 6.6.1 released on SDKMAN! #gradle
  * 2020-08-23: Jbang 0.39.0 released on SDKMAN! Checkout https://github.com/jbangdev/jbang/releases/tag/v0.39.0. Follow @jbangdev #jbang
  * 2020-08-18: Jbang 0.38.0 released on SDKMAN! Checkout https://github.com/jbangdev/jbang/releases/tag/v0.38.0. Follow @jbangdev #jbang
  ================================================================================
  
  SDKMAN 5.9.0+555
  ```

- 설치 가능한 버전 목록 보기

  ```shell
  $ sdk list gradle
  ```

- 설치하기 - gradle

  ```shell
  $ sdk install gradle 6.0.1
  ```

- 설치한 버전 조회

  ```
  $ sdk list gradle
  ```

  ![](/Users/freend/Documents/freend/docus/Mac/Sdkman.assets/스크린샷 2020-09-01 오전 10.41.36.png)

- 버전 변경

  ```shell
  # 현재 쉘변경
  $ sdk use gradle X.X.X
  # Default
  $ sdk default gradle X.X.X
  ```

  

- 삭제하기는 공식 홈페이지 참조

### 참고사이트

[공식사이트](https://sdkman.io/) 

[SDKMAN으로 java버전 관리하기](https://phoby.github.io/sdkman/)

