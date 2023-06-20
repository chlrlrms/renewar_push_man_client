package push_man.vo;

import java.io.Serializable;

public class ScoreVO implements Serializable{
	
	private static final long serialVersionUID = -8177391921612454810L;
	
	private int stage;			// 게임 스테이지
	private long score;			// 게임 clear 시간 을 점수로 등록
	private long regDate;		// 게임 clear 한 시간
	private int memberNum;		// clear한 회원 번호
	private int ranking;		// 순위

	public ScoreVO() {}
	
	// stage 별 clear 시간을 저장할 생성자
	public ScoreVO(int stage, long score, long regDate, int memberNum) {
		this.stage = stage;
		this.score = score;
		this.regDate = regDate;
		this.memberNum = memberNum;
	}

	// 등록된  clear 시간으로 랭킹 정보를 전달할 생성자
	public ScoreVO(int stage, long score, long regDate, int memberNum, int ranking) {
		this.stage = stage;
		this.score = score;
		this.regDate = regDate;
		this.memberNum = memberNum;
		this.ranking = ranking;
	}

	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public long getScore() {
		return score;
	}
	public void setScore(long score) {
		this.score = score;
	}
	public long getRegDate() {
		return regDate;
	}
	public void setRegDate(long regDate) {
		this.regDate = regDate;
	}
	public int getMemberNum() {
		return memberNum;
	}
	public void setMemberNum(int memberNum) {
		this.memberNum = memberNum;
	}
	public int getRanking() {
		return ranking;
	}
	public void setRanking(int ranking) {
		this.ranking = ranking;
	}
	@Override
	public String toString() {
		return "ScoreVO [stage=" + stage + ",score=" + score + ", regDate=" + regDate + ", memberNum="
				+ memberNum + ", ranking=" + ranking + "]";
	}
		
}
