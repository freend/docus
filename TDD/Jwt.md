## JWT Test

#### 개요

JWT를 이용한 controller test에는 token을 만들어서 넣어 줘서 테스트를 해야 한다. 안그러면 오류가 열심히 발생해 준다. 그래서 인터넷을 열심히 찾아서 jwt생성에서 테스트까지 진행해 보도록 하겠다.

#### 생성

```java
private String createToken(Date date) throws UnsupportedEncodingException {
  String[] roles = new String[1];
  roles[0] = "ROLE_USER";
  return Jwts.builder()
    .setSubject("a@test.com").setExpiration(date).claim("roles", roles)
    .signWith(SignatureAlgorithm.HS256, "secret".getBytes("UTF-8")).compact();
}
/*hours는 현재를 기준으로 -1은 한시간 전 1은 한시간 후를 의미한다.*/
private Date createExpirationTime(int hour) {
  Calendar calendar = Calendar.getInstance();
  calendar.setTime(new Date());
  calendar.add(Calendar.HOUR_OF_DAY, hour);
  return calendar.getTime();
}
```

위와 같이 생성하면 USER의 ROLE을 가진 a@test.com 유저를 생성하고 키로 secret를 가지게 된다. 로그인 검증을 해보자

#### 테스트 코드

```java
@Test
public void 로그인을_한다() throws Exception {
    // given
    given(memberService.logInMember(any(MemberDto.class)))
    .willReturn(createToken(createExpirationTime(1)));
    // when
    MvcResult result = mockMvc.perform(put("/signin")
    .contentType(MediaType.APPLICATION_JSON)
    .characterEncoding("UTF-8")
    .content(
    new Gson().toJson(MemberDto.builder().mail("a@test.com").password("pass")))
    )
    .andExpect(status().isOk())
    .andDo(print())
    .andReturn();
    // then
    String content = result.getResponse().getContentAsString();
    Jwts.parser().setSigningKey("secret".getBytes("UTF-8")).parseClaimsJws(content);
  	AuthorizationDto dto = StringUtils.validateToken(content); //이건 개인적으로 만든 token유틸이다.
		assertThat(dto.getSub(), equalTo("a@test.com")); //아이디가 동일한지 확인한다.
}
```

이렇게 하면 jwt검증에 오류가 있으면 parseClaimsJws에서 오류를 발생시킨다.

```
키값이 다른 경우
io.jsonwebtoken.SignatureException: JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.
유효시간 보다 짧은 경우
io.jsonwebtoken.ExpiredJwtException: JWT expired at 2020-08-09T16:01:22Z. Current time: 2020-08-09T17:01:23Z, a difference of 3601290 milliseconds.  Allowed clock skew: 0 milliseconds.
```

그 외 UnsupportedJwtException, MalformedJwtException 등이 있다고 한다 (참고사이트2)

#### 문제점

테스트를 하다 보니 다음과 같은 오류를 발견하게 되었다.

```
io.jsonwebtoken.ExpiredJwtException: JWT expired at 2020-08-09T16:35:44Z. Current time: 2020-08-09T16:35:45Z, a difference of 1008 milliseconds.  Allowed clock skew: 0 milliseconds.
```

이건 테스트시 유효시간이 현재시간보다 1008 밀리초 빨러서 발생한 애러다.

#### 참고사이트

참고사이트 1 : [이곳을 참고했어요](https://medium.com/@OutOfBedlam/jwt-%EC%9E%90%EB%B0%94-%EA%B0%80%EC%9D%B4%EB%93%9C-53ccd7b2ba10)
참고사이트 2 : [jwt 오류 메세지](https://alwayspr.tistory.com/8)