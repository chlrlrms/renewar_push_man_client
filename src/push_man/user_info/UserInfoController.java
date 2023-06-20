package push_man.user_info;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import push_man.main.ClientMain;
import push_man.member.MemberController;
import push_man.vo.MemberVO;
import push_man.vo.RankingVO;

/**
 * 사용자 정보 출력용 Controller
 * 사용자별 랭킹 기록 및 회원정보 출력
 * 자신의 정보 요청 시에는 로그아웃 및 회원탈퇴 기능 제공
 */
public class UserInfoController implements Initializable {

	/**
	 * 개인별 랭킹 기록 출력 테이블 뷰
	 */
	@FXML private TableView<RankingVO> rankingTableView;
	/**
	 * 회원 정보 출력 Label
	 */
	@FXML private Label lblNum, lblName, lblID, lblReg, lblLogin;
	/**
	 * 로그 아웃 및 회원 탈퇴 
	 */
	@FXML private Button logOut, withDraw;
	
	/**
	 * 로그아웃 및 회원 탈퇴 시 
	 * 닫을 스테이지 정보 저장
	 */
	private Stage userInfoStage, waittingRoomStage;
	
	/**
	 * 사용자 정보를 요청한 회원 정보 저장 class
	 */
	private MemberVO vo;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ClientMain.thread.userInfoController = this;
		logOut.setOnAction((e)->{
			vo.setOrder(4);
			vo.setSuccess(false);
			ClientMain.thread.sendData(vo);
		});
		
		withDraw.setOnAction((e)->{
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("회원탈퇴 요청");
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(new Image(getClass().getResource("/system_image/img_push_man.png").toString()));
			alert.setHeaderText("회원 탈퇴 하시겠습니까?");
			alert.setContentText("탈퇴 시 기존 클리어 기록은 모두 사라집니다.");
			alert.showAndWait();
			if(alert.getResult() == ButtonType.OK) {
				vo.setOrder(5);
				vo.setSuccess(false);
				ClientMain.thread.sendData(vo);
			}
		});
	}

	public void setUserInfoStage(Stage userInfoStage, Stage waittingRoomStage) {
		this.userInfoStage = userInfoStage;
		this.waittingRoomStage = waittingRoomStage;
		this.vo = (MemberVO)userInfoStage.getUserData();
		if(vo.getOrder() != 3 && vo.getOrder() != 4 && vo.getOrder() != 5 ) {
			initUserInfo();
		}else {
			// 지정된 사용자 회원 정보 요청
			ClientMain.thread.sendData(vo);
		}
		// 지정된 사용자 랭킹 정보 요청
		RankingVO rv = new RankingVO();
		rv.setOrder(2);
		rv.setNum(vo.getMemberNum());
		ClientMain.thread.sendData(rv);
	}

	/**
	 	// 회원번호
		private int memberNum;
		// 회원 닉네임
		private String memberName;
		// 회원 아이디
		private String memberId;
		// 회원 비밀번호
		private String memberPw;
		// 회원 가입일
		private long regdate;
		// 마지막 로그인 시간
		private long loginDate;
	 */
	public void initUserInfo() {
		lblNum.setText(String.valueOf(vo.getMemberNum()));
		lblName.setText(vo.getMemberName());
		lblID.setText(vo.getMemberId());
		lblReg.setText(vo.getRegdate());
		lblLogin.setText(vo.getLoginDate());
		userInfoStage.setTitle(vo.getMemberName()+"님의 정보입니다.");
		
		if(!MemberController.user.getMemberId().equals(vo.getMemberId())) {
			logOut.setVisible(false);
			withDraw.setVisible(false);
		}
	}
	
	/**
	 * 로그 아웃 및 회원 탈퇴 시 정보 초기화 및
	 * 회원(로그인,회원가입)페이지로 이동
	 */
	public void showMemberStage(){
		try {
			MemberController.user = null;
			FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/push_man/member/Member.fxml")
			);
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Stage stage = new Stage(StageStyle.DECORATED);
			stage.setScene(scene);
			stage.setTitle("Push Man");
			stage.getIcons().add(new Image(getClass().getResource("/system_image/img_push_man.png").toString()));
			stage.setResizable(false);
			stage.show();
		} catch (IOException e) {}
	}

	@SuppressWarnings("unchecked")
	public void receiveData(Object o) {
		Platform.runLater(()->{
			if(o instanceof MemberVO) {
				this.vo = (MemberVO)o;
				switch(vo.getOrder()) {
				// 로그아웃 또는 회원 탈퇴 일 경우
				case 4 : case 5 :
					this.vo = null;
					userInfoStage.close();
					waittingRoomStage.close();
					showMemberStage();
					break;
				case 3 :
					initUserInfo();
				}
			}else{
				initRankingTableView((List<RankingVO>)o);
			}
		});
	}
	
	/**
	 * 랭킹 테이블 초기화
	 * @param list - RankingVO
	 */
	public void initRankingTableView(List<RankingVO> list) {
		// public field만 가져옴
		// serialVersionID 제외
		if(list.get(0).getRanking() == 0) list.clear(); 
		ObservableList<TableColumn<RankingVO, ?>> observList = rankingTableView.getColumns();
		for(TableColumn<RankingVO, ?> tc : observList) {
			tc.setResizable(false);
			tc.setStyle("-fx-alignment:center");
		}
		TableColumn<RankingVO, ?> column0 = observList.get(0);
		column0.setCellValueFactory(new PropertyValueFactory<>("stage"));
		
		TableColumn<RankingVO, ?> column1 = observList.get(1);
		column1.setCellValueFactory(new PropertyValueFactory<>("ranking"));
		
		TableColumn<RankingVO, ?> column2 = observList.get(2);
		column2.setCellValueFactory(new PropertyValueFactory<>("score"));
		
		TableColumn<RankingVO, ?> column3 = observList.get(3);
		column3.setCellValueFactory(new PropertyValueFactory<>("regdate"));
		
		Label label = new Label("아직 등록된 기록이 없습니다.");
		label.setStyle("-fx-text-fill:BLACK;-fx-font-size:30;-fx-font-weight:bold;");
		rankingTableView.setPlaceholder(label);
		rankingTableView.setItems(FXCollections.observableArrayList(list));
	}
}
