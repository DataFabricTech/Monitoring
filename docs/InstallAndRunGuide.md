# Try Monitoring in Docker
# Linux
## 필요 설치 항목
- Docker
- Postgresql
- Monitoring

## Docker
도커는 애플리케이션의 개발, 전송 및 실행하기 위한 오픈 소스 플랫폼입니다. 이를 통하여 소프트웨어를 빠르게 제공할 수 있습니다.  
가지고 있는 도커 버전을 확인하려면, 다음 명령을 사용하세요.
```txt
docker --version
```
만약 도커의 설치가 필요하다면, [Get Docker](https://docs.docker.com/get-docker/)를 접속해주시기 바랍니다.

## PostgreSQL
PostgreSQL은 확장 가능성 및 표준 준수를 가종하는 객체-관계형 데이터베이스 관리 시스템입니다.  
만약 PostgreSQL의 설치가 필요하다면, [PostgreSQL DockerHub](https://hub.docker.com/_/postgres)를 이용해 주시기 바랍니다.

## Monitoring

도커를 통하여 Monitoring을 설치 및 실행 할 수 있습니다.
```cli
docker run -it \
    -e COLLECT_EXPRESSION="0 0/5 * * * *" \
    -e CONNECT_PAGING_PAGE=0 \
    -e CONNECT_PAGING_SIZE=5 \
    -e EVENT_PAGING_PAGE=0 \
    -e EVENT_PAGING_SIZE=5 \
    -e HISTORY_PAGING_PAGE=0 \
    -e HISTORY_PAGING_SIZE=5 \
    -e TARGET_SERVER_ID=targetserverid \
    -e TARGET_SERVER_PW=targetserverpw \
    -e TARGET_SERVER_URL=targetserverurl \
    -e REGISTRATION_PAGING_PAGE=0 \
    -e REGISTRATION_SIZE=5 \
    -e SAVE_EXPRESSION="0 0/5 * * * *" \
    -e DATASOURCE_URL=mydatasourceurl \
    -e DATASOURCE_USERNAME=mydatasourceusername \
    -e DATASOURCE_PASSWORD=mydatasourcepassword \
    -e DATASOURCE_CLASS_NAME=mydatasourceclassname \
    -e JPA_PROPERTIES_HIBERNATE_DIALECT=mydatasourcedialect \
    repo.iris.tools/datafabric/monitoring:$tag bash
```

## Environment
- TARGET_SERVER_ID
  - 모니터링하고자 하는 서버의 아이디
- TARGET_SERVER_PW
  - 모니터링하고자 하는 서버의 패스워드
- TARGET_SERVER_URL
  - 모니터링 하고자 하는 서버의 URL
- DATASOURCE_URL
  - 데이터를 저장할 저장소의 URL
- DATASOURCE_USERNAME
  - 저장소의 유저네임
- DATASOURCE_PASSWORD
  - 저장소의 패스워드
- JPA_PROPERTIES_HIBERNATE_DIALECT
  - 저장소의 DIALECT
- DATASOURCE_CLASS_NAME
  - 저장소의 ClassName

### Advanced Environment
- COLLECT_EXPRESSION
  - 수집 주기
  - 기본 값: 0 0/5 * * * *
- SAVE_EXPRESSION
  - 저장 주기
  - 기본 값: 0 0/30 * * * *
- CONNECT_PAGING_PAGE
  - '연결정보 응답 시간' 페이지
  - 기본 값: 0
- CONNECT_PAGING_SIZE
  - '연결정보 응답 시간' 페이지당 사이즈
  - 기본 값: 5
- EVENT_PAGING_PAGE
  - '이벤트' 페이지
  - 기본 값: 0
- EVENT_PAGING_SIZE
  - '이벤트' 페이지당 사이즈의 설
  - 기본 값: 5
- HISTORY_PAGING_PAGE
  - '히스토리' 페이지
  - 기본 값: 0
- HISTORY_PAGING_SIZE
  - '히스토리' 페이지당 사이즈
  - 기본 값: 5
- REGISTRATION_PAGING_PAGE
  - '데이터 모델' 페이지
  - 기본 값: 0
- REGISTRATION_SIZE
  - '데이터 모델' 페이지당 사이즈
  - 기본 값: 5
