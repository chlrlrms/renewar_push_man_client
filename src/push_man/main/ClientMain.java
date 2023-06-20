package push_man.main;

import java.net.Socket;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClientMain extends Application implements MainInterface{

	/**
	 * 한명의 client당 연결된 소켓 정보는 유일한 자원
	 */
	public static Socket socket;
	/**
	 * Server와 입출력을 담당하는 스레드 class
	 */
	public static MainThread thread;
	
	/**
	 * 메인 로딩 스테이지
	 */
	private Stage primaryStage;
	/**
	 * 접속 IP 정보
	 */
	private String ip;
	
	@Override
	public void start(Stage primaryStage) throws Exception{
		loadFont();
		this.primaryStage = primaryStage;
		this.ip = "10.100.205.205";
		FXMLLoader loader = new FXMLLoader(
			getClass().getResource("/push_man/main/main.fxml")
		);
		Parent root = loader.load();
		Scene scene = new Scene(root);
		// scene 배경색을 투명으로 지정
		scene.setFill(Color.TRANSPARENT);
		primaryStage.setScene(scene);
		// 무대 색상을 투명하게 지정
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		// 무대를 모니터 중앙으로 지정
		primaryStage.centerOnScreen();
		primaryStage.getIcons().add(new Image(getClass().getResource("/system_image/img_push_man.png").toString()));
		primaryStage.show();
		initClient();
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * 로딩 UI 스레드 종료 flag
	 */
	boolean isRun;
	
	/**
	 * 로딩 UI 스레드 
	 */
	Thread labelThread;
	
	@Override
	public void initClient() {
		labelThread = new Thread(()->{
			isRun = true;
			Label label = (Label)primaryStage.getScene().getRoot().lookup(".loading");
			Platform.runLater(()->{
				label.setText("Loading.");
			});
			String[] strs = {"Loading.","Loading..","Loading..."};
			run : while(isRun) {
				for(int i=0; i<strs.length; i++) {
					final int j = i;
					Platform.runLater(()->{
						label.setText(strs[j]);
					});
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						break run;
					}
				}
			}
		});
		labelThread.start();
		
		new Thread(()->{
			try {
				socket = new Socket(ip,8002);
				isRun = false;
				thread = new MainThread();
				thread.setDaemon(true);
				thread.start();
				
				Platform.runLater(()->{
					try {
						showMemberStage();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			} catch (Exception e) {
				Platform.runLater(()->{
					primaryStage.close();
					showAlert("연결실패\n서버와연결할 수 없습니다.\n다시 실행해주세요.");
				});
			}
		}).start();
	}

	@Override
	public void showMemberStage() throws Exception {
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
		primaryStage.close();
	}

	@Override
	public void showAlert(String text) {
		labelThread.interrupt();
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("알림");
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(getClass().getResource("/system_image/img_push_man.png").toString()));
		alert.setHeaderText(text);
		alert.setContentText("확인 : 재시도 \n취소:종료");
		alert.showAndWait();
		if(alert.getResult() == ButtonType.OK) {
			TextInputDialog tid = new TextInputDialog(ip);
			Stage tidStage = (Stage) tid.getDialogPane().getScene().getWindow();
			tidStage.getIcons().add(new Image(getClass().getResource("/system_image/img_push_man.png").toString()));
			tid.setTitle("ip 입력");
			tid.setHeaderText(null);
			tid.setContentText("서버 아이피를 입력해주세요.");
			Optional<String> a = tid.showAndWait();
			a.ifPresent(e->{
				ip = e;
			});
			primaryStage.show();
			initClient();
		}else if(alert.getResult() == ButtonType.CANCEL) {
			Platform.exit();
		}
	}
	
	private void loadFont() {
		try {
			/**
			 * 안티앨리어싱 적용(폰트를 부드럽게)
			 *  Noto Sans KR Light
			 *	Noto Sans KR Black
			 *	Noto Sans KR
			 *	Noto Sans KR Medium
			 */
			// System.setProperty("prism.lcdtext", "false"); // 폰트파일 로드전에 실행
			Font.loadFont(
					Class.forName("push_man.main.ClientMain")
					.getResource("/lib/Noto_Sans_KR/NotoSansKR-Light.otf").toString(), 12);
			Font.loadFont(
					Class.forName("push_man.main.ClientMain")
					.getResource("/lib/Noto_Sans_KR/NotoSansKR-Black.otf").toString(), 12);
			Font.loadFont(
					Class.forName("push_man.main.ClientMain")
					.getResource("/lib/Noto_Sans_KR/NotoSansKR-Bold.otf").toString(), 12);
			Font.loadFont(
					Class.forName("push_man.main.ClientMain")
					.getResource("/lib/Noto_Sans_KR/NotoSansKR-Medium.otf").toString(), 12);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}












