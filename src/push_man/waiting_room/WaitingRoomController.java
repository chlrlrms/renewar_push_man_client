package push_man.waiting_room;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Vector;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import push_man.game.GameController;
import push_man.game.GameInterface;
import push_man.main.ClientMain;
import push_man.member.MemberController;
import push_man.user_info.UserInfoController;
import push_man.vo.MemberVO;
import push_man.vo.MessageVO;
import push_man.vo.RankingVO;

/**
 * 게임시작을 위해 준비하는 대기실 화면
 * - 사용자 정보 및 게임 랭킹 정보 제공
 * - 사용자별 1:1 채팅 기능 제공 - 게임 시작 하더라도 유지
 */
public class WaitingRoomController implements Initializable {

	/**
	 * 대기실 사용자 목록 
	 */
	@FXML private TableView<MemberVO> userList;
	/**
	 * 전체 사용자 게임스테이지 별 랭킹 정보 목록
	 */
	@FXML private TableView<RankingVO> rankingList;
	/**
	 * 선택 가능한 스테이지 목록 ComboBox로 표현
	 */
	@FXML private ComboBox<Integer> stageBox;
	/**
	 * 대기실 채팅 입력 창
	 */
	@FXML private TextField inputText;
	/**
	 * 게임 시작 버튼
	 */
	@FXML private Button btnStart;
	/**
	 * 대기실 채팅 전송 버튼
	 */
	@FXML private Button btnSend;
	/**
	 * 대기실 채팅 출력 영역
	 */
	@FXML private TextArea chatArea;
	
	/**
	 * 현재 사용자와 대화중인 1:1 채팅 방 List
	 */
	private List<Stage> chatList;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		/**
		 * Server에서 전달 받은 데이터를 대기실 Controller에서 전달 받을 수 있도록 초기화
		 */
		ClientMain.thread.waittingRoomController = this;
		
		/**
		 * 1:1 채팅 룸 리스트 초기화
		 */
		chatList = new Vector<>();
		
		/**
		 * 현재 랭킹 정보를 1번 스테이지 정보로 초기화
		 */
		initComboBox(1);
		initRankingTableView(1);
		
		/**
		 * 대기실 사용자 리스트 정보 초기화
		 */
		initUserListTableView();
		
		/**
		 * 각 이벤트 별 기능 초기화
		 */
		Platform.runLater(()->{
			inputText.requestFocus();
		});
		
		/**
		 * 게임 시작 
		 * 게임룸으로 입장 시 대기실 목록에서 삭제
		 */
		btnStart.setOnAction(event->{
			Platform.runLater(()->{
				// 대기실에서 삭제
				ClientMain.thread.sendData(-1);
				showGameRoom();
			});
		});
		
		/**
		 * 채팅 입력창에서 ENTER key 입력 시 메세지 전송 
		 */
		inputText.setOnKeyPressed(event->{
			if(event.getCode().equals(KeyCode.ENTER)) {
				btnSend.fire();
			}
		});
		
