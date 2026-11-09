# Data Source Monitoring Service

> ## 저장소 상태
>
> 본 저장소는 아래 과제의 데이터 저장소 모니터링 서비스(Java)입니다.
> 모니터링 기능은 이후 Open VDAP 데이터 카탈로그 솔루션에 통합되어
> 개발이 계속되었습니다.
>
> - 과제명: 분산된 데이터에 대한 논리적 데이터 통합과 복합분석을 지원하는 데이터 패브릭 기술개발
> - 과제번호: RS-2023-00222191
> - 개발 기간: 2024년 5월 ~ 2025년 4월

## 개요

데이터 카탈로그에 등록된 데이터 저장소의 상태를 주기적으로 수집하고 조회 API로 제공하는 서비스입니다.

- **연결 상태 수집** — 등록된 저장소에 주기적으로 접속을 시도하여 연결 가능 여부와 응답 시간을 기록합니다.
- **자원 사용량 수집** — CPU 점유 시간, 메모리 사용량, 디스크 사용량, 요청 처리율을 수집합니다.
- **슬로우 쿼리 수집** — 저장소별 느린 질의 정보를 수집하여 보관합니다.
- **변경 감지** — 저장소의 데이터베이스·테이블 구성 변화를 감지하여 이력으로 남깁니다.
- **수집 주기 제어** — 수집 작업의 등록·해제·조회를 API로 처리합니다.

수집 대상 저장소의 종류별로 상태 조회 질의를 분리하여, PostgreSQL · MySQL · MariaDB · Oracle을 지원합니다.

## 스펙

| 항목 | 내용 |
|---|---|
| 언어 | Java 21 |
| 프레임워크 | Spring Boot 3.4.2 |
| 빌드 도구 | Gradle 8.5 (Wrapper 포함, Kotlin DSL) |
| 패키징 | Jar |
| 영속성 | Spring Data JPA, Flyway(스키마 이행) |
| 지원 저장소 | PostgreSQL, MySQL, MariaDB, Oracle, SQL Server, H2, MinIO |
| 실행 환경 연동 | Kubernetes Client (io.kubernetes 18.0.0) |
| API 문서 | springdoc-openapi (Swagger UI) |

## 구조

| 패키지 | 역할 |
|---|---|
| `controller` | REST 엔드포인트입니다. |
| `service` | 수집·조회 로직입니다. 저장소 종류별 상태 조회 구현을 포함합니다. |
| `repository` | JPA 리포지토리입니다. |
| `domain` | 영속 엔티티입니다. |
| `dto`, `vo` | 요청·응답 모델과 내부 전달 모델입니다. |
| `config` | 스케줄러, 저장소 종류 레지스트리, 메시지·Swagger 설정입니다. |
| `enums` | 저장소 종류 등 열거형입니다. YAML 정의로부터 생성합니다. |
| `utils` | Kubernetes 클라이언트 등 공통 유틸리티입니다. |
| `exception`, `aspect`, `annotation` | 예외 처리와 공통 응답 처리입니다. |

오류 메시지는 `src/main/resources/i18n/`에서 한국어·영어를 제공합니다.

## API

Swagger UI는 기동 후 `/swagger-ui/index.html`에서, OpenAPI 명세는 `/api-docs`에서 확인할 수 있습니다.

| 경로 | 설명 |
|---|---|
| `GET /api/v1/monitoring/connectStatus` | 저장소 연결 상태와 요약입니다. |
| `GET /api/v1/monitoring/avgResponseTime`, `/recResponseTime`, `/responseTime/{serviceID}` | 응답 시간 통계입니다. |
| `GET /api/v1/monitoring/connectionHistory` | 연결 이력입니다. |
| `GET /api/v1/monitoring/models`, `/ingestionHistory` | 등록 모델과 수집 이력입니다. |
| `POST /api/v1/monitoring/service/sync` | 저장소 등록 정보를 갱신합니다. |
| `POST /api/v1/database/test-connection`, `/test-database`, `/test-table`, `/test-schema` | 저장소 접속·조회 가능 여부를 점검합니다. |
| `POST /scheduler/add`, `/remove`, `GET /scheduler/list` | 수집 작업 스케줄을 제어합니다. |

## 빌드 및 실행

### 요구 사항

- JDK 21
- 수집 결과를 보관할 데이터베이스 (PostgreSQL 권장)

### 빌드

```bash
./gradlew clean build
```

`check` 단계에서 `validate` 작업이 함께 수행되어, YAML 정의로부터 저장소 종류 열거형이 생성됩니다.

### 실행

```bash
./gradlew bootRun
```

### 설정

접속 정보와 수집 주기는 환경 변수로 주입합니다. 주요 항목은 다음과 같습니다.

| 환경 변수 | 설명 |
|---|---|
| `DATASOURCE_URL`, `DATASOURCE_USERNAME`, `DATASOURCE_PASSWORD`, `DATASOURCE_CLASS_NAME` | 수집 결과를 보관할 데이터베이스 접속 정보입니다. |
| `JPA_PROPERTIES_HIBERNATE_DIALECT` | 위 데이터베이스의 Hibernate Dialect입니다. |
| `TARGET_SERVER_URL`, `TARGET_SERVER_ID`, `TARGET_SERVER_PW` | 저장소 등록 정보를 가져올 메타데이터 서버 접속 정보입니다. |
| `COLLECT_EXPRESSION`, `SAVE_EXPRESSION` | 수집·저장 주기입니다. cron 표현식으로 지정합니다. |
| `NAMESPACE`, `K8S_IP` | Kubernetes 환경에서 사용할 네임스페이스와 접근 주소입니다. |

Kubernetes 클러스터 내부에서 실행하면 in-cluster 설정을 사용합니다.
로컬에서 실행하여 클러스터에 접근해야 하는 경우에는 `KUBECONFIG` 환경 변수로 kubeconfig 경로를 지정하십시오.

Docker 기반 설치·실행 방법은 [docs/InstallAndRunGuide.md](docs/InstallAndRunGuide.md)를 참고하십시오.

## 라이선스

Apache License 2.0을 따릅니다. 자세한 내용은 [LICENSE](LICENSE)를 참고하십시오.
