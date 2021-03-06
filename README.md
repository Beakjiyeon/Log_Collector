## Log Collector
분산된 서비스들의 로그를 효과적으로 추적하기 위한 로그 수집 시스템
  
<br/>


### 아키텍처
![image](https://user-images.githubusercontent.com/35768650/151692146-7357f015-db28-4078-bda7-11e74500c8ef.png)


### Details
1. 임의 서비스가 보내온 전송 로그를 수집하는 API 서버를 구현해야한다
    1. JSON Format
    2. 필수 로그 정보
        1. 추적 ID (TraceId)
        2. 서비스이름(혹은 TagID)
        3. 로그타입 ( INFO, ERROR ) - 두가지 타입만
        4. 비정형 Log Data Content (TEXT)
        5. 서버 로깅 시각
2. 해당 수집한 API 서버에서 분류 작업 및 로그를 저장하는 서비스를 구현한다.
    1. ERROR의 경우 별도의 알림 처리(알림은 담당자 메일로)를 진행한다.
    2. 추적ID를 기준으로 어떤 서비스들을 사용했는지 어떤 로그를 남겼는지를 알 수 있도록 DB에 저장한다. (날짜별 테이블)
3. 해당 분류되어 저장된 로그를 지정한 조건으로 조회할 수 있는 API 서버를 구현해야한다.
    1. 하루에 어떤서비스가 에러가 많이 났는지
    2. 서비스별 평균 걸린시간이 얼마인지
    3. 서비스별로 가장 오래걸린 시간 및 짧은 시간과의 간극이 어느정도인지
    4. 가장 많은 서비스를 사용하는게 어떤건지
    5. 특정 ID의 내용을 검색하는 기능
   
<br/>

### Goal
- 서비스(혹은 서비스 그룹), 로그 레벨, 로그 시간에 따른 정렬 가능
- 단어 검색 가능
- api를 통한 정보 제공
- 알람 기능(설정, 발송)
- 다양한 서비스들이 사용 가능한 확장성과 유연함
- 부하 분산을 위한 인프라 구성 간편화 대비 컨테이너 구성 (이론적인 학습만)
- 부하 완충용 미들웨어(MOM) 도입을 고려한 개발
  
<br/>

  
### 실행 모습
![image](https://user-images.githubusercontent.com/35768650/151692441-e1e8c9e1-b604-400b-a02b-16dfdfa8b8f1.png)
![image](https://user-images.githubusercontent.com/35768650/151692531-811bf469-f998-4c4a-a4b8-74926d455d82.png)
![image](https://user-images.githubusercontent.com/35768650/151692556-a65fabf3-80f8-4f3e-8703-854bf7228876.png)
  
<br/>
