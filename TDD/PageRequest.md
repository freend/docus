## Page Request를 구현하고 싶어요

jpa를 사용할 때 Page를 이용해서 목록을 구현한다. 이 경우 페이지 정보 전체 목록의 수량 등 여러가지 정보를 가져올 수 있어서 편하다. 하지만 이 경우 데이터를 넣어서 테스트 결과를 확인하기 어려운 경우도 있다. 이럴 때의 해결법에 대해서 알아보도록 하자

설명을 많이 쓰려 했지만 실질적으로 소스에 그냥 설명을 적는게 더 좋을거 같아서 주석으로 설명을 넣었다.

```java
package com.freend.contact;

import com.freend.common.PageRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
public class ContactServiceTest {
    ContactService contactService;
    @MockBean
    ContactRepository contactRepository;
    @Before
    public void setUp() {
        contactService = new ContactService(contactRepository);
    }

    @Test
    public void 문의내역_보기() {
        // given
        PageRequest pageRequest = new PageRequest();
      	// 결과물이 들어갈 리스트를 생성한다.
        List<Contact> list = new ArrayList<>();
        list.add(Contact.builder().title("a").content("aaa").build());
        list.add(Contact.builder().title("b").content("bbb").build());
      	// mock으로 선언한 findAll에 PageImpl을 넣은 리스트를 반환한다.
        given(contactRepository.findAll(pageRequest.of())).willReturn(
                new PageImpl<Contact>(list)
        );
        // when
        Page<Contact> page = contactService.getContacts(pageRequest.of());
        // then
      	// 이것은 findAll이 한번만 실행되는지 확인하는 부분이다.
	      verify(contactRepository, atLeastOnce()).findAll(pageRequest.of());
        assertThat(page.getContent().get(0).getTitle(), equalTo("a"));
        assertThat(page.getContent().get(1).getContent(), equalTo("bbb"));
        assertThat(page.getTotalElements(), equalTo(2L));
    }
}
```

