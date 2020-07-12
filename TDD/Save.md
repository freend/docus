## Save를 할 때 오류가 나요

junit으로 save를 할 때 보면 save결과가 null이 나오는 경우가 있다. 이 경우의 해결책에 대해서 알아보자.

##### NoticeService.java

```java
package com.freend.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {
    final NoticeRepository noticeRepository;
  	public NoticeDto addNotice(NoticeDto dto) {
        Notice notice = noticeRepository.save(
                Notice.builder().title(dto.getTitle()).content(dto.getContent()).build()
        );
        return NoticeDto.builder().id(notice.getId()).title(notice.getTitle())
							.content(notice.getContent()).build();
    }
}

```

위와같이 새로운 공지사항을 저장하는 service를 추가했다 하자. 이제 이것이 테스트 코드에서 정상 작동하는지 확인하도록 하자.

> 서비스를 만들때 꼭 Autowired를 사용하지 않고 RequiredArgsConstructor을 사용하고 repository나 service를 넣도록 한다. 왜 그런지는 구글에서 '스프링 생성자 주입'으로 검색해 보도록 하라.
>
> 위와같이 해야 테스트 사용시 Mocking을 사용할 수 있기 때문이다. Mocking에 대해서는 [다음글을 참조하라](https://jojoldu.tistory.com/226)

이제 Test Class를 만들자.

##### NoticeServiceTest.java

```java
package com.freend.notice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class NoticeServiceTest {
    NoticeService noticeService;
  	// 여기엔 bean연결을 해주자. 여긴 2가지가 들어갈 수 있다. Autowired와 MockBean
  	// 하나씩 설명하겠다. 우선은 Autowired
  	@Autowired
    NoticeRepository noticeRepository;
  	@Before
    public void setUp() {
        service = new NoticeService(noticeRepository); //여기서 생성자를 만들어 준다.
    }
	  @Test
    public void 저장테스트() {
        // when
        NoticeDto dto = service.addNotice(
                NoticeDto.builder().title("sample").content("notice sample").build()
        );
        // then
        assertThat("id는 1이다.", dto.getId(), equalTo(1L));
    }
}
```

우선 위와 같이 하니 다음과 같은 애러가 발생했다. "UnsatisfiedDependencyException: Error creating bean" 이것을 해결하기 위해서 `@SpringBootTest` 를 추가해서 모든 bean 생성을 해서 테스트를 해줄 수 있다.

이렇게 하고 보니 local로 설정해 놓은 나의 데이터 베이스에 직접 추가가 되어 원하는 결과가 나오지 않았다. 이럴때 h2 data base나 그런거로 전부 초기화를 해서 테스트를 하는 경우도 있는데 이것보다는 테스트 하는 부분의 격리를 해서 원하는 결과를 만들기 위해서 repository를 MockBean으로 변경해서 하는게 훨씬 좋다.

##### NoticeServiceTest.java

```java
package com.freend.notice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest 이제 이건 없어도 된다.
public class NoticeServiceTest {
    NoticeService noticeService;
  	// 여기엔 bean연결을 해주자. 여긴 2가지가 들어갈 수 있다. Autowired와 MockBean
  	// 하나씩 설명하겠다. 이번엔 MockBean
  	@MockBean
    NoticeRepository noticeRepository;
  	@Before
    public void setUp() {
        service = new NoticeService(noticeRepository); //여기서 생성자를 만들어 준다.
    }
	  @Test
    public void 저장테스트() {
        // when
        NoticeDto dto = service.addNotice(
                NoticeDto.builder().title("sample").content("notice sample").build()
        );
        // then
        assertThat("id는 1이다.", dto.getId(), equalTo(1L));
    }
}
```

이렇게 하면 'java.lang.NullPointerException' 이 발생한다. 이유는 다음과 같다.

AutoWired로 만든 정상 Bean을 이번엔 Mock으로 위장을 했으니 저장을 하지 못해서 오류가 발생하는 것이다. 그럼 무엇을 해줘야 할까? 바로 given으로 조건을 주는 것이다. 다음과 같이 given을 추가해주자.

##### NoticeServiceTest.java

```java
package com.freend.notice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class NoticeServiceTest {
    NoticeService noticeService;
  	// 여기엔 bean연결을 해주자. 여긴 2가지가 들어갈 수 있다. Autowired와 MockBean
  	// 하나씩 설명하겠다. 우선은 Autowired
  	@Autowired
    NoticeRepository noticeRepository;
  	@Before
    public void setUp() {
        service = new NoticeService(noticeRepository); //여기서 생성자를 만들어 준다.
    }
	  @Test
    public void 저장테스트() {
				// given
        given(noticeRepository.save(Notice.builder().title("sample")
                                    .content("notice sample").build()))
                .willReturn(
                        Notice.builder().id(1L).title("sample")
          .content("notice sample").build()
                );
        // when
        NoticeDto dto = service.addNotice(
                NoticeDto.builder().title("sample").content("notice sample").build()
        );
        // then
        assertThat("id는 1이다.", dto.getId(), equalTo(1L));
    }
}
```

given은 Mockito의 함수로 given안의 내용을 받으면 willReturn으로 값을 반환하라는 의미이다. 이제 다시 해보면

또 'java.lang.NullPointerException'이 발생한다. 여기서 한동안 고생했는데 알고보니 given에서 만든 builder와 서비스에서 만드는 builder가 서로 다른 객체이기 때문에 발생한 것이다. 예를 들면 아래와 같이 생각하면 쉽다.

```java
// test
given(someMethod(1)).willReturn("right");
// service
String someMethod(int num) {
  String result = "";
  if (num == 1) {
    result = "right";
  } else {
    result = "false";
  }
  return result;
}
```

위의 함수는 그냥 예시로 만든거니 머라고 하지 마시길.. 이런경우는 정상적으로 "right"로 나온다는 이야기다. 그럼 save는 테스트를 못하는 걸까? 아니다. 다음과 같이 바꾸면 된다.

##### NoticeServiceTest.java

```java
package com.freend.notice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any; // 이게 핵심이다.
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class NoticeServiceTest {
    NoticeService noticeService;
  	// 여기엔 bean연결을 해주자. 여긴 2가지가 들어갈 수 있다. Autowired와 MockBean
  	// 하나씩 설명하겠다. 우선은 Autowired
  	@Autowired
    NoticeRepository noticeRepository;
  	@Before
    public void setUp() {
        service = new NoticeService(noticeRepository); //여기서 생성자를 만들어 준다.
    }
	  @Test
    public void 저장테스트() {
      	// given
        given(noticeRepository.save(any(Notice.class)))
                .willReturn(
                        Notice.builder().id(1L).title("sample")
          .content("notice sample").build()
                );
        // when
        NoticeDto dto = service.addNotice(
                NoticeDto.builder().title("sample").content("notice sample").build()
        );
        // then
        assertThat("id는 1이다.", dto.getId(), equalTo(1L));
    }
}
```

핵심은 바로 이 부분이다.

```java
import static org.mockito.ArgumentMatchers.any;
// Notice.class로 된 어떤 것이든 주어지면
				given(noticeRepository.save(any(Notice.class)))
          // 이렇게 반환하라.
					  .willReturn(
                        Notice.builder().id(1L).title("sample")
          .content("notice sample").build()
                );
```

위와같이 하면 완전히 독립된 상태로 나의 서비스가 원하는 값을 반환하는지 알 수 있다. 이제 테스트를 실행하면 passed를 반환할 것이다.

#### 결론

bean을 이용하는 Mock에서는 org.mockito.ArgumentMatchers.any 를 사용하자.