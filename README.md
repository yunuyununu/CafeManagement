# ☕ 안드로이드 무인 카페 키오스크 관리시스템

국비 훈련 과정에 진행한 첫 번째 팀 프로젝트입니다.  

* * *

## 📑 목차
[1. 프로젝트 개요](#1-프로젝트-개요)
  - [주제 및 목표](#-주제-및-목표)
  - [사용 기술 및 팀 구성](#-사용-기술-및-팀-구성)
  - [팀원별 구현 기능](#-팀원별-구현-기능)

[2. 프로그램 구조](#2-프로그램-구조)
  - [ER Diagram](#-er-diagram)
  - [Usecase Diagram](#-usecase-diagram)
  - [테이블 명세서](#-테이블-명세서)

[3. 개별 페이지 안내](#3-개별-페이지-안내)
  - [메인화면](#-메인화면)
  - [매출관리](#-매출관리)
  - [재고관리](#-재고관리)
  - [메뉴관리](#-메뉴관리)
  - [회원관리](#-회원관리)

[4. 후기 및 개선점](#4-후기-및-개선점)
  - [프로젝트 후기](#-프로젝트-후기)
  - [개선점](#-개선점)



* * *

## 1. 프로젝트 개요
### 📅 개발 기간
2023.12.18 ~ 2024.1.10 (약 4주)
- 1주차: 프로젝트 기획 및 화면 설계. DB 구축. 파트별 레이아웃 및 핵심 기능 설계.
- 2주차: 파트별 DB 설계 및 구축. 프로그램 화면 및 세부 기능 구현. 1차 통합 및 프레젠테이션 자료 준비.
- 3주차: 기능 테스트 진행 및 2차 통합. 최종 점검 및 프로젝트 완성.



### 📝 주제 및 목표
**[주제]** 무인 카페 키오스크 관리 시스템

**[목표]**
1. 훈련 기간 동안 학습한 내용의 복습 및 적용		
2. 안드로이드 스튜디오에서 제공하는 다양한 기본 위젯 적용과 커스터마이징
3. 데이터베이스 구축 및 SQL문 연습



### 💻 사용 기술 및 팀 구성

**[사용 기술 및 개발 환경]**
- OS : Windows11, Android
- Language : JAVA
- DB : SQLite
- Tools : Android Studio
- Test device : Emulator (Pixel XL API 34)


### 👩‍💻 팀원별 구현 기능

  
![team](https://github.com/jh91019/android/assets/156145497/6b4284d3-80af-4ee0-8dda-05bd896b2aac)


* * *


## 2. 프로그램 구조
### ☑ ER Diagram
![1_erd](https://github.com/jh91019/android/assets/156145497/253c14d9-71ca-4735-ad57-73d6eb8c3876)



### ☑ Usecase Diagram
![usecase](https://github.com/jh91019/android/assets/156145497/cdc8dd35-4770-4026-86c8-5dd79a753bb7)



### ☑ 테이블 명세서
![table_1](https://github.com/jh91019/android/assets/156145497/8b3632db-740c-448a-b563-3493344c975c)


![table_2](https://github.com/jh91019/android/assets/156145497/b24c25bb-728c-4f0f-9eee-f21d04fc54e8)



* * *


## 3. 개별 페이지 안내
#### 🔸 메인화면
![main1](https://github.com/jh91019/android/assets/156145497/dae06cea-67a9-4a5e-8d8d-5788b74c01c8)


![main2](https://github.com/jh91019/android/assets/156145497/2f7e899f-bb05-4a99-b517-b90d8ca8e769)



#### 🔸 매출관리
![order1](https://github.com/jh91019/android/assets/156145497/d2c28609-58f9-44db-879e-c743cc221143)


[매출관리 - 주문내역 조회]
- 땅콩이미지 클릭 시, 기본 셋팅값으로 초기화(전체 주문리스트)
- 주문번호, 날짜 검색 시 해당 주문 내역 리스트
- 없는 주문번호/날짜 검색 시 팝업창


![order2](https://github.com/jh91019/android/assets/156145497/82d374d7-6762-4f3c-b56b-77a48f9c3cf3)


[매출관리 - 일별,월별 조회]
- 날짜로 검색. 상품별 매출목록 리스트로 띄워짐
- 빈칸, 없는날짜 입력 시 팝업창
- 날짜검색 후 리스트 띄워지면 엑셀로 저장 가능
- 해당 엑셀자료 지정한 경로에 저장됨


#### 🔸 재고관리
![product1](https://github.com/jh91019/android/assets/156145497/e8799bf2-3e51-4b29-83e5-baafc56afe76)


![product2](https://github.com/jh91019/android/assets/156145497/5f5d4393-16c5-43e3-a531-67218dacdab7)


![product3](https://github.com/jh91019/android/assets/156145497/89dddd68-7e51-4cfc-804e-71c33a8dd9bd)




#### 🔸 메뉴관리
![menu1](https://github.com/jh91019/android/assets/156145497/7d0d443f-acd8-4c7a-949f-1893308abbe1)


![menu2](https://github.com/jh91019/android/assets/156145497/6e655097-3770-4312-9da9-b65f128a270a)


![menu3](https://github.com/jh91019/android/assets/156145497/3d6919b9-0abc-4e36-9eee-6566258b1a07)


![menu4](https://github.com/jh91019/android/assets/156145497/de950192-f404-4c61-809d-85387d0fcf78)



#### 🔸 회원관리
![member1](https://github.com/jh91019/android/assets/156145497/78f15a13-2373-4863-ae22-7b008550800c)


![member2](https://github.com/jh91019/android/assets/156145497/c71090ec-0e77-47a9-abcc-9678a5c1f025)



* * *


## 4. 후기 및 개선점
### 🖍 프로젝트 후기
- 웹 & 앱에서 각 이용되는 데이터베이스를 사용해 봄으로써 데이터베이스의 종류에 대해 알게 됨
- 기본 제공 위젯이나 어댑터 등을 각자 다양한 방식로 활용해 보며 많은 공부가 되었음
- 파트 분담 후 통합하는 과정에서 서로 간에 충분한 논의나 주석 활용을 통해 더 효율적으로 진행하지 못한 점이 아쉬움으로 남음



### ⚒ 개선점
- 전체 프로젝트 structure 및 각 액티비티에 공통으로 사용되는 메소드를 호출하는 방식 등을 고려한 재구성
- 더 간결하고 깔끔한 코드로 구성하여 가독성 향상 
- 향후 훈련 과정을 통해 학습하게 될 jsp, Spring 등을 활용하여 웹과 연동할 수 있도록 추가 확장
- 관리자 계정 등록 기능 및 사용자 화면 구성하여 연결
