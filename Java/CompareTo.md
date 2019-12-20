## CompareTo

정말 옆으로 빙글빙글 돌아다니기가 장난이 아니다. 모던 자바 인 액션을 공부하다가 예제로 자주 나오는 compareTo를 접하게 되었다. 별 생각없이 보다가 실습을 위해 써봤는데 바로 에러발생. 확인해 보기로 했다.

```java
int num1 = 1;
int num2 = 2;

int result = num1.compareTo(num2); //can not resolve compareTo
// 여기서 왜 이러지? 하고 있었음. 그래서 다음과 같이 바꿈
Integer num1 = 1;
Integer num2 = 2;

int result = num1.compareTo(num2); //not error
```

- compareTo는 Reference Data Type이다. 설마 이것도 모르는 사람이 .. 있을지도 모르겠다.. 나도 얼마전까진...

### Java Data Type의 종류

- Primitive Data Type
  - byte, short, int, long, float, double, boolean, char의 8가지
- Reference Data Type
  - Primitive Data Type을 제외한 모두 다

차이를 모르겠다? 그럼 다음의 코드를 보면 바로 알 수 있다.

```java
int num1 = null; // error
Integer num2 = null; // not error
```

- Primitive Data Type 은 순수한 데이터 타잎이고 Reference Data Type은 class로 wrapping한 것이라고 생각하면 쉽다.
- String 데이터 타잎을 생각하면 쉽다.

### 진행

두 수를 비교해서 앞이 크면 1 같으면 0 작으면 -1을 반환하는구나.

나는 모던 인 자바를 공부하는 중이였다. 거기서는 이렇게 표기가 되어 있었다.

```java
//따라해 보자.
(Apple a1, Apple a2) -> a1.getWeight.compareTo(a2.getWeight());
//어라 에러가 발생하네 return 이 되어야 하니 데이터 타입을 선언해 볼까?
int result = (Apple a1, Apple a2) -> a1.getWeight.compareTo(a2.getWeight());
// 그래도 에러 발생?? 뭐지 이건? 어디 오류를 보자
/**
Target Type of lambda conversion must be an interface 라는 오류네... 엥? 이게 머지? 하다 책을 보니 
다음줄과 같이 되어 있다.
**/
Comparator<Apple> byWeight = (Apple a1, Apple a2) -> a1.getWeight.compareTo(a2.getWeight());
```

우선 오류가 없는 저 상태로 가게 되었다. 근데 문제는 응용을 해보려니 할 줄을 모르겠더라. 그리고 comparator?? 저건 뭐지?
그래서 인터넷을 뒤지던 중 자바정렬의 내용을 알게 되었고 해 보기로 했다.

### 정렬

```java
String [] participant = {"mislav", "stanko", "mislav", "ana"};
// 이것을 알파벳 순으로 정렬하자.
Array.sort(participant); //{"ana", "mislav", "mislav", "stanko"}
// 기본은 순방향 정렬이다. 이번엔 역방향 정렬로 바꿔보자
Array.sort(participant, Comparator.reverseOrder());// {"stanko", "mislav", "mislav", "ana"}
```

이렇게 동일한 기본 데이터 형이 있으면 쉽게 정렬이 가능하다. 처음엔 출처의 자바정렬 부분을 보고 공부를 했다. 내용을 보던중 Comparable 이라는 단어가 나온다. 엥?? 저게 뭐지?? 즉 기본 데이터 타입(String, Integer, Date 등)으로 된 것을 정렬할 때 쓰는 인터페이스 라고 한다.

이제 다른 경우인 객체에서 정렬을 할 경우 입니다. 이 경우는 기준을 정의 할 수 없기에 정렬의 기준이 되는 것을 만들어야 한다.그래서 코드를 분석한 결과는 다음과 같다

```java
// 이건 내가 생각하던 두 수를 비교해서 결과를 반환하라가 아니라 무게를 기준으로 정렬의 기준을 만든다 의 의미다.
Comparator<Apple> byWeight = (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
// 아마 이 내용이 있었으면 더 쉽게 이해했겠지
Collections.sort(sources, byWeight);
```

결국 여기선 람다를 설명하기 위해서 위의 한줄만 보여준 것이고 그것을 잘못 이해한 나는 계속 정확한 사용을 찾아 다닌 것이다.

### 결론

사실 책에서는 그냥 넘어가라고 한다. 아마 뒤에서는 설명이 나오겠지? 사실 알게 되니 매우 허무했다 아니 사실상 나의 선입견적 사고가 부른 비극일지도 

- sort를 위해 사용하는 부분을 두 수를 비교하는거로 생각해 버린 문제
- Comparator를 몰랐던 것.

악으로 깡으로 익히게 되서 정확히 이해했다는 승리감을 가질지 시간을 낭비했다는 효율중심을 택할지는 각자 선택하자.

### 출처

- [데이터 타입](https://hyungjoon6876.github.io/jlog/2018/07/05/java-data-type.html)

- [자바정렬](https://cwondev.tistory.com/15)
- [자바정렬2](https://dev-daddy.tistory.com/23)

