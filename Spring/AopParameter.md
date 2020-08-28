### [AOP에 걸린 Method의 Parameter 이름 가져오기](https://alwayspr.tistory.com/34)

AlwaysPr 2018.06.19 21:01

먼저, AOP가 뭔지에 대해 알아보자.

Aspect-Oriented Programming 이란 프로그램 구조에 대해 또 다른 사고방식을 제공함으로써 Object-Oriented Programming을 보완한다. OOP 모듈성의 핵심 단위는 클래스인 반면, AOP는 모듈화 단위가 관점(Aspect)이다. @Transactional이 대표적인 예이다. 공통의 기능(트랜잭션)을 분리 및 모듈화하여 여러 코드(insertUser, selectMoney)에 쉽게 적용할 수 있게 해준다.



특정 계정의 ID와 token을 통해 유효한지 확인하는 코드가 프로젝트 전반에 산재해 있다고 가정을 해보자. 그리고 이를 비즈니스 코드와는 크게 관계없는 하나의 공통된 관점으로 판단을 하고, AOP를 적용시켜보자. 자세한 코드는 [github](https://github.com/viviennes7/blog-aop-parameter-name)를 참고하자.

(제목에 대한 답만 알고 싶으면 '3. AOP에서 메소드의 파라미터 이름을 알아와서 매핑 시킬 순 없을까?'만 보면 된다.)

## 1. 일단 동작하는 AOP 만들기

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccountValidator {
}
@Aspect
@Component
public class ValidatorAspect {
    @Pointcut("@annotation(com.ms.blogaopparametername.aop.AccountValidator)")
    public void accountValidator() {}

    @Before("accountValidator()")
    public void validateAccount() {
        System.out.println("AOP");
    }
}
@SpringBootApplication
public class BlogAopParameterNameApplication {
    private final BusinessService businessService;

    public BlogAopParameterNameApplication(BusinessService businessService) {
        this.businessService = businessService;
    }

    public static void main(String[] args) {
        SpringApplication.run(BlogAopParameterNameApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return (run) -> this.businessService.logic();
    }
}
@Service
public class BusinessService {
    @AccountValidator
    public void logic() {
        System.out.println("Hello World");
    }
}
```

@AccountValidator를 작성한 메소드에 AOP를 걸었고, 순차적으로 'AOP', 'Hello World'가 출력되게 된다.



![img](https://t1.daumcdn.net/cfile/tistory/99E738365B28E9D129)

IDE를 통해서도 AOP가 제대로 걸려있음을 확인할 수 있다.

## 2. 계정의 유효성을 판단하려면 ID와 Token 값을 알아야 한다. logic()에 파라미터로 추가해 보자.

```java
@Service
public class BusinessService {
    @AccountValidator
    public void logic(Long id, String token) {
        System.out.println("Hello World");
    }
}
```

유효성을 AOP를 통해 판단하려면, Aspect에서 id와 token 값을 가져와야한다. 간단하게 Pointcut의 표현식 중 하나인 args를 사용해보자.

```java
@Aspect
@Component
public class ValidatorAspect {
    @Pointcut("@annotation(com.ms.blogaopparametername.aop.AccountValidator)")
    public void accountValidator() {}

    @Before("accountValidator() && args(id,token,..)")
    public void validateAccount(Long id, String token) {
        System.out.println(id + " : " + token);
    }
}
```

다음처럼 파라미터의 값을 정상적으로 출력한다. '1 : token'



AOP는 로직들의 공통된 관점이라 했다. 이 말은 다른 메소드들에서도 잘 동작해야 한다는 말인데... 다음 코드를 보자.

![img](https://t1.daumcdn.net/cfile/tistory/99C97F435B28EE642B)

logic2()에서는 사용자의 이름이 앞에 추가되었다. IDE를 통해 보니 AOP가 걸려있지 않다.

또 logic3()에서는 첫 번째 파라미터가 businessId이지만, AOP는 걸려있다.

그렇다. args는 메소드 파라미터의 이름에 따라 정해지는 것이 아니라, 순서와 Type만을 통해 판단한다. 

공통의 관점은 무슨 메소드 만들 때마다 Aspect를 만들어줘야 될 판이다. args는 지워 버리자.

## 3. AOP에서 메소드의 파라미터 이름을 알아와서 매핑 시킬 순 없을까?

JoinPoint를 활용해보자.

```java
@Aspect
@Component
public class ValidatorAspect {
    @Pointcut("@annotation(com.ms.blogaopparametername.aop.AccountValidator)")
    public void accountValidator() {}

    @Before("accountValidator()")
    public void validateAccount(JoinPoint joinPoint) {
        Long id = null;
        String token = null;
        String parameterName;
        Object[] parameterValues = joinPoint.getArgs();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        for (int i = 0; i < method.getParameters().length; i++) {
            parameterName = method.getParameters()[i].getName();
            if (parameterName.equals("id"))
                id = (Long) parameterValues[i];
            if (parameterName.equals("token"))
                token = (String) parameterValues[i];
        }

        System.out.println(id + " : " + token);
    }
}
```

JoinPoint를 이용해서 call된 Method 객체를 얻는다. 그리고 이 객체를 통해서 파라미터의 name과 value를 얻는다.

뭔가 될 거 같아서 기분이 좋다.  



그러나 출력창에는 'null : null'이 반환된다. 다시 기분이 안 좋아졌다.

디버깅을 해보니 method.getParameters()[i].getName()을 통해 얻은 파라미터 명은 arg0, arg1, arg2이다. 



java.lang.reflect.Parameter 객체는 자바 8에서 추가되었고, 메소드 파라미터의 정보를 가져올 수 있는 녀석이다. 그러나 이러한 정보를 가져오기 위해서는 컴파일(javac) 시점에 VM option에 -parameters를 추가해주어야 한다. 

다음처럼 말이다. ' **javac -parameters** ' Maven , Gradle, IDE에도 옵션을 주고 싶다면 [여기](https://www.concretepage.com/java/jdk-8/java-8-reflection-access-to-parameter-names-of-method-and-constructor-with-maven-gradle-and-eclipse-using-parameters-compiler-argument)를 참고하자.

해당 옵션을 적용하고, Project Rebuild 후 실행시켜보자.



'1 : token '이 정상적으로 출력된다.

## 4. Id와 token 값을 가지고 있는 객체가 파라미터라면?

Stream을 사용해서 좀 더 우아하게 만들어보자.

```java
@Aspect
@Component
public class ValidatorAspect {
    @Pointcut("@annotation(com.ms.blogaopparametername.aop.AccountValidator)")
    public void accountValidator() {}
    @Before("accountValidator()")
    public void validateAccount(JoinPoint joinPoint) {
        User user = Arrays.stream(joinPoint.getArgs())
                .filter(User.class::isInstance)
                .map(User.class::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User를 찾을 수 없습니다."));

        System.out.println(user.getId() + " : " + user.getToken());
    }
}
```

물론 이 방식도 User란 객체가 두 개 이상이 되면 문제가 된다. 그럼 다시 위의 Parameter 명을 통해 해결해야 될 뜻하다.

## 결론

사실, AOP보다는 Reflection에 관한 주제이지만, 필자와 비슷한 고민(?)을 하는 사람이 있을까 싶어서 해당 제목으로 올리게 되었다.



파라미터 명을 통해 가져오는 것보다 더 좋은 방법이 있을 것 같다. 

AOP를 이렇게 쓰는게 옳은 방법일까? 사실, 파라미터 명으로 가져오게 되면 파라미터 명에 강하게 의존하게 된다. 나는 계정 ID의 파라미터 명을 id로 했지만 또 다른 누군가는 userId로 할지도 모르니 말이다.

추후 이에 대한 물음에 답이 떠오르면 미래의 내가(?) 마저 작성할 것 같다