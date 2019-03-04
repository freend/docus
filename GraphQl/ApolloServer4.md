## Mongo DB

### 1. 설치하기

- 우선은 편한 접근(내가 본 튜토리얼 참조)로 인해 mongodb로 작업을 진행하기로 했다.
  설치 버전은 ubuntu 16.04이다.

  ```shell
  apt-get install mongodb-clients mongodb-server
  ```

- 서버와 클라이언트가 정상적으로 설치되었는지 확인한다.

  ```shell
  mongo -version
  MongoDB shell version: 2.6.10
  mongod -version
  db version v2.6.10
  2019-01-14T16:52:07.840+0900 git version: nogitversion
  2019-01-14T16:52:07.840+0900 OpenSSL version: OpenSSL 1.0.2g  1 Mar 2016
  ```

- 외부에서 mongo db에 접속하기 위해서는 포트번호 27017를 열어주어야 한다. 이건 상황마다 다르므로 패쓰

- mongoDB 설정파일(/etc/mongod.conf)을 바꾸어 localhost 이외에서도 접근할 수 있도록 하자.
  해당 파일을 nano나 vim등을 이용해서 열어주게 되면 다음과 같은 부분을 찾을 수 있다.

  ```shell
  bind_ip = 127.0.0.1
  ```

  저 부분이 바로 아이피 허용 부분이다. 해당 부분을 주석처리 하거나 bind_ip = 0.0.0.0로 변경해 주고 restart하자

  ```shell
  sudo service mongodb restart
  ```

- 이제 mongodb용 editor를 설치해보자. [Robo3T download](https://robomongo.org/download)

- mongodb에 대한 간단한 강의는 다음 링크를 이용해서 공부하라 [VELOPERT.LOG](https://velopert.com/436)

### 2. Database, Collection 생성하기

#### DB 구조

MongoDB는 Database -> Collection -> Document(Data)의 순으로 계층 데이터가 생성된다.

#### Database생성

- use DATABASE_NAME으로 생성한다. 생성되며 해당 database로 이동합니다. 여기서는 Authros로 생성합니다.

  ```shell
  > use Authros
  switched to db Authors
  ```

#### Collection 생성

- db.createCollection(name, option)

  ```shell
  > db.createCollection("Author")
  { "ok" : 1 }
  ```

### 3. mongoose 설치하기

- 이제 mongodb와 server와의 연결을 위한 middle ware인 mongoose를 설치한다.

  ```shell
  npm install mongoose --save
  ```

- server.js에서 mongoose를 import 해주자

  ```javascript
  import mongoose from 'mongoose';
  ```

- mongoose에 connect를 해주자.

  ```javascript
  mongoose.connect('mongodb://urlAddress/database');
  const connection = mongoose.connection;
  connection.once('open', () => {
      console.log('data base connection success');
  });
  ```

  이렇게 한 후 서버를 구동시켰을 때 'data base connection success' 메세지가 나오면 정상적으로 연결된 것이다.

### 4. Model 생성하기

Model Folder와 Author.js파일을 생성한다. 내용은 다음과 같다.

```javascript
// import mongoose
import mongoose from 'mongoose';

const schema = mongoose.Schema;
const authorSchema = new Schema({
    //id, name, age, books
    id: Number,
    name: String,
    age: Number,
    books: [String]
});

const model = mongoose.model('Author', authorSchema);
```

어디서 많이 본 것 같다. 바로 schema, resolvers에 있는 부분과 동일하게 데이터 모델 형을 가져왔다.

#### Next to

[[Mutation]](ApolloServer5.md)