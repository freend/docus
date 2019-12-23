## Jenv로 자바버전 여러개를 관리하자

### 개기

- 공장 초기화 후 자바를 설치하던 중 jenv의 존재를 알게 됨
- brew로 관리해서 편하다는 생각도 한몫 함

### 목차

- [시작은 이것으로](#chapter-1)
- [8버전이 안깔려요](#chapter-2)
- [드디어 성공](#chapter-3)

### 시작은 이것으로 
<a id="chapter-1"></a>
**jojoldu 님의 홈페이지를 보고 시작하게 되었다.**

- 최신 자바버전 설치

```shell
brew cask info java
brew cask install java
```

- jenv 설치

```shell
brew install jenv
```

- 위와 같이 해서 나의 자바 버전은 13.0.1이다.
- bash_profile에 넣을 내용이 있는데 이 부분은 공식 홈페이지의 내용을 따랐다.

```shell
Bash
$ echo 'export PATH="$HOME/.jenv/bin:$PATH"' >> ~/.bash_profile
$ echo 'eval "$(jenv init -)"' >> ~/.bash_profile
$ source ~/.bash_profile
ZSH
$ echo 'export PATH="$HOME/.jenv/bin:$PATH"' >> ~/.zshrc
$ echo 'eval "$(jenv init -)"' >> ~/.zshrc
$ source ~/.zshrc
```

- 막히기 시작

  - 그다음을 보면 바로 다음과 같은 내용이 나온다.

  ```shell
  jenv add /Library/Java/JavaVirtualMachines/jdk1.8.0_162.jdk/Contents/Home/
  ```

  - 똑같이 하니 열심히 에러를 출력시키기 시작했다. 원인은 단순했다. jenv가 깔아주는 건줄 알았는데 아니였다.

### 8버전이 안깔려요
<a id="chapter-2"></a>
- 8버전을 이제 brew를 통해 설치하려고 했더니 brew는 일반적으로 구버전중 계속 쓰이는 거 (sql)이 아니면 구버전을 지원 안한다고 하더라. 눈앞이 깜깜했다. 어디서는 oracle에서 다운받으라고 했는데. 그럼 그냥 첨부터 그거쓰지 왜~
- jenv로 이리저리 검색을 하던 중 J.S Ahn 님의 홈페이지를 보고 이거구나!! 하고 실행했다
- AdoptOpenJDK는 사전에 prebuild 형태로 java binary를 제공하는 커뮤니티 그룹이다 라는 내용과 함께 다음과 같이 실행했다.

```shell
brew tap AdoptOpenJDK/openjdk
brew cask install <version>

JDK Version
OpenJDK8 - adoptopenjdk8
OpenJDK9 - adoptopenjdk9
OpenJDK10 - adoptopenjdk10
OpenJDK11 - adoptopenjdk11
OpenJDK11 w/ OpenJ9 JVM - adoptopenjdk11-openj9
```

- 나도 8버전을 설치할 것이므로 다음과 같이 했다.

```shell
brew tap AdoptOpenJDK/openjdk
brew cask install adoptopenjdk8
```

### 드디어 성공 
<a id="chapter-3"></a>

- 8버전 까지는 성공했는데 이제 jenv에 추가해야 하는 문제가 남았다. 그런데 jenv에 추가할 때 보면 다음과 같다

```
jenv add /Library/Java/JavaVirtualMachines/jdk1.8.0_162.jdk/Contents/Home/
```

- 이런 내가 jdk의 버전이 뭔지 어떻게 알아?? 휴 안되면 또 검색해야지 하고 jenv관련 페이지를 확인하던중 Jungbin Kim님의 홈페이지를 찾게 되었다.

```shell
# java home에 설치 되었는지 확인 
$ /usr/libexec/java_home -v 1.8
/Library/Java/JavaVirtualMachines/jdk1.8.0_232.jdk/Contents/Home
```

- 위의 설명대로 하니 경로를 찾을 수 있어서 java8을 jenv에 추가할 수 있었다.
- 이제 java8을 global로 변경하자

```shell
$ jenv global 1.8.0.232
$ java -version
openjdk version "1.8.0_232"
OpenJDK Runtime Environment (AdoptOpenJDK)(build 1.8.0_232-b09)
OpenJDK 64-Bit Server VM (AdoptOpenJDK)(build 25.232-b09, mixed mode)
```

- 다 됐다

### 관련링크

- [jojoldu님의 홈페이지](https://jojoldu.tistory.com/329)

- [J.S Ahn님의 홈페이지](https://findstar.pe.kr/2019/01/20/install-openjdk-by-homebrew/)

- [Jungbin Kim님의 홈페이지](https://blog.jungbin.kim/notes/2018-04-28-java-version-manager-jenv/)
- [공식홈페이지](https://www.jenv.be/)

