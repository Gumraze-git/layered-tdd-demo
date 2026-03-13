# layered-tdd-demo

Spring Boot 기반으로 레이어드 아키텍처에서 TDD를 연습하기 위한 데모 프로젝트입니다.

현재 데모는 `Profile` 생성 도메인을 중심으로, 아래 흐름을 테스트로 고정하는 데 초점을 두고 있습니다.

- 유효한 입력이면 프로필이 생성된다.
- 필수값이 비어 있으면 생성이 실패한다.
- 중복 이메일이면 생성이 실패한다.
- 태그는 서버에서 생성되며, 중복되지 않아야 한다.
- 컨트롤러는 HTTP 계약을 반환한다.
- 리포지토리는 실제 DB에서 query method와 persistence 동작을 검증한다.

## 목적

이 프로젝트는 단순히 테스트 코드를 많이 작성하는 것이 아니라, 켄트 백의 `Red -> Green -> Refactor` 흐름을 레이어드 아키텍처에 적용해보는 것을 목표로 합니다.

즉, 아래 순서로 작은 사이클을 반복하며 설계를 조금씩 성장시키는 데 의미가 있습니다.

1. 서비스 테스트로 핵심 비즈니스 규칙을 먼저 고정한다.
2. 최소 구현으로 테스트를 통과시킨다.
3. 컨트롤러 테스트로 HTTP 계약을 고정한다.
4. 리포지토리 테스트로 실제 DB 동작을 검증한다.

## 기술 스택

- Java 21
- Spring Boot 4.0.3
- Spring Web MVC
- Spring Data JPA
- Spring Validation
- H2 Database
- JUnit 5
- Mockito

## 현재 도메인

현재 구현된 도메인은 `Profile` 생성입니다.

`Profile`은 아래 필드를 가집니다.

- `id`
- `email`
- `nickname`
- `passwordHash`
- `profileImageUrl`
- `tag`
- `region`
- `grade`
- `ageGroup`
- `gender`
- `createdAt`
- `updatedAt`

도메인 규칙은 다음과 같습니다.

- `email`, `nickname`, `passwordHash`, `region`, `grade`, `ageGroup`, `gender`는 필수값입니다.
- `email`은 중복될 수 없습니다.
- `tag`는 사용자가 입력하지 않고 서버가 생성합니다.
- `tag`는 4자리의 대문자 영어/숫자 조합이며, 중복되면 안 됩니다.

## 프로젝트 구조

```text
src
├── main
│   └── java/com/gumraze/demo/layeredtdd
│       ├── common/exception
│       └── profile
│           ├── constants
│           ├── controller
│           ├── dto
│           ├── entity
│           ├── repository
│           └── service
└── test
    └── java/com/gumraze/demo/layeredtdd
        └── profile
            ├── controller
            ├── repository
            └── service
```

## 테스트 구성

이 프로젝트는 레이어별로 테스트를 나누어 작성합니다.

### 1. 서비스 테스트

`ProfileServiceTest`에서는 비즈니스 규칙을 검증합니다.

- 유효한 입력이면 생성 성공
- 중복 이메일이면 실패
- 필수값 누락이면 실패
- 태그 생성 및 중복 회피

여기서는 `ProfileRepository`, `ProfileTagGenerator`를 mock으로 두고 서비스 규칙만 확인합니다.

### 2. 태그 생성기 테스트

`RandomProfileTagGeneratorTest`에서는 태그 형식 규칙만 검증합니다.

- 생성된 태그는 4자리
- 생성된 태그는 대문자 영어와 숫자로만 구성

즉, 서비스는 태그 사용 규칙을 담당하고, 생성기는 태그 형식 규칙을 담당하도록 분리했습니다.

### 3. 컨트롤러 테스트

`ProfileControllerTest`에서는 HTTP 계약을 검증합니다.

- 유효한 요청이면 `201 Created`
- 잘못된 enum 값이면 `400 Bad Request`
- 서비스 예외는 `409 Conflict`로 변환
- 컨트롤러가 서비스에 요청을 위임하는지 검증

여기서는 `@WebMvcTest`, `MockMvc`, `@MockitoBean`을 사용합니다.

### 4. 리포지토리 테스트

`ProfileRepositoryTest`에서는 실제 DB 기준 동작을 검증합니다.

- `existsByEmail()`
- `existsByTag()`
- 저장 시 `id`, `createdAt`, `updatedAt` 생성

여기서는 `@DataJpaTest`와 H2 인메모리 DB를 사용합니다.

## 실행 방법

애플리케이션 실행:

```bash
./gradlew bootRun
```

테스트 실행:

```bash
./gradlew test
```

## H2 설정

현재 프로젝트는 H2 in-memory DB를 사용합니다.

- JDBC URL: `jdbc:h2:mem:layeredtdddemo`
- H2 Console: `/h2-console`

애플리케이션 실행 후 H2 콘솔로 접속해서 테이블 상태를 확인할 수 있습니다.

## 관련 글

- [TDD, TDD 해보자](https://velog.io/@gumraze/TDD-TDD-%ED%95%B4%EB%B3%B4%EC%9E%90)

## 현재 구현된 주요 클래스

- `Profile`
- `ProfileService`
- `ProfileServiceImpl`
- `ProfileTagGenerator`
- `RandomProfileTagGenerator`
- `ProfileController`
- `ProfileRepository`
- `GlobalExceptionHandler`

## 참고

이 프로젝트는 학습과 실험을 위한 데모 프로젝트입니다.

그래서 실제 서비스 수준의 예외 세분화, 응답 표준화, 패키지 구조 고도화보다는  
레이어드 아키텍처에서 TDD를 어떤 순서로 적용할 수 있는가를 보여주는 데 더 집중하고 있습니다.
