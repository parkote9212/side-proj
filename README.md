# Side Project - 공매 물건 정보 관리 시스템

## 프로젝트 개요

온비드(Onbid) 공매 물건 정보를 수집, 관리하고 사용자에게 제공하는 풀스택 웹 애플리케이션입니다. 사용자는 공매 물건을 검색하고, 지도에서 위치를 확인하며, 관심 물건을 저장할 수 있습니다.

### 주요 특징
- **공매 물건 데이터 관리**: 온비드 API에서 물건 정보를 수집하여 데이터베이스에 저장
- **Full-Text Search**: MySQL Full-Text 인덱스를 활용한 빠른 주소/물건명 검색
- **지도 기반 위치 표시**: 카카오 맵 API를 통한 물건 위치 시각화
- **사용자 인증**: JWT 기반 로그인 및 회원가입
- **찜 기능**: 사용자별 관심 물건 저장 및 관리
- **통계 대시보드**: 공매 물건 통계 시각화

## 기술 스택

### Backend
- **Java 21** - 최신 LTS 버전
- **Spring Boot 3.5.7** - 백엔드 프레임워크
- **MyBatis 3.0.5** - SQL 매퍼
- **Gradle** - 빌드 시스템
- **MySQL / MariaDB** - 데이터베이스
- **WebFlux** - 비동기 HTTP 클라이언트
- **Spring Retry** - 재시도 로직
- **BCrypt** - 비밀번호 암호화
- **JWT** - 토큰 기반 인증

### Frontend
- **React 19.2.0** - UI 라이브러리
- **Vite 7.2.2** - 빌드 도구 및 개발 서버
- **TailwindCSS 4.x** - 유틸리티 기반 CSS 프레임워크
- **React Router DOM 7.9.5** - 클라이언트 사이드 라우팅
- **Zustand 5.0.8** - 가벼운 상태 관리
- **Axios 1.13.2** - HTTP 클라이언트
- **react-kakao-maps-sdk 1.2.0** - 카카오 지도 연동
- **Recharts 3.4.1** - 차트 라이브러리
- **jwt-decode 4.0.0** - JWT 토큰 디코딩
- **React Spinners** - 로딩 인디케이터

## 프로젝트 구조

```
side-proj/
├── backend/                    # Spring Boot 백엔드
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/pgc/sideproj/
│   │   │   │   ├── SideProjApplication.java
│   │   │   │   ├── config/          # 설정 클래스
│   │   │   │   ├── dto/              # 데이터 전송 객체
│   │   │   │   │   ├── db/           # 데이터베이스 DTO
│   │   │   │   │   ├── onbid/        # 온비드 API DTO
│   │   │   │   │   └── kakao/        # 카카오 API DTO
│   │   │   │   ├── mapper/           # MyBatis 매퍼
│   │   │   │   └── service/          # 비즈니스 로직
│   │   │   │       ├── OnbidApiService.java
│   │   │   │       ├── KakaoMapService.java
│   │   │   │       ├── AuctionBatchService.java
│   │   │   │       └── DataCleansingService.java
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── mapper/           # MyBatis XML 매퍼
│   │   └── test/
│   ├── build.gradle                  # Gradle 빌드 설정
│   ├── settings.gradle
│   ├── gradlew
│   ├── gradlew.bat
│   ├── gradle/
│   ├── db스키마.sql                   # 데이터베이스 스키마
│   └── .gitignore
├── frontend/                   # React 프론트엔드
│   ├── src/
│   │   ├── api/                      # API 통신 모듈
│   │   │   ├── axiosInstance.js      # Axios 인스턴스
│   │   │   ├── authApi.js            # 인증 API
│   │   │   ├── itemApi.js            # 물건 API
│   │   │   ├── savedItemApi.js       # 찜 API
│   │   │   ├── myPageApi.js          # 마이페이지 API
│   │   │   └── statisticsApi.js      # 통계 API
│   │   ├── components/               # 공통 컴포넌트
│   │   │   ├── Navi.jsx              # 네비게이션 바
│   │   │   └── AdminRoute.jsx        # 관리자 라우트 보호
│   │   ├── pages/                    # 페이지 컴포넌트
│   │   │   ├── MainPage.jsx          # 메인 페이지 (검색, 지도)
│   │   │   ├── LoginPage.jsx         # 로그인
│   │   │   ├── RegisterPage.jsx      # 회원가입
│   │   │   ├── MyPage.jsx            # 마이페이지
│   │   │   ├── DashboardPage.jsx     # 통계 대시보드
│   │   │   └── AdminPage.jsx         # 관리자 페이지
│   │   ├── store/                    # Zustand 스토어
│   │   │   ├── authStore.js          # 인증 상태
│   │   │   └── savedItemStore.js     # 찜 상태
│   │   ├── assets/                   # 정적 자산
│   │   ├── App.jsx                   # 앱 진입점
│   │   ├── App.css
│   │   ├── main.jsx                  # React 진입점
│   │   └── index.css
│   ├── public/                       # 정적 파일
│   ├── index.html                    # HTML 템플릿
│   ├── package.json                  # npm 의존성
│   ├── package-lock.json
│   ├── vite.config.js                # Vite 설정
│   ├── eslint.config.js              # ESLint 설정
│   └── .gitignore
├── .gitignore                  # 루트 .gitignore
├── .gitattributes
└── README.md                   # 이 파일
```

