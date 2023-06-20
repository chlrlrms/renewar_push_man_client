package push_man.member;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import push_man.main.ClientMain;
import push_man.vo.MemberVO;

public class MemberController implements Initializable , MemberInterface{

	/**
	 * 회원 가입 페이지에서 간단하게 보유 줄 웹 화면 <br/>
	 * YOUTUBE 영상 출력
	 */
	@FXML private WebView webView;
	/**
	 * YOUTUBE 영상 로딩 시 보여줄 이미지
	 */
	@FXML private ImageView imageView;
	
	/**
	 * 회원가입 <-> 로그인 <br/>
	 * 페이지 정보 저장용 Panel 
	 */
	@FXML private AnchorPane joinAnchor, loginAnchor;
	
	/**
	 * 로그인, 회원가입 시 아이디 작성 공간 <br/>
	 * 회원가입 시 회원 이름  작성 공간
	 */
	@FXML private TextField loginID, joinID, joinName;
	
	/**
	 * 로그인 시 비밀번호 작성 공간 <br/>
	 * 회원가입시 비밀번 호 및 비밀번호 확인 작성 공간
	 * 
	 */
	@FXML private PasswordField loginPW, joinPW, joinRePW;
	
	/**
	 * 로그인 요청 및 회원 가입 요청 버튼
	 */
	@FXML private Button btnLogin, btnJoin;
	
	/**
	 *  로그인  <-> 회원 가입 페이지 이동 link
	 */
	@FXML private Hyperlink loginLinkBtn, joinLinkBtn;
	
	/**
	 * 회원가입 시 <b>아이디</b> 체크 후 결과를 출력할 Label <br/>
	 * 회원가입 시 <b>닉네임</b> 체크 후 결과를 출력할 Label
	 */
	@FXML private Label checkID,checkINick;
	
	/**
	 * 중복아이디 체크 완료 여부 저장용 
	 */
	boolean isJoin;
	
