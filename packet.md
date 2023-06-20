# 실행 Class : push_man.main.ClientMain

# 대분류 - Client - 전달된 데이터 객체 Type에 따라 분류

## MemberVO - 회원관련 요청 처리
   order 
   0 : 아이디 중복 체크
   1 : 회원가입
   2 : 로그인
   3 : memberNum으로 회원 정보 검색

## ScoerVO - 게임기록 관련 요청 처리
   회원 별 게임 기록 정보 갱신

## RankingVO - Waiting room 랭킹 리스트 관련 요청 처리 - UserInfo 사용자 랭킹 정보

## Integer(스테이지 번호) - 해당 스테이지 랭킹 리스트
   0 : 대기실 목록에 추가하고 갱신
   -1 : 대기실 목록에서 삭제(게임 입장)
   
   1~ 36 : 대기실 스테이지 랭킹 리스트 전달
   stage별 1 ~ 10 위 까지위 회원 랭킹 리스트
   

## MessageVO - Waiting room- 대기실 채팅  // 1:1 채팅
   order 
   0 : 대기실 채팅
   1 : 1:1 채팅 요청 
   2 : 1:1 채팅 요청 받음
   3 : 1:1 채팅
   4 : 채팅 종료
   
   
   