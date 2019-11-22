# Tree map 사용하기

### 사전정보

- 인덱싱 하게 될 데이터는 map데이터로 {가격 : 수량} 정보이다.

### 사용하게 된 이유

- List나 Array를 사용하게 된 경우 인덱스를 찾기 위해 항상 for문을 돌리면 O(N)에서 자유로울 수 없음
- 이에 대해 key를 기준으로 찾기위해 뒤지던 중 tree map에 대해 알게 되었다.
- treemap은 key의 깂에 따라 자동으로 정렬을 해준다.
- 즉 map.put(5, 10) map.put(1, 3) map.put(7,3)이면 다음과 같이 저장해 둔다

```json
{1=3,5=10,7=3}
```

- 만약 반대 순서로 저장하기 원한다면 treemap을 생성할때 다음과 같이  해줘야 한다.

```
TreeMap<Double, Long> items = new TreeMap<>(Collections.reverseOrder());
```

- index를 사용하지 못한다는 단점이 존재하긴 하지만 자동으로 정렬을 해주는 기능은 매우 매력적이다.

### 사용법

- 사용법은 map과 동일하다 추가는 put 변경시는 put, replace 불러올 때는 get 지울때는 remove
- headmap(key) : 처음부터 key전까지 추출하라
- tailmap(key) : key를 포함하여 끝까지 추출하라

- submap(fromkey, tokey) : fromkey포함 tokey전까지 추출하라.
  - 인덱스를 가지고 추출할 수 없기 때문에 인덱스를 사용하고 싶으면 추후 list등으로 변경해야 한다.
