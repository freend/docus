## Controller Setting

### MVC Setting

- mvc를 설정해 준다.

```java
public class MemberControllerTest {
 		private MockMvc mockMvc; // 기본적으로 url설정을 하는 mockMvc이다.
	  @InjectMocks
    MemberController memberController; // 이건 안에 다른 mock을 넣을 수 있는 mock controller이다.
  	@Before
    public void setUp() {
      	//membercontroller를 mockMvc로 사용하겠다.
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }
  	@Test
  	public void 로그인을_한다() throw Exception {
      	// when
        MvcResult result = mockMvc.perform(put("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(
											// 여긴 json 형식으로 만들어도 되는데 build한 후 json으로 passing했다.
                      new Gson().toJson(
                        MemberDto.builder().mail("a@test.com").password("pass")
                      )
                    )
                )
                .andExpect(status().isOk()) // 200으로 반환하는가?
                .andDo(print()) // 결과를 출력해 달라.
                .andReturn(); // 결과를 MvcResult로 넣는다.
    }
}
```

