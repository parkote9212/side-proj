# Frontend - 옥션 맵 웹 애플리케이션

React 기반의 단일 페이지 애플리케이션(SPA)입니다.

## 📋 개요

경매 물건 정보를 지도와 목록으로 조회하고 관리할 수 있는 프론트엔드 애플리케이션입니다.

## 🛠️ 기술 스택

- **Framework**: React 19.2.0
- **Build Tool**: Vite 7.2.2
- **Language**: JavaScript (ES6+)
- **Styling**: Tailwind CSS 4.1.17
- **State Management**: Zustand 5.0.8
- **Routing**: React Router DOM 7.9.5
- **HTTP Client**: Axios 1.13.2
- **Maps**: react-kakao-maps-sdk 1.2.0
- **Charts**: Recharts 3.4.1
- **UI Components**: react-spinners 0.17.0

## 📁 프로젝트 구조

```
frontend/
├── src/
│   ├── api/              # API 호출 함수
│   │   ├── axiosInstance.js
│   │   ├── authApi.js
│   │   ├── itemApi.js
│   │   └── ...
│   ├── components/       # 재사용 가능한 컴포넌트
│   │   ├── Navi.jsx
│   │   ├── SearchFilter.jsx
│   │   ├── ItemCard.jsx
│   │   ├── ItemMap.jsx
│   │   ├── ItemDetailModal.jsx
│   │   └── ErrorMessage.jsx
│   ├── pages/           # 페이지 컴포넌트
│   │   ├── MainPage.jsx
│   │   ├── LoginPage.jsx
│   │   ├── RegisterPage.jsx
│   │   ├── DashboardPage.jsx
│   │   └── MyPage.jsx
│   ├── store/           # Zustand 상태 관리
│   │   ├── authStore.js
│   │   └── savedItemStore.js
│   ├── App.jsx          # 메인 앱 컴포넌트
│   └── main.jsx         # 진입점
├── public/              # 정적 파일
├── index.html          # HTML 템플릿
├── package.json        # 의존성 및 스크립트
└── vite.config.js      # Vite 설정
```

## 🚀 시작하기

### 사전 요구사항

- Node.js 18 이상
- npm 또는 yarn

### 설치

```bash
# 의존성 설치
npm install
```

### 환경 변수 설정

`.env` 파일을 생성하고 다음 내용을 추가:

```env
VITE_KAKAO_MAP_JS_KEY=your_kakao_map_api_key
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

### 개발 서버 실행

```bash
npm run dev
```

개발 서버는 기본적으로 `http://localhost:5173`에서 실행됩니다.

### 빌드

```bash
# 프로덕션 빌드
npm run build

# 빌드 결과물은 dist/ 폴더에 생성됩니다
```

### 미리보기

```bash
# 빌드된 결과물 미리보기
npm run preview
```

## 📱 주요 기능

### 1. 메인 페이지 (지도/목록)
- 카카오맵을 활용한 지도 시각화
- 마커 클러스터링
- 지역별 필터링
- 키워드 검색
- 물건 목록 조회
- 페이지네이션

### 2. 통계 대시보드
- 지역별 평균 입찰가 막대 차트
- 카테고리별 물건 개수 파이 차트

### 3. 사용자 기능
- 로그인/회원가입
- 찜 목록 관리
- 마이페이지

## 🎨 컴포넌트 구조

### 재사용 가능한 컴포넌트

- **SearchFilter**: 검색 필터 컴포넌트
- **ItemCard**: 물건 카드 컴포넌트
- **ItemMap**: 지도 컴포넌트
- **ItemDetailModal**: 상세 정보 모달
- **ErrorMessage**: 에러 메시지 토스트

### 페이지 컴포넌트

- **MainPage**: 메인 페이지 (지도 + 목록)
- **DashboardPage**: 통계 대시보드
- **LoginPage**: 로그인 페이지
- **RegisterPage**: 회원가입 페이지
- **MyPage**: 마이페이지 (찜 목록)

## 🔐 인증

JWT 토큰 기반 인증을 사용합니다.

- 토큰은 `localStorage`에 저장
- Zustand를 통한 전역 상태 관리
- Axios 인터셉터를 통한 자동 토큰 첨부

## 📡 API 통신

### Axios 인스턴스

`src/api/axiosInstance.js`에서 기본 설정:
- Base URL 설정
- 요청 인터셉터 (토큰 자동 첨부)
- 응답 인터셉터 (에러 처리)

### API 함수

각 도메인별로 API 함수를 분리:
- `authApi.js`: 인증 관련
- `itemApi.js`: 물건 조회 관련
- `savedItemApi.js`: 찜 목록 관련
- `statisticsApi.js`: 통계 관련

## 🎯 상태 관리

Zustand를 사용한 전역 상태 관리:

- **authStore**: 인증 상태 (토큰, 로그인 여부)
- **savedItemStore**: 찜 목록 상태

## 🗺️ 지도 기능

카카오맵 SDK를 사용하여:
- 지도 표시
- 마커 클러스터링
- 마커 클릭 이벤트 처리

## 📊 차트 기능

Recharts를 사용하여:
- 막대 차트 (지역별 평균 가격)
- 파이 차트 (카테고리별 개수)

## 🎨 스타일링

Tailwind CSS를 사용한 유틸리티 퍼스트 스타일링:
- 반응형 디자인
- 다크 모드 지원 가능
- 커스텀 색상 및 스타일

## 🧪 테스트

```bash
# 린트 검사
npm run lint
```

## 🚀 성능 최적화

- React.memo를 사용한 컴포넌트 메모이제이션
- useMemo, useCallback을 사용한 값/함수 메모이제이션
- 코드 스플리팅 (필요시)

## 📱 반응형 디자인

- 모바일, 태블릿, 데스크톱 대응
- Tailwind CSS의 반응형 유틸리티 사용
- 최대 너비 제한으로 큰 화면 대응

## 🐛 문제 해결

### 카카오맵이 표시되지 않을 때
- `.env` 파일에 `VITE_KAKAO_MAP_JS_KEY`가 올바르게 설정되었는지 확인
- 카카오 개발자 콘솔에서 도메인 등록 확인

### API 호출 실패
- 백엔드 서버가 실행 중인지 확인
- CORS 설정 확인
- `.env` 파일의 `VITE_API_BASE_URL` 확인

## 📄 라이선스

개인 포트폴리오 프로젝트
