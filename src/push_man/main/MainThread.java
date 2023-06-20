package push_man.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import push_man.game.GameController;
import push_man.member.MemberController;
import push_man.user_info.UserInfoController;
import push_man.vo.MemberVO;
import push_man.vo.MessageVO;
import push_man.vo.RankingVO;
import push_man.vo.ScoreVO;
import push_man.waiting_room.WaitingRoomController;

/**
 * Server에서 전달 받은 데이터를 각 무대 별 화면 제어 Controller로 전달하는 router(발송 담당) Class
 * run() - Server Data Receive
 * sendData() - Server Data Send
 * stopClient() - return resources 
 * 
 */
public class MainThread extends Thread {

	public MemberController memberController;
	public GameController gameController;
	public WaitingRoomController waittingRoomController;
	public UserInfoController userInfoController;
	

	// Server에서 발신한 내용을 Receive
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		
		ObjectInputStream ois = null;
		
		while (true) {
			if (isInterrupted()) {
				break;
			}
			try {
				Object obj = null;
				ois = new ObjectInputStream(new BufferedInputStream(ClientMain.socket.getInputStream()));
				
				if((obj = ois.readObject()) == null) {
					throw new NullPointerException("Server 연결 끊김");
				}
				if (obj instanceof List<?> || obj instanceof MessageVO) {
					// 대기실 정보 요청 처리 - 대기실 chat
					if(obj instanceof List<?> && ((List<?>) obj).isEmpty()) {
						waittingRoomController.receiveData(obj);
						continue;
					}
					if (obj instanceof List<?> && ((List<?>) obj).get(0) instanceof RankingVO && ((RankingVO)((List<?>) obj).get(0)).getOrder() == 2) {
						// 사용자 정보 창 게임 기록 정보 갱신
						userInfoController.receiveData((List<RankingVO>)obj);
					}else {
						// 대기실 랭킹 목록 갱신
						waittingRoomController.receiveData(obj);
					}
				}else if (obj instanceof MemberVO) {
					// 회원관련 요청 처리 결과
					MemberVO vo = (MemberVO) obj;
					switch(vo.getOrder()) {
					case 3 : case 4 : case 5 :
						// 회원 정보 요청 - UserInfo Controller
						userInfoController.receiveData(vo);
						break;
					default :
						// 회원 관련 가입 - 로그인 - 아이디 중복 체크
						memberController.receiveData(vo);
					}
				} else if (obj instanceof ScoreVO) {
					// 기록관련 요청
					gameController.receiveData((ScoreVO) obj);
				}
				
			} catch (Exception e) {
				stopClient();
				break;
			}
		}
	}

	public void sendData(Object o) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new BufferedOutputStream(ClientMain.socket.getOutputStream()));
			oos.writeObject(o);
			oos.flush();
		} catch (IOException e) {
			stopClient();
		}
	}

	// Client Server와 연결 종료
	public void stopClient() {
		this.interrupt();
		if (ClientMain.socket != null && !ClientMain.socket.isClosed()) {
			try {
				ClientMain.socket.close();
			} catch (IOException e) {
			}
		}
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setHeaderText(null);
			alert.setContentText("서버와 연결이 끊겼습니다.");
			alert.showAndWait();
			Platform.exit();
		});
	}
}
