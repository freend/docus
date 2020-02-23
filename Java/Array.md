## Array

### 이놈은

사실 확장성이나 그런게 적어서 그리 많이 사용하지는 않는 자료타잎인데 요즘 알고리즘 공부하다가 많이 건들게 된 자료구조이다

몇몇 재미난 기능이 있어서 여기에 올린다.

### 정렬



[자료출처](https://minaminaworld.tistory.com/70)

```java
import java.util.*;
class Solution {
    public String solution(int[] numbers) {
        String answer = "";
        String[] numArr = Arrays.toString(numbers).split("[\\[\\]]")[1].split(", ");

        Arrays.sort(numArr, Comparator.reverseOrder());
        int len = numArr.length - 1;
        int reverseSort = Integer.parseInt(numArr[len] + numArr[len - 1]);
        int naturalSort = Integer.parseInt(numArr[len - 1] + numArr[len]);

        if (Math.max(naturalSort, reverseSort) == reverseSort) {
            Arrays.sort(numArr, len - 1, len + 1);
        }

        for (int i = 0; i < numArr.length; i++) {
            answer += numArr[i];
        }
        return answer;
    }
}
```

