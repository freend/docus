## 2장

### 동작 파라미터화

- 자주 바뀌는 요구사항에 효과적으로 대응 가능.
- 비슷한 코드가 반복 존재하면 코드를 추상화 하라

```java
// 기존에 사용하던 방식.
    private List<Apple> appleFilter(List<Apple> inventory) {
        List<Apple> response = new ArrayList<>();
        for (Apple apple : inventory) {
          	// 여기만 계속적으로 변할수 있는 부분이다.
            if (apple.getColor().toUpperCase().equals("GREEN")) { 
                response.add(apple);
            }
        }
        return response;
    }
```

- 알고리즘(전략)을 캡슐화 한 후 런타임에 알고리즘을 선택하는 방법

- 알고리즘 페밀리 생성

```java
public interface ApplePredicate {
  boolean isFind(Apple apple);
}
```

- 알고리즘 생성

```java
public class WeightPredicate implements ApplePredicate {
  @Override
  public boolean isFind(Apple apple) {
    return apple.getWeight() > 150;
  }
}
```
- 알고리즘을 파라미터로 만든 구현체

```java
private List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
  List<Apple> result = new ArrayList<>();
  for (Apple apple : inventory) {
    if(p.isFind) {
      result.add(apple);
    }
  }
  return result;
}
```

- 구현체 적용

```java
List<Apple> filterList = filterApples(List<Apple> inventory, new AppleWeightRedPredicate());
```

### 익명 클래스

- 위와 같이 하는것도 많은 과정이 들어서 줄이기에 들어간다.

```java
List<Apple> redApples = filterApples(inventory, new ApplePredicate() {
            @Override
            public boolean isFind(Apple apple) {
                return apple.getWeight() > 150;
            }
        });
```

- 이렇게 하면 위의 과정중 알고리즘 생성 부분이 없어지게 되는 것이다.

- 이것을 람다표현식으로 나타내면 다음과 같다.
  - 핵심적인 부분은 사과의 무게는 150이 넘느냐이다.

```java
List<Apple> redApples = filterApples(inventory, (Apple apple) -> apple.getWeight() > 150);
```

- 이렇게 하면 구현체 적용 부분이 훨씬 간단해 졌다.

### 전체 추상화

- 