	/**
	 * 로그인 완료 시 추가 될 사용자 정보 <br/>
	 * 모든 스테이지에 공유 
	 */
	public static MemberVO user;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		/*
		try {
			
			String fileName = "C:\\Temp\\file\\세나2길드.gif";
			String ext = fileName.substring(fileName.lastIndexOf(".")+1);
			BufferedImage image = ImageIO.read(new File(fileName));
			byte[] bytes = ImageUtils.toByteArray(image, ext);
			
			image = ImageUtils.toBufferedImage(bytes);
			imageView.setImage(new Image(new ByteArrayInputStream(bytes)));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		ClientMain.thread.memberController = this;
		
		WebEngine wg = webView.getEngine();
		wg.load("https://www.youtube.com/embed/v49p_vhEQt0");
		wg.getLoadWorker().stateProperty()
		.addListener(new ChangeListener<State>() {
			@Override
			public void changed(
					ObservableValue<? extends State> observable, 
					State oldValue, 
					State newValue) {
				// WebView Load 완료 상태
				if(newValue.equals(State.SUCCEEDED)) {
					imageView.setVisible(false);
				}
			}
		});
		setHyperLink();
		setJoinEvent();
		setLoginEvent();
		// System.out.println("완료");
	}

	@Override
	public void setHyperLink() {
		joinLinkBtn.setOnAction(e->{
			Platform.runLater(()->{
				loginAnchor.setVisible(false);
				loginAnchor.setDisable(true);
				joinAnchor.setVisible(true);
				joinAnchor.setDisable(false);
				joinID.requestFocus();
			});
		});
		
		loginLinkBtn.setOnAction(e->{
			loginAnchor.setVisible(true);
			loginAnchor.setDisable(false);
			joinAnchor.setVisible(false);
			joinAnchor.setDisable(true);
			loginID.requestFocus();
		});
	}

	@Override
	public void initLoginUI() {
		Platform.runLater(()->{
			loginID.clear();
			loginPW.clear();
			loginID.requestFocus();
		});
	}

	@Override
	public void initJoinUI() {
		Platform.runLater(()->{
			joinID.clear();
			joinPW.clear();
			joinName.clear();
			joinRePW.clear();
			joinID.requestFocus();
		});
	}

	@Override
	public void setLoginEvent() {
		loginID.setOnKeyPressed(event->{
			if(event.getCode() == KeyCode.ENTER) {
				loginPW.requestFocus();
			}
		});
		// 로그인 패스워드 작성란에 Enter 키가 눌러지면
		// btnLogin Button action event 실행
		loginPW.setOnKeyPressed(event->{
			if(event.getCode() == KeyCode.ENTER) {
				btnLogin.fire();
			}
		});
		
		btnLogin.setOnAction(event->{
			String id = loginID.getText().trim();
			String pw = loginPW.getText().trim();
			
			if(id.equals("")) {
				loginID.requestFocus();
			}else if(pw.equals("")) {
				loginPW.requestFocus();
			}else {
				MemberVO member = new MemberVO(id,pw);
				member.setOrder(2);
				ClientMain.thread.sendData(member);
			}
		});
		
	}

	@Override
	public void setJoinEvent() {
		joinID.textProperty().addListener((obj,o,n)->{
			String text = joinID.getText().trim();
			if(!Pattern.matches("^[A-Za-z0-9]{6,16}$", text)) {
				String style = "-fx-text-fill:red;-fx-font-size:6;";
				String result = "영문&숫자로만 6~16자 이내";
				checkID.setStyle(style);
				checkID.setText(result);
				setJoinIDCheck(false,result);
			}else{
				MemberVO member = new MemberVO(text);
				// 아이디 중복 체크
				member.setOrder(0);
				ClientMain.thread.sendData(member);
			}
		});
		
		joinID.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.ENTER && isJoin) {
				joinName.requestFocus();
			}
		});
		
		joinName.textProperty().addListener((o,b,text)->{
			String nick = text.trim();
			if(!Pattern.matches("^[가-힣]{2,4}$",nick)) {
				checkINick.setStyle("-fx-text-fill:red;-fx-font-size:12;");
				checkINick.setText("한글 2~8자 사이");
			}else {
				checkINick.setStyle("-fx-text-fill:green;-fx-font-size:12;");
				checkINick.setText("사용가능합니다.");
			}
		});
		
		joinName.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.ENTER) {
				joinPW.requestFocus();
			}
		});
		
		joinPW.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.ENTER) {
				joinRePW.requestFocus();
			}
		});
		
		joinRePW.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.ENTER) {
				btnJoin.fire();
			}
		});
		
		btnJoin.setOnAction(event->{
			String id = joinID.getText().trim();
			String pw = joinPW.getText().trim();
			String repw = joinRePW.getText().trim();
			String nick = joinName.getText().trim();
			
			if(id.equals("") || !isJoin) {
				joinID.clear();
				joinID.requestFocus();
			}else if(!Pattern.matches("^[가-힣]{2,8}$",nick)) {
				checkINick.setStyle("-fx-text-fill:red;-fx-font-size:12;");
				checkINick.setText("닉네임은 한글 2~8자 사이만 가능 합니다.");
				joinName.clear();
				joinName.requestFocus();
			}else if(pw.equals("") || repw.equals("") || !pw.equals(repw)) {
				joinPW.clear();
				joinRePW.clear();
				joinPW.requestFocus();
				checkID.setText("비밀번호가 일치하지 않습니다.");
			}else {
				MemberVO member = new MemberVO(nick,id,pw);
				member.setOrder(1);
				ClientMain.thread.sendData(member);
			}
		});
	}

	// 아이디 중복체크 UI 변경
	@Override
	public void setJoinIDCheck(boolean isChecked,String result) {
		Platform.runLater(()->{
			String style = isChecked ? "-fx-text-fill:green;" : "-fx-text-fill:red;" ;
			String text = isChecked ? "사용가능합니다." : result == null ? "사용할 수 없는 아이디입니다." : result;
			checkID.setStyle(style);
			checkID.setText(text);
			if(!isChecked) {
				joinID.requestFocus();
				joinID.selectEnd();
			}
		});
	}

	@Override
	public void setJoinCheck(boolean isSuccess) {
		if(isSuccess) {
			// System.out.println("회원가입 성공");
			joinAnchor.setVisible(false);
			joinAnchor.setDisable(true);
			loginAnchor.setVisible(true);
			loginAnchor.setDisable(false);
			initLoginUI();
		}else {
			// System.out.println("회원가입 실패");
			initJoinUI();
		}
	}

	@Override
	public void setLoginCheck(MemberVO vo) {
		if(vo.isSuccess()) {
			// System.out.println("로그인 성공");
			Platform.runLater(()->{
				// 로그인 완료된 사용자 정보 저장
				user = vo;
				// 대기실로 이동
				showWaitingRoom();
			});
		}else {
			// System.out.println("로그인 실패");
			// textField 초기화
			Platform.runLater(()->{
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("로그인 요청 처리 실패");
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().add(new Image(getClass().getResource("/system_image/img_push_man.png").toString()));
				alert.setHeaderText(null);
				alert.setContentText("로그인 요청 처리 실패\n아이디와 비밀번호를 확인해 주세요.");
				alert.show();
			});
			initLoginUI();
		}
	}

	@Override
	public void receiveData(MemberVO vo) {
		// 0  아이디 중복 체크  1 회원가입 2 로그인
		switch(vo.getOrder()) {
		case 0 :
			// System.out.println("아이디 중복 체크");
			isJoin = vo.isSuccess();
			setJoinIDCheck(isJoin,"이미 사용중인 아이디입니다.");
			break;
		case 1 : 
			// System.out.println("회원가입 요청 처리 결과");
			if(!vo.isSuccess()) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("회원가입 처리 실패");
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().add(new Image(getClass().getResource("/system_image/img_push_man.png").toString()));
				alert.setHeaderText(null);
				alert.setContentText("회원가입 실패\n회원가입정보를 확인해주세요.");
				alert.showAndWait();
			}
			setJoinCheck(vo.isSuccess());
			break;
		case 2 : 
			// System.out.println("로그인 요청 처리 결과");
			setLoginCheck(vo);
			break;
		}
	}

	// 대기실 오픈
	public void showWaitingRoom() {
		try {
			FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/push_man/waiting_room/Waiting_room.fxml")
			);
			Parent root = loader.load();
			Stage stage = new Stage();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle(user.getMemberName()+"님 반갑습니다.");
			stage.setResizable(false);
			scene.getStylesheets().add(
					getClass().getResource("/push_man/waiting_room/Room.css").toExternalForm()
				);
			stage.getIcons().add(new Image(getClass().getResource("/system_image/img_push_man.png").toString()));
			stage.show();
			Stage memberStage = (Stage)checkID.getScene().getWindow();
			memberStage.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}