		/**
		 * send 버튼 event 처리
		 * 대기실에 있는 사용자 끼리만 채팅
		 */
		btnSend.setOnAction(event->{
			ClientMain.thread.sendData(new MessageVO(0,MemberController.user.getMemberNum(),inputText.getText()));
			inputText.clear();
			inputText.requestFocus();
		});
		
	}
	
	/**
	 * 대기실 사용자 목록 정보 갱신 및 우클릭 Context 메뉴 정보 추가
	 * - ContextMenu 
	 *   1. 내 정보 , 자신의 회원 정보 및 스테이지 별 기록 출력
	 *   2. 1:1 채팅, 자신을 제외한 회원 선택 시 개인간 채팅(1:1)채팅 구현
	 */
	private void initUserListTableView() {
		TableColumn<MemberVO, String> columnName = new TableColumn<>("닉네임");
		columnName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
		TableColumn<MemberVO, String> columnId = new TableColumn<>("아이디");
		columnId.setCellValueFactory(new PropertyValueFactory<>("memberId"));
		userList.getColumns().add(0,columnName);
		userList.getColumns().add(1,columnId);
		for(TableColumn<MemberVO,?> tc : userList.getColumns()) {
			tc.setStyle("-fx-alignment:center;");
			tc.setPrefWidth(100);
		}
		
		userList.setOnMouseClicked(event->{
			if(event.getButton() == MouseButton.SECONDARY) {
				MemberVO vo = userList.getSelectionModel().getSelectedItem();
				if(vo == null) {
					return;
				}
				ContextMenu cm = new ContextMenu();
				cm.setAutoHide(true);
				if(!vo.getMemberId().equals(MemberController.user.getMemberId())) {
					MenuItem menu1 = new MenuItem("상세정보");
					ImageView imageView = new ImageView();
					imageView.setFitWidth(20);
					imageView.setPreserveRatio(true);
					imageView.setImage(new Image(getClass().getResource("/system_image/img_push_man.png").toString()));
					menu1.setGraphic(imageView);
					menu1.setOnAction(e->{
						showUserInfo(vo);
					});
					MenuItem menu2 = new MenuItem("1:1 채팅");
					menu2.setOnAction(e->{
						// 내가 채팅 열기
						MessageVO msg = new MessageVO();
						msg.setOrder(1);
						msg.setFrom(vo.getMemberNum());
						msg.setTo(MemberController.user.getMemberNum());
						showOneOnOneChatRoom(msg);
						
					});
					cm.getItems().addAll(menu1,menu2);
					cm.getItems().forEach(a->{
						a.setStyle("-fx-padding:5 50 5 5");
					});
				}else {
					MenuItem menu1 = new MenuItem("내 정보");
					ImageView imageView = new ImageView();
					imageView.setFitWidth(20);
					imageView.setPreserveRatio(true);
					imageView.setImage(new Image(getClass().getResource("/system_image/img_push_man.png").toString()));
					menu1.setGraphic(imageView);
					menu1.setOnAction(e->{
						showUserInfo(MemberController.user);
					});
					cm.getItems().addAll(menu1);
					cm.getItems().forEach(a->{
						a.setStyle("-fx-padding:5 50 5 5");
					});
				}
				cm.show(userList.getScene().getWindow(),event.getScreenX(),event.getScreenY());
			}
		});
		
		/**
		 * Server를 통해 대기실 전체 사용자에게 사용자 추가 및 목록 요청
		 */
		ClientMain.thread.sendData(0);
	}
	
	/**
	 * 사용자 정보 창 
	 * 랭킹 기록 및 개인 정보
	 * 로그인 한 사용자는 로그아웃 및 회원 탈퇴
	 */
	public void showUserInfo(MemberVO member) {
		FXMLLoader loader = new FXMLLoader(
			getClass().getResource("/push_man/user_info/UserInfo.fxml")
		); 
		Parent root = null;
		try {
			root = loader.load();
		} catch (IOException e) {}
		Stage primary = (Stage)inputText.getScene().getWindow();
		double x = primary.getX();
		Stage stage = new Stage();
		stage.setX(x+primary.getWidth());
		stage.setY(primary.getY());
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(primary);
		stage.setScene(new Scene(root));
		stage.setTitle(member.getMemberName()+"님의 정보");
		stage.getIcons().add(new Image(getClass().getResource("/system_image/img_push_man.png").toString()));
		stage.setResizable(false);
		stage.setUserData(member);
		UserInfoController cont = loader.getController();
		cont.setUserInfoStage(stage,(Stage)btnStart.getScene().getWindow());
		stage.show();
	}

	/**
	 * 1:1 채팅 창 오픈
	 */
	public void showOneOnOneChatRoom(MessageVO vo) {
		// chat list에서 채팅 요청이 전달된 1:1 채팅 방이 존재하는지 검색
		Optional<Stage> result = chatList.parallelStream().filter(
			s ->((MemberVO)s.getUserData()).getMemberNum() == vo.getFrom()
		).findAny();
		
		// 이미 해당 사용자와 채팅방이 존재 하면... 기존 방에 채팅 추가
		if(vo.getOrder() == 1 
				&& 
			result.isPresent() 
				&& 
			((MemberVO)result.get().getUserData()).getMemberNum() == vo.getFrom()) {
			// 새로운 1:1 채팅방을 생성 할때 이미 해당 번호로 사용자가 존재 한다고 하면 기존 채팅방에 포커스
			Stage requestStage = result.get();
			requestStage.requestFocus();
			return;
		}
		
		if(vo.getOrder() == 4 && !result.isPresent()) {
			return;
		}
		
		if((vo.getOrder() == 3 || vo.getOrder() == 4) && result.isPresent()) {
			Stage requestStage = result.get();
			Parent root = requestStage.getScene().getRoot();
			TextArea oneOnOneArea = (TextArea)root.lookup("#oneOnOneArea");
			TextField message = (TextField)root.lookup("#message");
			Button btnSend = (Button)root.lookup("#btnSend");
			MemberVO s = (MemberVO)requestStage.getUserData();
			Platform.runLater(()->{
				oneOnOneArea.appendText(s.getMemberName() + " : " + vo.getMessage() + "\n");
			});
			if(vo.getOrder() == 4){
				message.setDisable(true);
				btnSend.setDisable(true);
			}
			requestStage.requestFocus();
		}
		
		
		if((vo.getOrder() == 1 || vo.getOrder() == 2) && !result.isPresent()){
			// 기존에 채팅 방이 존재하지 않으면 오픈
			ObservableList<MemberVO> memberList = userList.getItems();
			Optional<MemberVO> res = memberList.parallelStream().filter(
					m -> m.getMemberNum() == vo.getFrom()
			).findAny();
			
			// 기존에 채팅 방이 존재하지 않고 대기실에 사용자가 존재할 때 1:1 채팅 오픈
			res.ifPresent(member->{
				
				Stage stage = new Stage();
				FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/push_man/waiting_room/OnoOnOneChat.fxml")
				);
				Parent root = null;
				
				try {root = loader.load();} catch (IOException e1) {}
				
				int mNum = MemberController.user.getMemberNum();
				
				TextArea oneOnOneArea = (TextArea)root.lookup("#oneOnOneArea");
				oneOnOneArea.setFocusTraversable(false);
				
				TextField message = (TextField)root.lookup("#message");
				message.requestFocus();
				
				Button btnSend = (Button)root.lookup("#btnSend");
				
				btnSend.setOnAction(e->{
					String msg = message.getText();
					oneOnOneArea.appendText("나 : "+msg + "\n");
					MessageVO m = new MessageVO(3,mNum,member.getMemberNum(),msg);
					ClientMain.thread.sendData(m);
					message.clear();
					message.requestFocus();
				});
				
				message.setOnKeyPressed(event->{
					if(event.getCode() == KeyCode.ENTER) {
						btnSend.fire();
					}
				});
				
				stage.setOnCloseRequest(e->{
					chatList.remove(stage);
					ClientMain.thread.sendData(new MessageVO(4,mNum,member.getMemberNum(),"1:1 채팅을 종료 하였습니다."));
				});
				
				stage.setScene(new Scene(root));
				stage.setTitle(member.getMemberName()+"님과의 채팅");
				stage.setUserData(member);
				stage.getIcons().add(new Image(getClass().getResource("/system_image/img_push_man.png").toString()));
				stage.show();
				chatList.add(stage);
				
				if(vo.getOrder() == 1) {
					ClientMain.thread.sendData(new MessageVO(2,mNum,member.getMemberNum(),"1:1 채팅을 요청 하였습니다."));
				}else {
					oneOnOneArea.appendText(member.getMemberName()+" : "+vo.getMessage() + "\n");
				}
			});
		}
	}

	/**
	 * 스테이지 콤보 박스 초기화
	 */
	public void initComboBox(Integer mGameLevelNum) {
		ObservableList<Integer> list = FXCollections.observableArrayList();
		for (int i = 1; i <= GameInterface.MAX_GAME_LEVEL_NUM; i++) {
			list.add(i);
		}
		stageBox.setItems(list);
		if(mGameLevelNum != null) {
			stageBox.getSelectionModel().select(mGameLevelNum-1);
		}else {
			stageBox.getSelectionModel().selectFirst();
		}
		
		stageBox.getSelectionModel().selectedItemProperty().addListener((o, o1, value) -> {
			// stage 번호 서버로 전송 시 해당 스테이지 랭킹 정보 List로 반환
			// Object Type Integer
			ClientMain.thread.sendData(value);
		});
	}

	/**
	 * 랭킹 테이블 초기화
	 * @param stageNum - 스페이지 별 랭킹
	 */
	public void initRankingTableView(int stageNum) {
		// public field만 가져옴
		// serialVersionID 제외
		rankingList.getColumns().clear();
		Field[] fields = RankingVO.class.getFields();
		for (int i = 0; i < fields.length; i++) {
			TableColumn<RankingVO, ?> column = new TableColumn<>(fields[i].getName());
			column.setCellValueFactory(new PropertyValueFactory<>(fields[i].getName()));
			column.setResizable(false);
			column.setStyle("-fx-alignment:center");
			column.setPrefWidth(70);
			// 마지막 -- 클리어 시간
			if (i == fields.length - 1) {
				column.setPrefWidth(200);
				// Score
			} else if (i == fields.length - 2) {
				column.setPrefWidth(100);
			}
			rankingList.getColumns().add(column);
			Label label = new Label("아직 등록된 기록이 없습니다.\n지금 도전하세요!");
			label.setStyle("-fx-text-fill:WHITE;-fx-font-size:30;-fx-font-weight:bold;");
			rankingList.setPlaceholder(label);
		}
		
		rankingList.setOnMouseClicked(event->{
			if(event.getClickCount() == 2) {
				RankingVO lankingVO = rankingList.getSelectionModel().getSelectedItem();
				if(lankingVO != null){
					MemberVO member = new MemberVO();
					member.setOrder(3);
					member.setMemberNum(lankingVO.getNum());
					showUserInfo(member);
				}
			}
		});
		ClientMain.thread.sendData(stageNum);
	}

	@SuppressWarnings("unchecked")
	public void receiveData(Object obj) {
		if (obj instanceof List<?>) {
			List<?> temp = (List<?>) obj;
			if (temp.isEmpty()) {
				rankingList.setItems(FXCollections.observableArrayList());
				return;
			}
			Object o = temp.get(0);
			// 대기실 랭킹 리스트를 스테이지별 갱신
			if (o instanceof RankingVO) {
				int stageNum = stageBox.getSelectionModel().selectedItemProperty().get();
				List<RankingVO> waitList = (ArrayList<RankingVO>) obj;
				if(waitList.get(0).getStage() == stageNum) {
					rankingList.setItems(FXCollections.observableArrayList(waitList));
				}
			// 대기실 목록 갱신
			}else if(o instanceof MemberVO) {
				userList.setItems(FXCollections.observableArrayList((ArrayList<MemberVO>) obj));
			}
		}else if(obj instanceof MessageVO) {
			MessageVO msg = (MessageVO)obj;
			switch(msg.getOrder()) {
				case 0:
					// 대기실 채팅 
					appendText(msg.getMessage());
					break;
				case 1 : case 2 : case 3 : case 4 :
					// 1:1 채팅 
					Platform.runLater(()->{
						showOneOnOneChatRoom(msg);	
					});
					break;
			}
		}
	}
	
	/**
	 * 대기실 에서 게임 룸 입장 
	 * ComboBox에서 선택된 게임 스테이지 오픈
	 */
	public void showGameRoom() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/push_man/game/Game.fxml")
			);
			Parent root = loader.load();
			Stage stage = new Stage();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			Integer num = stageBox.getSelectionModel().getSelectedItem();
			stage.setTitle(MemberController.user.getMemberName()+"님 반갑습니다.- "+num+" 레벨");
			stage.setResizable(false);
			GameController controller = (GameController)loader.getController();
			controller.setLevel(num);
			stage.setOnCloseRequest(event->{
				controller.showWaittingRoom();
			});
			stage.getIcons().add(new Image(getClass().getResource("/system_image/img_push_man.png").toString()));
			stage.show();
			Stage waittingRoomStage = (Stage)btnStart.getScene().getWindow();
			waittingRoomStage.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * 대기실 채팅 - TextArea 출력 
	 * @param 출력할 메세지 문자열
	 */
	public void appendText(String text) {
		Platform.runLater(()->{
			chatArea.appendText(text+"\n");
		});
	}

	/**
	 * 게임룸에서 나가기 선택 시 현재 스테이지 기록 보여주기
	 * @param mGameLevelNum - 게임 레벨 번호
	 */
	public void setStage(int mGameLevelNum) {
		initComboBox(mGameLevelNum);
		initRankingTableView(mGameLevelNum);
	}
	
}





