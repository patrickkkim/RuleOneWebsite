# RuleOneWebsite
Rule one website for value investing.

https://ruleonewebsite-da0fc5ae463d.herokuapp.com/

### 회원가입
curl --header "Content-Type: application/json" --request POST --data '{"username":"honggilddong","email":"pat@naver.com", "encryptedPassword":"password123123@"}' https://ruleonewebsite-da0fc5ae463d.herokuapp.com/user

### 로그인
curl --header "Content-Type: application/json" --request POST --data '{"username":"honggilddong", "password":"password123123@"}' https://ruleonewebsite-da0fc5ae463d.herokuapp.com/user/login

### 주식 정보
curl --header "Authorization: Bearer ${JWT}" --request GET https://ruleonewebsite-da0fc5ae463d.herokuapp.com/stock-info/big-five/annual/META

### 주식 분석
curl --header "Content-Type: application/json" --header "Authorization: Bearer ${JWT}" --data '${DATA}' --request GET https://ruleonewebsite-da0fc5ae463d.herokuapp.com/growth
