# 옥션 맵 (Auction Map)

경매 물건 정보를 지도와 목록으로 조회하고 관리할 수 있는 웹 애플리케이션입니다.

## 📋 프로젝트 개요

이 프로젝트는 공매 물건 정보를 수집하고, 지도 기반으로 시각화하여 사용자가 쉽게 검색하고 관리할 수 있도록 하는 풀스택 웹 애플리케이션입니다.

### 주요 기능

- 🗺️ **지도 기반 물건 조회**: 카카오맵을 활용한 지도 시각화
- 🔍 **검색 및 필터링**: 키워드 및 지역별 검색 기능
- 📊 **통계 대시보드**: 지역별 평균 가격, 카테고리별 통계
- ❤️ **찜 목록 관리**: 관심 물건 저장 및 관리
- 🔐 **사용자 인증**: JWT 기반 로그인/회원가입

## 🏗️ 기술 스택

### Backend
- **Framework**: Spring Boot 3.3.5
- **Language**: Java 21
- **Database**: MariaDB
- **ORM**: MyBatis
- **Security**: Spring Security + JWT
- **API Documentation**: SpringDoc OpenAPI (Swagger)

### Frontend
- **Framework**: React 19.2.0
- **Build Tool**: Vite 7.2.2
- **Styling**: Tailwind CSS 4.1.17
- **State Management**: Zustand 5.0.8
- **Maps**: react-kakao-maps-sdk
- **Charts**: Recharts 3.4.1

## 📁 프로젝트 구조

```
side-proj/
├── backend/          # Spring Boot 백엔드
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/        # Java 소스 코드
│   │   │   └── resources/   # 설정 파일 및 MyBatis 매퍼
│   │   └── test/            # 테스트 코드
│   └── build.gradle        # Gradle 빌드 설정
│
├── frontend/        # React 프론트엔드
│   ├── src/
│   │   ├── api/            # API 호출 함수
│   │   ├── components/     # 재사용 가능한 컴포넌트
│   │   ├── pages/         # 페이지 컴포넌트
│   │   └── store/         # Zustand 상태 관리
│   └── package.json       # npm 의존성
│
└── README.md        # 프로젝트 루트 README
```

## 🚀 시작하기

### 사전 요구사항

- Java 21 이상
- Node.js 18 이상
- MariaDB 또는 MySQL
- Gradle 7.x 이상 (또는 Gradle Wrapper 사용)

### 설치 및 실행

#### 1. 백엔드 설정

```bash
cd backend

# 데이터베이스 설정
# application.properties 파일에서 데이터베이스 연결 정보 수정

# 애플리케이션 실행
./gradlew bootRun
```

백엔드는 기본적으로 `http://localhost:8080`에서 실행됩니다.

#### 2. 프론트엔드 설정

```bash
cd frontend

# 의존성 설치
npm install

# 개발 서버 실행
npm run dev
```

프론트엔드는 기본적으로 `http://localhost:5173`에서 실행됩니다.

#### 3. 환경 변수 설정

프론트엔드에서 카카오맵 API 키가 필요합니다:

```bash
# frontend/.env 파일 생성
VITE_KAKAO_MAP_JS_KEY=your_kakao_map_api_key
```

## 📚 API 문서

백엔드 실행 후 Swagger UI에서 API 문서를 확인할 수 있습니다:

```
http://localhost:8080/swagger-ui.html
```

## 🧪 테스트

### 백엔드 테스트
```bash
cd backend
./gradlew test
```

### 프론트엔드 테스트
```bash
cd frontend
npm run lint
```

## 📝 주요 기능 상세

### 1. 물건 조회
- FTS(Full-Text Search) 기반 검색
- 지역별 필터링
- 페이지네이션 지원

### 2. 지도 시각화
- 카카오맵을 활용한 마커 클러스터링
- 물건 위치 표시
- 지도와 목록 연동

### 3. 통계 대시보드
- 지역별 평균 입찰가 막대 차트
- 카테고리별 물건 개수 파이 차트

### 4. 사용자 기능
- JWT 기반 인증
- 찜 목록 관리
- 마이페이지

## 🔧 개발 가이드

### 백엔드 개발
- [backend/README.md](./backend/README.md) 참고

### 프론트엔드 개발
- [frontend/README.md](./frontend/README.md) 참고

## 📄 라이선스

이 프로젝트는 개인 포트폴리오 목적으로 제작되었습니다.

## 👤 작성자

개인 프로젝트
