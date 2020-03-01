# Hash map함수

### 기본정보

map 만들기, put, get 같은 내용은 여기에 없다. 자바 HashMap관련 글을 읽던 도중 디사한번 정리한 내용이다.

### Setting

```java
Map map = new HashMap<>();
```

### putIfAbsent()

```java
boolean isContain = map.containsKey("apple"); //false

map.putIfAbsent("apple", 1);
System.out.println(map.get("apple")); // 1

map.putIfAbsent("apple", 3);
System.out.println(map.get("apple")); // 1
```

- 즉 key가 없으면 추가하고 있는 경우에는 가지고 있는 값을 반환한다. 절대로 뒤 arguments가 적용되는게 아니다.
- computeIfAbsent 도 있다. 근데 계산을 안하면 putIfAbsent를 사용하길 권한다.

### computeIfPresent(), compute()

- 그냥 개인적으론 왜 이리 복잡하게 만들었나 싶기도 하다. 람다식을 넣어야 하는데

```java
map.computeIfPresent("apple", (key, value) -> 20);
System.out.println(map.get("apple")); // 20
```

- 아니 앞에 key도 있고 ... 그냥 value만 넣으면 되는데.. 라고 생각하고 까봤다.. 사실 람다식에 익숙하지 못해서 적응하는 과정 포함이다.

```java
map.compute("banana", (key, value) -> 20);
System.out.println(map.get("banana")); // 20
map.computeIfPresent("melon", (key, value) -> 20);
System.out.println(map.get("melon")); // null
```

- 결국 차이는 computeIfPresent는 key가 없으면 null을 반환한다 이다

```
map.computeIfPresent("apple", (key, value) -> null);
System.out.println(map.containsKey("apple")); // false
map.compute("apple", (key, value) -> null);
System.out.println(map.containsKey("apple")); // false
map.put("apple", null);
System.out.println(map.containsKey("apple")); // true
```

- 이번엔 null을 넣은 경우다. put의 경우 null값을 가지고 존재하나 compute, computeIfPresent는 지운다.

### forEach(), replaceAll()

- 남은 함수 없나 뒤지던 와중에 다음의 두가지를 더 찾을 수 있었다. 사용법은 위와 비슷하다.

```
map.put("apple", 10);
map.put("banana", 20);
map.forEach((key, value) -> System.out.println("key : " + key + ", value : " + value));
// 10, 20
map.replaceAll((key, value) -> value + 20);
map.forEach((key, value) -> System.out.println("key : " + key + ", value : " + value));
// 30, 40
```

