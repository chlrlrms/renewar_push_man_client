module renewar_push_man_client {
	exports push_man.vo;

	requires java.desktop;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.web;
	
	opens push_man.main to javafx.graphics, javafx.fxml;
	opens push_man.game to javafx.graphics, javafx.fxml;
	opens push_man.member to javafx.graphics, javafx.fxml;
	opens push_man.user_info to javafx.graphics, javafx.fxml;
	opens push_man.waiting_room to javafx.graphics, javafx.fxml;
	
}