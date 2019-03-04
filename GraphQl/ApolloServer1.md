## Apollo Server Setting

- npm init으로 node server 설정.
- Install express

#### 1. server.js 파일 생성

  ```
import callName from 'middleware-name'
  ```

- 위와 같이 해서 import express from 'express'; 후 실행하면 컴파일러 때문에 에러가 발생한다.
  그래서 에러를 없애기 위해 babel이라는 middle ware를 설치해서 사용합니다.

  ```
  npm install --save-dev babel-cli babel-preset-env
  ```

  - package.json에서 dependency를 할 때 dependencies, devDependencies의 차이를 알려주면 감사하겠습니다.

- 이것을 연동하기 위해서 .babelrc를 만들어 준 후 다음의 내용을 입력합니다.

  ```
  {
    "presets": ["env"]
  }
  ```

  이 부분에 대해서는 다음 [링크](https://jaeyeophan.github.io/2017/05/16/Everything-about-babel/)를 참조하시기 바랍니다.

- 그 후 babel을 이용해서 node를 실행하기 위해 package.json으로 이동해서 다음과 같이 변경한다.

  ```
   "scripts": {
      "test": "echo \"Error: no test specified\" && exit 1"
    }
  ```

  ```
   "scripts": {
      "test": "echo \"Error: no test specified\" && exit 1",
      "build": "babel-node server.js"
    }
  ```

  server.js에 express를 import하고 서버를 생성해 준다.

  ```
  import express from 'express'
  const server = express();
  
  server.listen(4000, () => {
      console.log('hello on port 4000');
  });
  ```

  pm2 에서 실행할 때는 다음의 명령어를 넣어주면 된다.

  ```
   pm2 start --name graphql npm -- run build
  ```

  이렇게 하면 npm으로 실행할 수 있다.

#### 2. Babel-Watch

- Babel-Watch는 서버의 내용이 변경되었을 때 자동으로 재실행 해주는 미들웨어이다.

  ```
  npm install --save babel-watch
  ```

- 설치가 완료된 후 package.json에서 build항목을 다음과 같이 변경한다.

  ```
  "build": "babel-node server.js" -> "build": "babel-watch server.js"
  ```

- 그 후 pm2 reload 후 내용 변경 후 저장하면 자동으로 restart가 됨을 알 수 있다.
  ![](Images/ApolloService/step01.png)

이제 node서버의 기본적인 세팅이 끝났다.

#### Next to

[GraphQl](ApolloServer2.md)