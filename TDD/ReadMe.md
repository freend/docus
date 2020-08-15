# TDD

이곳에는 TDD를 하면서 배우고 적용한 내용에 대해서 기술하려고 한다. 그리고 TDD개발론을 그대로 따르는게 아님을 명심하도록 하자. TDD 개발론을 알고 싶다면 다른 곳을 참조하기 바란다.

사용 스팩

- spring boot 2
- jpa
- junit 4

TDD의 장점을 몇가지로 요약하자면 다음과 같다.

- 내가 원하는 대로 작동하는지 확인할 수 있다.
- 원하는 부분만 확인할 수 있다.
- 빌드를 할 때 테스트를 하게되면 로직의 변경으로 의도치 않게 발생하는 오류를 미리 찾을 수 있다.
- 이걸 위해서 객체지향 설계의 원칙인 "단일 책임 원칙"에 충실해 지게 된다.

개인적인 견해로 TDD는 다른 서비스 등은 최대한 mock을 하는게 좋다. 처음엔 데이터베이스나 다른 서비스에 연동을 하는 방식으로 했는데 이것은 의미가 없는거 같다. 왜냐하면 다른 서비스는 다른 테스트 케이스에서 사용하고 디비는 타인과의 데이터가 다를수도 없을수도 있기 때문이다.

## Controller

- [TDD Setting](Controller.md)

- [Token을 사용하는데 테스트해야 해요](Jwt.md)

## Service

- [save를 테스트 할때 오류가 나요](Save.md)

- [Page Request를 구현하고 싶어요](PageRequest.md)

- [PasswordEncoder를 Autowired 없이 사용하기](PasswordEncoder.md)

## Other

- [Any를 사용하는데 Invalid use of argument matchers! 가 발생해요](ProblemAny.md)