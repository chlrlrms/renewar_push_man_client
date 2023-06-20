package push_man.member;

import push_man.vo.MemberVO;

public interface MemberInterface {
	
	/**
	 * 로그인  <-> 회원가입 화면 전환
	 */
	void setHyperLink();
	/**
	 * login UI 초기화
	 */
	void initLoginUI();
	/**
	 * join UI 초기화
	 */
	void initJoinUI();
	/**
	 * login event 초기화
	 */
	void setLoginEvent();
	/**
	 * join event 초기화
	 */
	void setJoinEvent();
	/**
	 * 아이디 중복체크 UI 변경
	 * @param isChecked
	 * @param result
	 */
	void setJoinIDCheck(boolean isChecked, String result);
	/**
	 * 회원가입 성공 유무
	 * @param isSuccess
	 */
	void setJoinCheck(boolean isSuccess);
	/**
	 * 로그인 성공 여부 check
	 * @param vo
	 */
	void setLoginCheck(MemberVO vo);
	/**
	 * order - 
	 *  0 - 아이디 중복체크 
 		1 - 회원가입 
 		2 - 로그인 
 		3 - 회원 번호로 회원 정보 검색
		4 - 로그아웃 요청 처리
		5 - 회원탈퇴 요청 처리 
	 */
	void receiveData(MemberVO vo);

	
}








