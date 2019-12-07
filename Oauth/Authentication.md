

- 인증(Authetication) : 자신이 누구라고 주장하는 사람을 확인하는 절차이다.

- 권한(Authorization) : 원하는 정보를 얻도록 허용하는 과정

사용자의 정보와 자산을 보호하기 위해 사용됨.

### 서버 기반 인증의 문제점

- 세션
  유저가 인증을 할 때, 서버는 이 기록을 서버에 저장을 해야합니다. 이를 **세션** 이라고 부릅니다. 대부분의 경우엔 메모리에 이를 저장하는데, 로그인 중인 유저의 수가 늘어난다면 어떻게될까요? 서버의 램이 과부화가 되겠지요? 이를 피하기 위해서, 세션을 데이터베이스에 시스템에 저장하는 방식도 있지만, 이 또한 유저의 수가 많으면 데이터베이스의 성능에 무리를 줄 수 있습니다.
- 확장성
  세션을 사용하면 서버를 확장하는것이 어려워집니다. 여기서 서버의 확장이란, 단순히 서버의 사양을 업그레이드 하는것이 아니라, 더 많은 트래픽을 감당하기 위하여 여러개의 프로세스를 돌리거나, 여러대의 서버 컴퓨터를 추가 하는것을 의미합니다. 세션을 사용하면서 분산된 시스템을 설계하는건 불가능한것은 아니지만 과정이 매우 복잡해집니다.
- CORS (Cross-Origin Resource Sharing)
  웹 어플리케이션에서 세션을 관리 할 때 자주 사용되는 쿠키는 단일 도메인 및 서브 도메인에서만 작동하도록 설계되어있습니다. 따라서 쿠키를 여러 도메인에서 관리하는것은 좀 번거롭습니다.

### 권한획득 방식

