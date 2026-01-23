# Backend - 옥션 맵 API 서버

Spring Boot 기반의 RESTful API 서버입니다.

## 📋 개요

경매 물건 정보를 관리하고 제공하는 백엔드 서버입니다. 공매 데이터를 수집, 저장, 조회하는 기능을 제공합니다.

## 🛠️ 기술 스택

- **Framework**: Spring Boot 3.3.5
- **Language**: Java 21
- **Build Tool**: Gradle
- **Database**: MariaDB
- **ORM**: MyBatis 3.0.5
- **Security**: Spring Security + JWT
- **API Documentation**: SpringDoc OpenAPI 2.3.0
- **Other**:
  - Spring WebFlux (외부 API 호출)
  - ShedLock (분산 락)
  - Spring Retry (재시도 로직)

## 📁 프로젝트 구조

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/pgc/sideproj/
│   │   │   ├── config/          # 설정 클래스
│   │   │   ├── controller/      # REST 컨트롤러
│   │   │   ├── service/         # 비즈니스 로직
│   │   │   ├── mapper/          # MyBatis 인터페이스
│   │   │   ├── dto/             # 데이터 전송 객체
│   │   │   ├── exception/       # 예외 처리
│   │   │   ├── filter/          # 필터
│   │   │   └── util/            # 유틸리티
│   │   └── resources/
│   │       ├── application.properties    # 설정 파일
│   │       ├── mappers/          # MyBatis XML 매퍼
│   │       └── db/               # DB 초기화 스크립트
│   └── test/                     # 테스트 코드
├── build.gradle                  # Gradle 빌드 설정
└── settings.gradle              # Gradle 설정
```

## 🚀 시작하기

### 사전 요구사항

- Java 21 이상
- Gradle 7.x 이상 (또는 Gradle Wrapper 사용)
- MariaDB 또는 MySQL

### 데이터베이스 설정

1. MariaDB/MySQL 데이터베이스 생성
2. `src/main/resources/application.properties` 파일 수정:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. 데이터베이스 스키마 초기화:
   - `db스키마.sql` 파일 실행
   - 또는 `src/main/resources/db/init-mariadb.sql` 사용

### 실행 방법

#### Gradle Wrapper 사용 (권장)

```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

#### 직접 실행

```bash
./gradlew build
java -jar build/libs/side-proj-0.0.1-SNAPSHOT.jar
```

서버는 기본적으로 `http://localhost:8080`에서 실행됩니다.

## 📚 API 엔드포인트

### 인증
- `POST /api/v1/auth/register` - 회원가입
- `POST /api/v1/auth/login` - 로그인

### 물건 조회
- `GET /api/v1/items` - 물건 목록 조회 (검색, 필터링, 페이지네이션)
- `GET /api/v1/items/{cltr_no}` - 물건 상세 조회

### 찜 목록
- `GET /api/v1/saved-items` - 내 찜 목록 조회
- `POST /api/v1/saved-items/{item_id}` - 찜하기
- `DELETE /api/v1/saved-items/{item_id}` - 찜 취소

### 통계
- `GET /api/v1/statistics` - 통계 데이터 조회

### 관리자
- `GET /api/v1/admin/users` - 전체 사용자 조회 (관리자만)

## 🔐 인증

JWT(JSON Web Token) 기반 인증을 사용합니다.

### 토큰 사용 방법

```
Authorization: Bearer {token}
```

## 📖 API 문서

Swagger UI를 통해 API 문서를 확인할 수 있습니다:

```
http://localhost:8080/swagger-ui.html
```

## 🧪 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "com.pgc.sideproj.mapper.UserMapperTest"
```

## 🔧 주요 기능

### 1. 예외 처리
- 커스텀 예외 클래스 사용
- `GlobalExceptionHandler`를 통한 통합 예외 처리
- HTTP 상태 코드 매핑

### 2. 입력값 검증
- Bean Validation 사용
- 커스텀 Validator 구현

### 3. 로깅
- SLF4J + Logback 사용
- 주요 메서드에 로깅 적용

### 4. 데이터 수집
- 배치 작업을 통한 공매 데이터 수집
- ShedLock을 사용한 분산 환경 대응

## 📝 설정 파일

### application.properties

주요 설정 항목:
- 데이터베이스 연결 정보
- JWT 시크릿 키
- 외부 API 설정 (Onbid API)
- 카카오맵 API 설정

### 프로파일별 설정
- `application-dev.properties` - 개발 환경
- `application-prod.properties` - 운영 환경

## 🗄️ 데이터베이스

### 주요 테이블
- `auction_master` - 경매 물건 기본 정보
- `auction_history` - 경매 가격 이력
- `user` - 사용자 정보
- `saved_item` - 찜 목록

## 🔄 배치 작업

공매 데이터를 주기적으로 수집하는 배치 작업이 포함되어 있습니다.

## 📦 빌드

```bash
# JAR 파일 생성
./gradlew build

# 생성된 JAR 파일 위치
build/libs/side-proj-0.0.1-SNAPSHOT.jar
```

## 🐛 문제 해결

### 포트 충돌
`application.properties`에서 포트 변경:
```properties
server.port=8081
```

### 데이터베이스 연결 오류
- 데이터베이스 서버가 실행 중인지 확인
- 연결 정보가 올바른지 확인
- 방화벽 설정 확인

## 📄 라이선스

개인 포트폴리오 프로젝트
