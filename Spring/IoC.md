## IoC Inversion Of Control 제어권 역전 현상

### 작성하게 된 계기

백기선님의 예제로 배우는 스프링 입문을 다시한번 들어보자는 생각에 실행시킨후 ioc부분을 보다가 생각나서 정리하기 시작함.

### 내용

전에는 스프링 의존성을 클래스 내부에서 사용했다. 기억하는가 @Autowired를 이것을 자바식으로 표현하면 이렇게 된다.

```java
// 자바
class MyController {
  private MyRepository repo = new MyRepository();
}
// spring
class MyController {
  @Autowired
  private MyRepository repo;
}
```

초기에 프로젝트를 만들던 때는 이렇게 많이 사용했다. 생각해보니 이게 편해서 인듯 하다. 생성자 방식을 쓰면 일일이 찾아서 추가하고, 지우고 등을 해야해서 아마 이렇게 사용하지 않았나 생각된다.

이것이 의존성에 대한 제어권은 내가 만든다 라는 내용과 동일해 보였다.

그러던 것이 언제부턴가 필드인젝션을 하기보단 생성자를 통한 주입을 추천하게 되었다. 즉 아래와 같이 변경되었다.

```java
// 자바
class MyController {
  private MyRepository repo;
  public MyController (MyRepository repo) {
    this.repo = repo;
  }
}
// spring
@RequiredArgsConstructor
class MyController {
  final MyRepository repo;
}
```

이것이 Ioc의 개념과 동일한 느낌이 들었다. 즉 내가 사용할 의존성을 누군가 알아서 주겠지 라는 느낌인 것이다.

이렇게 하면 테스트 코드를 생성할 때 Mock 객체의 생성등이 쉬워진다.

### IoC 컨테이너

[스프링 프레임워크 공식문서](https://docs.spring.io/spring/docs/current/spring-framework-reference/index.html) 