## 주요 기능

### 1. 공매 물건 관리
- **물건 마스터 정보** (`auction_master`)
  - 물건번호, 물건명, 카테고리
  - 지번 주소 및 도로명 주소
  - 위도/경도 좌표 (카카오 지도 API를 통해 수집)
  - 온비드 상세 페이지 URL
  
- **물건 이력 정보** (`auction_history`)
  - 물건이력번호
  - 최저입찰가, 감정가
  - 입찰 시작일시, 마감일시
  - 물건 상태 (진행중, 유찰 등)

- **Full-Text Search**
  - 정제된 주소 및 물건명에 대한 전문 검색
  - 빠른 검색 성능을 위한 인덱스 활용

- **지도 기반 시각화**
  - 카카오 맵을 통한 물건 위치 표시
  - 클러스터링을 통한 효율적인 표시

### 2. 사용자 관리
- **회원가입 및 로그인**
  - 이메일 기반 인증
  - BCrypt를 사용한 안전한 비밀번호 저장
  - JWT 토큰 기반 인증
  
- **권한 관리**
  - 일반 사용자 (`ROLE_USER`)
  - 관리자 (`ROLE_ADMIN`)

### 3. 찜 기능
- 사용자별 관심 물건 저장
- 찜한 물건 목록 조회
- 찜 추가/제거

### 4. 통계 대시보드
- 공매 물건 통계 시각화
- 카테고리별, 지역별 통계
- Recharts를 활용한 차트 표시

### 5. 관리자 기능
- 데이터 수집 관리
- 온비드 API를 통한 물건 정보 배치 수집

## 데이터베이스 설정

### 데이터베이스 생성 및 스키마 적용

```sql
CREATE DATABASE onbid_db;
USE onbid_db;
```

그 다음 `backend/db스키마.sql` 파일을 실행하여 테이블을 생성합니다:

```bash
mysql -u [username] -p onbid_db < backend/db스키마.sql
```

### 테이블 구조

1. **auction_master** - 공매 물건 마스터 (불변 정보)
2. **auction_history** - 공매 물건 이력 (가변 정보)
3. **user** - 사용자
4. **saved_item** - 찜 목록

## 실행 방법

### 사전 요구사항

- **Backend**: Java 21, MySQL/MariaDB
- **Frontend**: Node.js 18+ (권장: 20+)

### Backend 실행

1. 데이터베이스 설정을 완료합니다.

2. `backend/src/main/resources/application.yml` 파일에서 데이터베이스 연결 정보를 설정합니다:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/onbid_db
       username: your_username
       password: your_password
   ```

3. 백엔드 서버를 실행합니다:
   ```bash
   cd backend
   ./gradlew bootRun
   ```

   또는 빌드 후 실행:
   ```bash
   ./gradlew build
   java -jar build/libs/side-proj-0.0.1-SNAPSHOT.jar
   ```

백엔드 서버는 기본적으로 `http://localhost:8080`에서 실행됩니다.

### Frontend 실행

1. 의존성을 설치합니다:
   ```bash
   cd frontend
   npm install
   ```

2. 개발 서버를 시작합니다:
   ```bash
   npm run dev
   ```

프론트엔드는 기본적으로 `http://localhost:5173`에서 실행됩니다.

### 프로덕션 빌드

**Frontend:**
```bash
cd frontend
npm run build
```

빌드된 파일은 `frontend/dist/` 디렉토리에 생성됩니다.

**Backend:**
```bash
cd backend
./gradlew build
```

빌드된 JAR 파일은 `backend/build/libs/` 디렉토리에 생성됩니다.

## 개발 가이드

### Backend 테스트
```bash
cd backend
./gradlew test
```

### Frontend 린팅
```bash
cd frontend
npm run lint
```

### API 엔드포인트 예시

- `POST /api/auth/login` - 로그인
- `POST /api/auth/register` - 회원가입
- `GET /api/items` - 물건 목록 조회
- `GET /api/items/{cltrNo}` - 물건 상세 조회
- `POST /api/saved-items` - 찜 추가
- `DELETE /api/saved-items/{itemId}` - 찜 제거
- `GET /api/statistics` - 통계 조회

## 환경 변수

### Backend
`backend/src/main/resources/application.yml`에서 설정:
- 데이터베이스 연결 정보
- JWT 시크릿 키
- 온비드 API 키
- 카카오 API 키

### Frontend
필요시 `.env` 파일 생성:
```env
VITE_API_BASE_URL=http://localhost:8080
VITE_KAKAO_MAP_APP_KEY=your_kakao_map_key
```

## 라이선스

이 프로젝트는 개인 학습 및 포트폴리오 목적으로 제작되었습니다.

## 기여

이슈 및 풀 리퀘스트를 환영합니다.

## 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 등록해주세요.
