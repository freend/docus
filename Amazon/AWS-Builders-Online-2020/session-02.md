## 모바일 앱의 성공방정식 - Amplify로 극대화하기

발표자 : 정창호, AWS 솔루션즈 아키텍트

### 주요내용

#### AWS Amplify 소개

- [Doc](https://docs.amplify.aws/)

#### Amplify 설치

```shell
# amplify cli 설치
$ npm install -g @aws-amplify/cli
# IDE등에서 프로젝트 생성
# 프로젝트 초기화
$ amplify init
# 패키지 설치
$ npm install aws-amplify aws-amplify-react-native
# 파일에서 클라우드 백엔드와 동기화
import Amplify from 'aws-amplify'
import config from './aws-exports'
Amplify.configure(config)
```

#### Key Use Cases and Features

##### 인증, 인가 - Amazon Cognito

사용자 풀 (인증)

가입, 로그인, 이메일 혹은 전화번호 확인, 패스워드 분실 / 리셋, 멀티팩터 인증

자격증명 풀(인가)

aws 리소스 액세스 위한 임시 자격 증명

외부 자격 공급자에 인증 위임

인증되지 않은 사용자 위한 게스트 액세스 지원

```shell
# Amplify Auth 추가하기
$ amplify add auth
# Configuration 설정
Do you want to use the default authentication and security configuration? Default configuration
How do you want users to be able to sign in? Email
Do you want to configure advanced settings? No, I am done
Successfully added resource package-name locally
# 해당 서비스 올리기
$ amplify push
```

작동될 화면에 다음의 내용을 추가한다

```javascript
import {withAuthenticator} from 'aws-amplify-react-native'
export default withAuthenticator(App)
```

이렇게 하면 Coginto에서 기본 지원하는 회원가입 / 로그인 화면이 생성된다.

##### API - AWS AppSync

```shell
$ amplify add api
? Please select from one of the below mentioned services:
> GraphQL
  REST
```

- GraphQL 동작방식 - 앱관점

  스키마 정의 후 앱에서 쿼리, 쿼리한 내용만 리턴, 쿼리의 변경에 의해 리턴되는 데이터가 달라짐.

- GraphQL Operations
  select에 해당하는 Queries, insert, delete, update에 해당하는 mutation, 실시간 데이터를 정기구독하는 subscription

- GraphQL 구성요소
  스키마에서 질의하면 해석기에서 해석하여 (이때 해석기가 그래프 처럼 얽혀있어서 GraphQL이라 한다.) 데이타 소스에서 가져온다

- AWS AppSync

  - AWS의 서비스를 데이타 소스로 사용
  - 관리형 GraphQL 서비스
  - data sync, real-time, and offline 업데이트
  - IAM, Amazon Cognito, OIDC, API Keys를 통한 접근제어

##### Amplify API 추가하기

```shell
# 이렇게 @model을 붙이면 query, operation, dynamo db, 해석기까지 만들어줌
type Todo @model {
	id: ID!
	name: String!
	description: String
}
```

```javascript
import { API, graphqlOperation } from 'aws-amplify'
import { createTodo } from './src/graphql/mutations'
import { listTodo } from './src/graphql/queries'

async function fetchTodos() {
  try {
    const todoData = await API.graphql(graphqlOperation(listTodos))
    const todos = todoData.data.listTodos.items
    setTodos(todos)
  } catch (err) {
    console.log('error fetching todos')
  }
}

async function addTodos() {
  try {
    const todo = {...formState}
    setTodos([...todos, todo])
    setFormState(initialState)
    await API.graphql(graphqlOperation(createTodo, {input: todo}))
  } catch (err) { console.log('error creating todo:', err)}
}
```

##### Preditions

- Amazon Polly : TTS (text to speech)
- Amazon Transcribe : STT (speech to text)
- Amazon Rekognition : 이미지 및 비디오 분석 자동화
- Amazon Tetract : 문서에서 텍스트와 데이터를 추출
- Amazon Comperhend : 키워드, 감정, 구문

```shell
# Amplify Prediction 추가하기
$ amplify add predictions
? Please select from one of the categories below
> Identify
  Convert
  Interpret
  Infer
  Learn More
? Please select from of the categories below Identify
? What would you like to identify?
  Identify Text
  Identify Entities
> Identify Labels
```

```javascript
Predictions.convert({
  translateText: {
    source: {
      text: textToTranslate,
      language: "es", // defaults configured on aws-exports.js
    },
    targetLanguage: "en"
  }
})
.then(result => console.log({result}))
.catch(err => console.log({err}));
```

##### Analytics & notification

- Amazon Pinpoint : 사용자 관리, 잠재 고객 관리, 타겟팅, 채널, 캠페인 인사이트

```shell
$ amplify add analytics
```

```javascript
Analytics.record({
	name: 'albumVisit',
	attributes: { genre: '', atrist: ''}
});
```

- Amazon notification

```shell
$ amplify add notificaton
? Choose the push notification channel to enable.
  APNS
> FCM
  Email
  SMS
```