- Basic Auth : 사용자의 아이디와 비밀번호를 헤더에 넣어서 base64 Encoding등을 통해 쉽게 디코딩이 가능하다.
  [참조](https://hamait.tistory.com/416)
- Digest Auth : Basic Auth의 취약점을 개선하기 위한 인증기법이다.
  nonce(서버), cnonce(클라이언트)값을 통해 Replay Attack을 방지한다. password를 MD5로 인코딩한다.
  [참조](https://ssup2.github.io/theory_analysis/HTTP_Digest_%EC%9D%B8%EC%A6%9D/)
- Bearer Token : token type bearer로 발급된 토큰 이것을 통상적으로 access-token이라고 칭한다.
  그리고 이것을 표준 사용자 인증 프로토콜화 한 것이 Oauth이다.
  The Bearer authentication scheme was originally created as part of [OAuth 2.0](https://swagger.io/docs/specification/authentication/oauth2/) in [RFC 6750](https://tools.ietf.org/html/rfc6750), but is sometimes also used on its own. Similarly to [Basic authentication](https://swagger.io/docs/specification/authentication/basic-authentication/), Bearer authentication should only be used over HTTPS (SSL).

### Bearer Token의 token 획득방법

전제조건 : 인증서버에 클라이언트 아이디와 클라이언트 시크릿이 등록되 있다는 전제조건이 있음.

#### 1. 권한 코드 방식 (Authorization Code flow)

- 제일 많이 사용되는 방식으로 구글 등에서 사용하는 방식입니다.
- 이건 추가로 응답을 받는 주소도 등록해야 함.
- 서비스의 인증요청 -> 응답받는 주소로 코드 발송 -> 코드를 다시 넣어서 인증요청 -> 토큰발급

#### 2. 자원소유자 비밀번호 방식

- 위의 과정에서 코드발급 과정이 빠진 방식

#### 특징

- 인증서버에 사용자의 정보가 저장되어 있어야만 한다.
- 리플레시 토근을 발급한다.

#### 3. 클라이언트 인증 플로우

- 클라이언트 아이디와 시크릿 만으로 인증코드 발급
- 리플레시 토근이 존재하지 않는다.
- 인증서버에 사용자의 정보가 없어도 된다.

### Oauth란?

  [Oauth 위키](https://ko.wikipedia.org/wiki/OAuth)

  - Oauth 1.0

    - Consumer 가 id/pw를 가지지 않아도 됨
    - 권한 제어 가능
    - 사용자가 인증서비스에서 권한 취소도 가능
    - 패스워드 변경 시에도 인증 토큰과는 무관(계속 유효한 토큰을 가짐)

  - Oauth 2.0

    - 간단해졌다. 

    - 여러가지 인증 방법 지원 

    - 대형 서비스로의 확장성 지원 

    - 불려지는 용어 변경

  - [Oauth란](https://minwan1.github.io/2018/02/24/2018-02-24-OAuth/)

### JWT 란?

- 이렇게 하는 방식에는 단점이 존재한다. Access Token만 교환하기 때문에 그 다시 토큰을 가지고 인증 정보를 조회하기 위해 OAuth2 서버로 다시 요청하여 인증된 정보를 얻어오는 오버헤드가 생기게 된다.

- 이것을 해결해주기 위해 나온 형태가 JWT이다.

- 외부로 알려져도 상관없는 header와 payload로 구성되어 있고 그것을 header에 포함된 방식의 해싱으로 무결성을 인정하기 위한 signature부분으로 구성되어 있다.

- signature에 사용되는 key는 리소스 서버 기동시 authentication 서버에 요청을 해서 받아간다.

- 아니면 클라이언트와 서버에 RSA (private, public) 키값을 기술해 줄 수도 있다.

  > 확인방법
  >
  > 인증서버 기동 -> 리소스서버 기동 -> token요청 -> token 확인 : 정상작동 확인
  > 인증서버 재 기동 -> token 요청 -> token 확인 : 아래의 오류 메세지 확인
  >
  > ```json
  > {
  >  "error": "invalid_token",
  >  "error_description": "Cannot convert access token to JSON"
  > }
  > ```
  >
  > Key 값은 OAuth2 서버가 시작할 때 랜덤으로 결정된다.

- JWT 참조사이트
  [참조1](https://blog.outsider.ne.kr/1160)
  [참조2](https://velopert.com/2389)

### token 발급시 필요 정보

token을 발급받을 때는 다음의 값들을 필요로 하며 이것은 인증서비스 신청시 미리 설정한다.

- AuthorizedGrantType : 인증형식

  |                    |                                                              |
  | ------------------ | ------------------------------------------------------------ |
  | authorization_code | 코드 인증                                                    |
  | password           | 클라이언트 아이디, 시크릿, 유지아이디, 유저비밀번호 인증     |
  | client_credentials | 클라이언트 아이디, 시크릿 인증                               |
  | implicit           | 클라이언트 전용으로 클라이언트 아이디만 보고 묵시적으로 인증해줌 |
  | refresh_token      | 리플레시 토큰으로 억세스 토큰 재발급                         |

- authorities : 클라이언트에게 부여된 권한으로 접근가능 영역 확인

- scope : 접근 가능한 api나 필터링된 데이터

> authorities, scope 둘다 접근권한을 가지고 있다
> authorities는 사용자 기준의 분류를 기준 : @PreAuthorize("#oauth2.hasRole('ROLE_ADMIN')")
> scope는 사용서비스를 기준 : @PreAuthorize("#oauth2.hasScope('member.info.public')")
>
> [ROLE 참조](https://syaku.tistory.com/278)
> [ROLE 참조 2](https://books.google.co.kr/books?id=TOBBDwAAQBAJ&pg=PA370&lpg=PA370&dq=.authorities&source=bl&ots=guzyelTADz&sig=ACfU3U2_co7oIiZLvQ8DJnTCPf5yIHKW-Q&hl=ko&sa=X&ved=2ahUKEwiPt4Sh2pThAhVF6LwKHYemD-4Q6AEwBXoECAkQAQ#v=onepage&q=.authorities&f=false)
>
> [SCOPE 참조](https://brunch.co.kr/@sbcoba/15)
>
> 테스트 중 확인결과로 token발행시 scope은 필수값이며 authorities는 null로 많이 사용했다.

```json
{
  "scope": [
    "event.detail",
    "faq.read",
    "ticket.all",
    "user.detail"
  ],
  "exp": 1553270176,
  "authorities": [
    "ROLE_USER"
  ],
  "jti": "c6bd0073-377a-4cd2-8cbb-96c30236d8d1",
  "client_id": "foo"
}
```

위와같이 나오면 event.detail, faq.read 등을 넣을 수 있다.

### Oauth 공급업체

- [auth0](https://auth0.com/)

