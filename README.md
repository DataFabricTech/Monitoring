# Monitoring

## Monitoring Project

모니터링이란 설정된 애플리케이션에 등록되어 있는 서비스와 컨테이너의 연결 상태, 저장소 유형, 연결 응답 시간등을 모니터링 하기 위한 서비스입니다.

## Contents:

- [Features](#key-features-of-monitoring)
- [Install & Run](#install-and-run-monitoring)
- [Versions](#tool-versions-of-monitoring)
- [RoadMap](#roadmap)
- [Contributors](#contributes)

## Key features of monitoring

**connectStatus**: 설정된 애플리케이션에 등록되어 있는 서비스/컨테이너의 연결 정보 상태의 요약 상태 조회할 수 있다. 또한, 특정 서비스/컨테이너의 연결 상태를 조회 할 수 있으며 해당 서비스의 연결
상태 변경 이력을 조회한다.  
**responseTime**: 설정된 애플리케이션에 등록되어 있는 서비스/컨테이너 연결 시의 평균 응답시간을 조회할 수 있다. 또한, 특정 서비스/컨테이너의 응답 시간을 조회 할 수 있으며 해당 서비스의
응답시간들을 조회한다.  
**eventHistory**: 설정된 애플리케이션의 서비스/컨테이너의 상태값이 변경되었을 시에 대한 히스토리를 조회할 수 있다. 또한, 특정 서비스/컨테이너의 히스토리를 조회할 수 있으며 해당 서비스의 히스토리들을
조회한다.   
**modelRegistration**: 설정된 애플리케이션의 데이터 모델로 등록된 데이터의 개수와 실제 저장소의 데이터 개수를 조회할 수 있다.

## Install and run monitoring

설치 및 실행 방법에 대한 [가이드 문서](docs/InstallAndRunGuide.md)를 보시기 바랍니다.

## RoadMap

Monitoring 로드맵에 대한 [로드맵](docs/RoadMap.md)를 보시기 바랍니다.

## tool-versions-of-monitoring

- Gradle 8.5 (kotlin)
- Java 21
- SpringBoot 3.3.0

## Swagger 접속 방법
- http://{url}/swagger-ui/index.html#/Monitoring

## Contributes

Monitoring를 위해 기여하는 모든 Contributor들을 환영합니다!   
[Install & Run](#install-and-run-monitoring)를 참조하여 실행 시키고, 코드에 기여해보세요!


<a href="https://github.com/DataFabricTech/monitoring/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=DataFabricTech/Monitoring&max=4000&columns=30"  alt=""/>
</a>