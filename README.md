# JPA PK 생성 전략 별 Bulk Insert 방식 전략 

이 프로젝트는 JPA 환경에서 대용량 데이터를 삽입할 때, **PK 생성 전략(ID Generation Strategy)**에 따른 Bulk Insert 동작 여부와 그에 따른 **성능 차이**를 실측하고 분석합니다.


## 프로젝트 개요

개발 과정에서 대량의 데이터를 `INSERT`해야 하는 상황은 빈번하게 발생합니다. 하지만 JPA(Hibernate)를 사용할 때 기본적으로 많이 사용하는 `IDENTITY` 전략은 성능 최적화 기법인 **Bulk Insert(Batch Insert)**를 지원하지 않는 제약이 있습니다.

본 프로젝트에서는 이러한 제약 사항을 확인하고, 이를 해결하기 위한 대안인 `SEQUENCE` 전략 및 `JdbcTemplate`을 이용한 우회 방법의 성능을 비교 분석합니다.

## 프로젝트 목적

다음 세 가지 핵심 질문에 대한 해답을 찾기 위해 실습 및 성능 측정을 진행합니다.

1. **IDENTITY 전략의 한계 확인**: `spring.jpa.properties.hibernate.jdbc.batch_size` 설정이 `IDENTITY` 전략에서 실제로 동작하는가?
2. **전략별 성능 비교**: `IDENTITY` (기본), `SEQUENCE` (JPA Batch), `IDENTITY` (JDBC 우회) 간의 삽입 속도 차이는 어느 정도인가?
3. **최적의 설정 탐색**: `order_inserts`, `reWriteBatchedInserts` 등 대량 삽입 성능을 극대화하기 위한 DB/라이브러리 옵션을 파악합니다.

## 관련 문서
* [블로그] [JPA에서 Batch Insert 최적화](https://jojoldu.tistory.com/558)


## 기술 스택 (Tech Stack)

* **Language**: Java 17
* **Framework**: Spring Boot 3.x, Spring Data JPA
* **Database**: **PostgreSQL** (Sequence 지원 및 Batch Insert 테스트 목적)

---

## 프로젝트 결과 분석

100건부터 10만 건까지의 데이터를 삽입하며 측정한 결과입니다. (PostgreSQL 환경)

| 삽입 방식 | ID 전략 | 특징 | 10만 건 처리 속도 |
| --- | --- | --- | --- |
| **Regular Inserts** | `IDENTITY` | JPA 기본 `saveAll()`. 배치 처리 미지원 | 약 30.0s |
| **Batch Inserts** | `SEQUENCE` | JPA Batch 설정 적용. 다중 행 삽입 | 약 0.5s |
| **Improved Inserts** | `IDENTITY` | **JdbcTemplate** 사용. JPA 엔티티 관리 우회 | **약 0.2s** |

* **IDENTITY의 한계**: `IDENTITY` 전략은 DB에 데이터를 넣어야만 PK 값을 알 수 있기 때문에, 쓰기 지연 저장소의 쿼리를 모아서 보내는 배칭이 불가능함을 확인했습니다.
* **네트워크 오버헤드**: 개별 `INSERT` 쿼리를 10만 번 보내는 것과, 묶어서 한 번에 보내는 것 사이에는 약 **150배 이상의 성능 차이**가 발생했습니다.
* **JDBC의 우위**: 영속성 컨텍스트 관리 비용이 없는 `JdbcTemplate` 방식이 JPA의 `SEQUENCE` 방식보다도 미세하게 더 빠른 성능을 보였습니다.

---

## 인사이트

* **전략의 선택**: 데이터 양이 적다면 생산성이 높은 `IDENTITY`가 유리하지만, 대용량 처리가 잦은 시스템이라면 `SEQUENCE` 전략이나 별도의 Bulk용 모듈(JDBC)을 고려해야 합니다. 🧐
* **옵션의 중요성**: `reWriteBatchedInserts=true` (PostgreSQL) 또는 `rewriteBatchedStatements=true` (MySQL)와 같은 드라이버 옵션을 설정해야만 실제 다중 행(Multi-row) `INSERT`가 수행됩니다.
* **트레이드 오프(Trade-off)**:
* **JDBC**: 압도적 성능 이점이 있으나, SQL을 직접 작성해야 하므로 Type-Safe하지 않고 생산성이 떨어집니다.
* **JPA**: 객체 지향적인 개발이 가능하지만, Bulk 작업 시 프레임워크의 메커니즘을 정확히 이해하고 설정을 튜닝해야 합니다.
