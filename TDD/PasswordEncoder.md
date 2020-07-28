## PasswordEncoder를 Autowired 없이 사용하기

테스트를 할 때 독립성을 보장하기 위해 전체 테스트인 SpringBootTest 어노테이션을 사용하지 않기로 했다. (머 물론 그거 말고 전체테스트 시 Bean 생성등을 하지않아서 전체 테스트 시간이 줄어든다는 장점도 있지만) 이런 경우 Autowired 어노테이션을 사용하게 되면 **UnsatisfiedDependencyException** 가 발생함을 알 수 있다. 그럼 이런 경우 어떻게 하면 될까?

테스트를 할때 PasswordEncoder를 Mocking 할 필요없이 다음과 같이 하면 된다. 여기서는 전체 서비스가 아닌 로그인만 구현하였다.

```java
package com.freend.member;

import com.freend.configuration.security.JwtTokenProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringJUnit4ClassRunner.class)
public class MemberServiceTest {
    MemberService memberService;
    @Mock
    MemberRepository memberRepository;
    PasswordEncoder passwordEncoder;
    @Mock
    JwtTokenProvider jwtTokenProvider;
  	MathUtils mathUtils;
    @Before
    public void setUp() {
      	//이렇게 PasswordEncoderFactories를 사용하면 된다.
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
      	//혹은 내가 만든 클래스를 사용하고 싶다면 아래와 같이 해서 아래 생성자에 넣어주면 된다.
	      mathUtils = new MathUtils();
        this.memberService = new MemberService(memberRepository, passwordEncoder, jwtTokenProvider);
    }

    @Test
    public void 로그인_하기() throws Exception {
        // given
        List<String> role = new ArrayList<>();
        role.add("ROLE_USER");
        given(memberRepository.findByMail("freending@gmail.com")).willReturn(
                java.util.Optional.ofNullable(
                        Member.builder().mail("freending@gmail.com").password(passwordEncoder.encode("pass"))
                                .roles(role).build()
                )
        );

        given(jwtTokenProvider.createToken("freending@gmail.com", role))
        .willReturn("freending@gmail.com");
        // when
        String result = this.memberService.logInMember(MemberDto.builder().mail("freending@gmail.com").password("pass").build());

        // then
        assertThat(result, equalTo("freending@gmail.com"));
    }
}
```

