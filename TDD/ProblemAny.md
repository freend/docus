## Any의 문제

#### 개요

우리가 mock을 사용할 때 주로 사용하는게 given, when 이다. 이건 mock에 어떠한 경우가 주어졌을 때 우리가 원하는 값을 리턴해 달라는 매우 좋은 명령이다.

아래를 보면 dto 인자인 member의 구매내역을 Page로 달라는 내용이다. 이런 경우 arguments에서 dto등을 사용하는 경우에 any()라는 것을 사용한다.
이것을 사용하는 이유는 우리가 given에 넣은 instance와 실제로 적용하는 인스턴스가 달라서 null이 return되기 때문이다.

#### 문제

그런데 이렇게 좋은 기능이 문제가 되는 경우가 있다. 바로 arguments가 두개 이상인 경우이다. 다음의 경우를 보자

```java
given(purchaseService.getMemberPurchases(any(member), request.of()))
.willReturn(new PageImpl<Purchase>(purchases));
when(purchaseService.getMemberPurchases(any(member), request.of()))
.thenReturn(new PageImpl<Purchase>(purchases));
```

위와 비교하면 첫번째 arguments를 any로 놓은 것 밖에 없다. 그런데 이렇게 실행하면 다음과 같은 애러가 발생한다.

```shell
Invalid use of argument matchers!
2 matchers expected, 1 recorded:
```
그냥 딱 보면 2개가 기대되는데 한개만 기록되었다고 한다. 이 오류 메세지 내용때문에 고생했다. 
그냥 바보같이 2개가 기대되는데 1개만 사용했다고 생각한게 문제였다. 저 경우를 조사한 결과 둘다  any를 사용하던지 둘다 실 데이터를 넣으라는 이야기 였다.

#### 해결책

```java
given(purchaseService.getMemberPurchases(1L, request.of()))
.willReturn(new PageImpl<Purchase>(purchases));
when(purchaseService.getMemberPurchases(1L, request.of()))
.thenReturn(new PageImpl<Purchase>(purchases));
```
그래서 우선은 dto로 받던 것을 숫자로 받게 바꿨다. 그러니 잘되네

#### 개선점

둘다 any를 사용해 보고 싶긴한데 두번째 arguments가 Page Request네.. 좀 더 봐야겠다.