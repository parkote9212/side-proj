# Side Project - Backend

Spring Boot 기반의 경매 물건 정보 서비스 백엔드 애플리케이션

## 기술 스택

- **Java 21**
- **Spring Boot 3.3.5**
- **MyBatis**
- **MariaDB** (프로덕션) / **H2** (개발)
- **Spring Security + JWT**
- **WebClient** (외부 API 연동)

## 시작하기

### 1. 환경 변수 설정

프로젝트 루트에 `.env` 파일을 생성하고 필요한 환경 변수를 설정하세요:

```bash
cp .env.example .env
```

`.env` 파일을 열어 다음 값들을 설정하세요:

- `JWT_SECRET_KEY`: JWT 토큰 서명에 사용할 시크릿 키 (최소 32자 이상)
- `ONBID_API_SERVICEKEY`: [공공데이터포털](https://www.data.go.kr/)에서 발급받은 온비드 API 서비스 키
- `KAKAO_API_RESTAPIKEY`: [Kakao Developers](https://developers.kakao.com/)에서 발급받은 REST API 키

### 2. 빌드 및 실행

#### Gradle을 사용한 빌드

```bash
./gradlew clean build
```

#### 애플리케이션 실행

**방법 1: Gradle로 실행**
```bash
./gradlew bootRun
```

**방법 2: JAR 파일로 실행**
```bash
java -jar build/libs/side-proj-0.0.1-SNAPSHOT.jar
```

**방법 3: 환경 변수를 직접 지정하여 실행**
```bash
java -jar build/libs/side-proj-0.0.1-SNAPSHOT.jar \
  --JWT_SECRET_KEY=your-secret-key \
  --ONBID_API_SERVICEKEY=your-onbid-key \
  --KAKAO_API_RESTAPIKEY=your-kakao-key
```

### 3. 데이터베이스 설정

#### 개발 환경 (H2 In-Memory)

기본적으로 H2 인메모리 데이터베이스가 사용됩니다. 별도 설정 없이 바로 실행 가능합니다.

- H2 Console: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (비어있음)

#### 프로덕션 환경 (MariaDB)

`.env` 파일에 다음 설정을 추가하세요:

```properties
SPRING_DATASOURCE_URL=jdbc:mariadb://localhost:3306/onbid_db
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.mariadb.jdbc.Driver
```

## API 문서

애플리케이션 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI 스펙: http://localhost:8080/v3/api-docs

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/pgc/sideproj/
│   │   ├── aop/              # AOP 로깅
│   │   ├── config/           # 설정 클래스
│   │   ├── controller/       # REST API 컨트롤러
│   │   ├── dto/              # 데이터 전송 객체
│   │   ├── exception/        # 예외 처리
│   │   ├── filter/           # JWT 필터
│   │   ├── mapper/           # MyBatis 매퍼
│   │   ├── service/          # 비즈니스 로직
│   │   └── util/             # 유틸리티 (JWT Provider)
│   └── resources/
│       ├── mappers/          # MyBatis XML 매퍼
│       └── application.properties
└── test/                     # 테스트 코드
```

## 보안 주의사항

⚠️ **중요**: `.env` 파일은 절대 Git에 커밋하지 마세요!

- `.env` 파일은 `.gitignore`에 포함되어 있습니다
- `.env.example`을 템플릿으로 사용하세요
- 프로덕션 환경에서는 환경 변수나 시크릿 관리 시스템을 사용하세요

## 라이선스

이 프로젝트는 사이드 프로젝트입니다.
