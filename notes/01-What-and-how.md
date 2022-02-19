### 1. What and how notes?
- Refactor cheffy API: change db from postgresql to datomic
    - Architectural change doc
        - why was the change made?
    q. what is cheffy?
        - cheffy is an imaginary resturant created by jacek schae for his online course
- Swap database layer
- This course involves modifying an existing clojure backend for cheffy api.
- cheffy api current stack
  - reitit   - routing
  - postgres - db
  - heroku   - infra
- main focus: migrating from postgresql to datomic
- focus on datomic and datalog
    - we are going to look at:
        - data validation
        - tests
        - replace db layer
        - datomic
            - schema: how all of these are constructed and built
            - rules
            - transactions
            - transaction functions

### 2. How does reitit works and how is everything connected?
TODO
