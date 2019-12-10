## Java 자료형 모음

### Multimap

- 발견경로 : 알고리즘 공부중 우연히
- 자료출처
  [김용환 님의 블로그](https://knight76.tistory.com/entry/Guava-Multimap-%EC%98%88%EC%A0%9C)

- [메이븐 레파지토리 주소](https://mvnrepository.com/artifact/com.google.guava/guava)

- 일반적인 HashMap은 동일한 key가 존재하는 경우 나중에 들어온 것이 먼저 있던것을 대체한다.
- 그런데 multimap은 동일한 키가 있을 경우 value를 collection으로 해서 하나의 키에 여러개의 값을 가질 수 있게 했다.

- 그분 소스 복사 붙여넣기

```java
Multimap<String, String> family = ArrayListMultimap.create();

family.put("Father Kimchy", "1st Son-Kimchy");
family.put("Father Kimchy", "1st Daughter-kimchy");
family.put("Father Jason", "1st Son-Jason");

Collection<String> child = family.get("Father Kimchy");
System.out.println(child);
child = family.get("Father Jason");
System.out.println(child);
```

- 결과

```
[1st Son-Kimchy, 1st Daughter-kimchy]
[1st Son-Jason]
```

- 결과 : 열심히 알면 머하냐 ㅇㅅㅇ 알고리즘이라 공부라 넣지도 못하는데 ㅋㅋㅋ