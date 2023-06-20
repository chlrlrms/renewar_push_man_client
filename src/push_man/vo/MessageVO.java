package push_man.vo;

import java.io.Serializable;

/**
 * 대기실 및 1:1 채팅 정보 처리용 class
 */
public class MessageVO implements Serializable{

	private static final long serialVersionUID = 3598913335109274202L;

	/**
     메뉴 번호 - 0  대기실 채팅
	         - 1  1:1 채팅 요청
	         - 2  1:1 채팅 요청 받음
	         - 3  1:1 채팅 진행
	         - 4  1:1 채팅 종료
    */ 
	private int order;
	// 발신자
	private int from;
	// 수신자
	private int to;
		// 메세지
	private String message;
	
	public MessageVO() {}
	
	// 대기실 broadCast message 용
	public MessageVO(int order, int from, String message) {
		this.order = order;
		this.from = from;
		this.message = message;
	}
	
	// 1:1 채팅
	public MessageVO(int order, int from, int to, String message) {
			super();
			this.order = order;
			this.from = from;
			this.to = to;
			this.message = message;
		}
	
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public int getFrom() {
		return from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
	public int getTo() {
		return to;
	}
	public void setTo(int to) {
		this.to = to;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "MessageVO [order=" + order + ", from=" + from + ", to=" + to + ", message=" + message + "]";
	}
	
	
}
