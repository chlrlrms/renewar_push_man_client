package push_man.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 탈퇴한 회원정보를 저장할 Class
 * 서버에서 사용
 */
public class MemberBackUpVO extends MemberVO implements Serializable{
	
	private static final long serialVersionUID = 3459181516966951326L;

	private Date deleteDate;
	
	public MemberBackUpVO() {}
	
	public MemberBackUpVO(int memberNum, String memberName, String memberId, String memberPw, long regdate,
			long loginDate, Date deleteDate) {
		super(memberNum, memberName, memberId, memberPw, regdate, loginDate);
		this.deleteDate = deleteDate;
	}

	public Date getDeleteDate() {
		return deleteDate;
	}

	public void setDeleteDate(Date deleteDate) {
		this.deleteDate = deleteDate;
	}

	@Override
	public String toString() {
		return super.toString()+": MemberBackUpVO [deleteDate=" + deleteDate + "]";
	}
	
}








