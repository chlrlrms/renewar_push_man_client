package push_man.game;

import java.awt.Point;

import javafx.scene.input.KeyCode;
import push_man.vo.ScoreVO;

public interface GameInterface {
	
	int COUNT_SCREEN_IMAGE_ROW = 12;	// 화면 행 개수
	int COUNT_SCREEN_IMAGE_COL = 10;	// 화면 열 개수
	int LENGTH_SCREEN_START_X = 0;		// 시작 포인트 x 좌표
	int LENGTH_SCREEN_START_Y = 0;		// 시작 포인트 y 좌표
	int LENGTH_IMAGE_WIDTH = 48;		// 이미지 폭
	int LENGTH_IMAGE_HEIGHT = 48;		// 이미지 높이
	int MAX_GAME_LEVEL_NUM = 36;		// 최대 스테이지 수
	
	// 이미지 정보
	int IMAGE_TYPE_BACK = 0;				// 배경이미지
	int IMAGE_TYPE_BLOCK = 1;				// 벽
	int IMAGE_TYPE_STONE = 2;				// 돌 이미지
	int IMAGE_TYPE_HOUSE_EMPTY = 3;			// 비어 있는 집
	int IMAGE_TYPE_HOUSE_FULL = 4;			// 돌 + 집
	int IMAGE_TYPE_PUSH_MAN = 5;			// 푸쉬맨 이미지
	int IMAGE_TYPE_PUSH_MAN_IN_HOUSE = 6;	// 푸쉬맨+집
	
	// 게임시작
	void startGame();
	// 게임 종료-중지
	void stopGame();
	// 멤버 변수 초기화
	void initField();
	// 게임 맵 설정 텍스트 파일 로드
	String readTextFile(String filePath);
	// 매개변수로 넘겨 받은 stage번호에 따라 배열을 초기화
	void loadDataFile(int levelNum);
	// 로드된 파일 정보로 맵 초기화
	void initCanvas();
	// 게임진행 시간
	void startTimer();
	// 케릭터 이동 - key Event
	void movePushMan(KeyCode keyCode);
	// 캐릭터 좌표 얻기
	Point getPositionPushMan();
	// 이동된 돌의 위치 확인 - 돌이 이동 가능한지
	// Point == x,y 두개의 좌표값을 저장하는 객체
	boolean insertStoneToCell(Point poCell);
	// 빈자리를 기본이미지로 변경
	void recoverToEmptyCell(Point poCell);
	// 게임 클리어 체크
	boolean isGameComplate();
	// 게임 성공시 사용자 알림 다이얼로그
	void showDialog(ScoreVO obj);
}

















