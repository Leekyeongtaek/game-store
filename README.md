# Game-Store 프로젝트
- 플레이스테이션 스토어를 참고해서 만든 게임 관리 사이트입니다.

## AWS EC2 운영 서버 주소
- http://15.165.177.116:8080/

## 목차
- [프로젝트 기술 상세](#프로젝트-기술-상세)
- [테이블 ERD](#테이블-ERD)

### 프로젝트 기술 상세
- IDE : IntelliJ
- Java : 17
- SpringBoot : 3.4.1
- MySql : 8.0.32
- ORM 기술 : JPA, SpringDataJPA, QueryDSL
- 뷰 템플릿 : 타임리프

## 테이블 ERD

### 게임 스토어
<img width="800" alt="game_stroe_erd" src="https://github.com/user-attachments/assets/4608589b-2476-43ea-b1bb-1b13f171c846">

|테이블|인덱스명|컬럼|용도|
|------|---|---|---|
|game|idx_game_discount_id|game_discount_id|할인 게임 목록 조회시 game 테이블과 game_discount 테이블 조인시 사용|
|game|idx_type|type|필터에서 게임 유형 선택|
|game_discount|idx_discount_price|discount_price|필터에서 금액 범위 선택|
|game_genre|idx_game_id_genre_id|game_id, genre_id|필터에서 장르 선택|

- 할인 게임 목록 조회 쿼리에서 사용되는 인덱스

### 멤버십
<img width="800" alt="game_stroe_erd" src="https://github.com/user-attachments/assets/b7e6c63a-8127-42e7-b277-0d6282c9f687">

### 학습한 것 실습 목록
- 자바 기본기 관련 ~~~
- JPA만을 사용해 프로젝트 ~~~
- 스프링 AOP를 사용해 게임 ~~~
- 레디스를 통한 캐시 ~~~ + 레디스 매니저 제네릭 메서드 적용
- 데이터베이스 정규화, 인덱스(순서), 실행 계획 분석
