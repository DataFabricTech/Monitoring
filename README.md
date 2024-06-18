# Monitoring

## Monitoring이란?
OpenMetadata의 Service/Table 등의 상태를 모니터링을 하기 위한 서비스로, Databases/Storage의 상태를 조회할 수 있습니다.

## Monitoring의 목표 주요 기능
- 연결정보 상태
  - Get Connected/Disconnected
  - Save Connected/Disconnected
- 데이터 모델
  - Get Created/Uncreated Model
  - Save Created/Uncreated Model
- 최근 등록/수정된 연결 정보
  - Get Recent Created/Updated
  - Save Recent Created/Updated
- 이벤트 히스토리
  - Get Event
  - Save Event
- 연결 Test
  - 연결 시간 측정
  - Get AverageResponseTimes
  - Save ResponseTimes
- Scheduler
  - 주기적으로 Monitoring Data 수집
- OpenMetadata의 Notification으로 들어오는 정보 습득
  - DatabaseServices
    - Rename
    - Delete
      - SoftDelete
      - HardDelete
    - TestConnection
    - Edit Connection
  - Database
    - Rename
    - Delete
      - SoftDelete
  - DatabaseSchema
    - Rename
    - Delete
      - SoftDelete
  - Table
    - Rename
    - Delete
      - SoftDelete
  - Storage
- SetUp
  - Notification 생성(databaseService, database, databaseSchema, table, storageService)
    - 생성 Body
      - ```json 
        {
            "name": "resourceCheck",
            "resources": [
              "databaseService"
            ],
            "input": {},
            "alertType": "Notification",
            "provider": "user",
            "destinations": [
              {
              "type": "Generic",
              "config": {
                "endpoint": "http://192.168.106.104:5000/v1/monitoring/databaseService"
              },
              "category": "External"
              }
            ]
        }
  - Token 획득용 Bot 생성
  - 예거 dependency 추가
    - API Gateway에서 예거의 Header를 갖고 온다.
    - global exception 방식
    - error 발생 위치의 예거 
    - response 위치의 예거

## 동작 설명


