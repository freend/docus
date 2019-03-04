## Mutation

### 1. Define

- Mutation은 database에 insert, update하는 행동을 의미한다.


### 2. 설정

- Schema에 mutation을 설정한다.
  우선은 데이터를 추가하는 부분으로 addAuthor로 선언한다.

  ```javascript
  const typeDefs = `
      # The "Mutation" type is insert, update
      type Mutation {
          addAuthor(id: Int, name: String, age: Int, book: [String])
      }
  `;
  ```

- Schema에서 데이터를 database에 보냈으므로 결과를 반환받는 부분을 만들어야 한다. 이 부분은 resolver에서 담당한다

