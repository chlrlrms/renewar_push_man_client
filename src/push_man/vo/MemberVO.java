package push_man.vo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 회원정보를 저장하고 통신에 사용할 Class
 */
public class MemberVO implements Serializable{
	
	private static final long serialVersionUID = 3459181516966951326L;
	
	/**
		명령 번호
		0 - 아이디 중복 체크
		1 - 회원가입 요청
		2 - 로그인 요청 - 중복 로그인 처리(기존 연결 해제)
		3 - 회원 번호로 회원 정보 검색
		4 - 로그아웃 요청 처리
		5 - 회원탈퇴 요청 처리
	*/
	private int order;	
	// 회원번호
	private int memberNum;
	// 회원 닉네임
	private String memberName;
	// 회원 아이디 
	private String memberId; 
	// 회원 비밀번호  
	private String memberPw;
	// 전화번호 String memberPhone;
	private String memberPhone;
	// 회원 가입일
	private long regdate;
	// 마지막 로그인 시간
	private long loginDate;
	// 명령에 따른 요청 처리 성공 여부
	private boolean success;
	
	public MemberVO() {}

	// 중복 아이디 체크용
	public MemberVO(String memberId) {
		this.memberId = memberId;
	}
	
	// 회원 로그인 및 일치정보 확인용 생성자
	public MemberVO(String memberId, String memberPw) {
		this.memberId = memberId;
		this.memberPw = memberPw;
	}

	// 회원가입용 생성자
	public MemberVO(String memberName, String memberId, String memberPw) {
		this.memberName = memberName;
		this.memberId = memberId;
		this.memberPw = memberPw;
	}
	
	// 모든 회원 정보를 저장할 생성자
	public MemberVO(int memberNum, String memberName, String memberId, String memberPw, long regdate,long loginDate) {
		this.memberNum = memberNum;
		this.memberName = memberName;
		this.memberId = memberId;
		this.memberPw = memberPw;
		this.regdate = regdate;
		this.loginDate = loginDate;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getMemberNum() {
		return memberNum;
	}

	public void setMemberNum(int memberNum) {
		this.memberNum = memberNum;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemberPw() {
		return memberPw;
	}

	public void setMemberPw(String memberPw) {
		this.memberPw = memberPw;
	}
	
	public String getMemberPhone() {
		return memberPhone;
	}

	public void setMemberPhone(String memberPhone) {
		this.memberPhone = memberPhone;
	}

	public String getRegdate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(this.regdate));
	}

	public void setRegdate(long regdate) {
		this.regdate = regdate;
	}
	
	public String getLoginDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(this.loginDate));
	}

	public void setLoginDate(long loginDate) {
		this.loginDate = loginDate;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MemberVO) {
			MemberVO m = (MemberVO)obj;
			if(this.memberId.equals(m.getMemberId())
				&&
				this.memberPw.equals(m.getMemberPw())
				) {
				// id 와 pw가 일치하면 동일한 객체로 확인
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "MemberVO [order=" + order + ", memberNum=" + memberNum + ", memberName=" + memberName + ", memberId="
				+ memberId + ", memberPw=" + memberPw + ", memberPhone=" + memberPhone + ", regdate=" + getRegdate()
				+ ", loginDate=" + getLoginDate() + ", success=" + success + "]";
	}

}








