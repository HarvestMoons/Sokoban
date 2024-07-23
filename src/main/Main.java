package main;

import java.io.File;
import lab10.Entity.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

//这是一个不完善的lab，回退功能（'B'）有bug————2023.11.30
//可以成功导出jar包并运行，以后可以考虑使用maven构建项目管理依赖
public class Main extends Application {

	private final int width = 550;// 主窗口宽度
	private final int height = 550;// 主窗口高度
	private final int tipSceneHeight = 100;

	@Override
	public void start(Stage stage) {
		MapController.setHW(height, width, tipSceneHeight);
		MapController.setMainStage(stage);
		Music.playBackgroundMusic();
		gameInitialize(stage);

	}

	// 开始或继续游戏选择界面
	public void gameInitialize(Stage stage) {

	
		Image backgroundImage = new Image("file:src/img/mystart.png");
		ImageView imageView = new ImageView(backgroundImage);
		
		imageView.setFitWidth(width);
		imageView.setFitHeight(height);
		Button startButton = new Button("Start");
		Button continueButton = new Button("Continue");
		Button helpButton = new Button("Help");
		startButton.setPrefWidth(100);
		startButton.setPrefHeight(20);
		continueButton.setPrefWidth(100);
		continueButton.setPrefHeight(20);
		helpButton.setPrefWidth(100);
		helpButton.setPrefHeight(20);
		startButton.setOnAction(event -> {
			modeSelection(stage);
		});
		continueButton.setOnAction(event -> {
			MapController.isContinue = true;
			if (isFileEmpty()) {
                MapController.showMistakeScene("目前没有存档！");
            } else {
				MapController.readSaving();
					MapController.showGameStage();
			}
		});
		helpButton.setOnAction(event -> MapController.getHelp());

		Pane pane = new Pane();

		startButton.setLayoutX((double) width * 0.5 - 50);
		startButton.setLayoutY((double) width * 0.5 - 100);

		continueButton.setLayoutX((double) width * 0.5 - 50);
		continueButton.setLayoutY((double) width * 0.5);

		helpButton.setLayoutX((double) width * 0.5 - 50);
		helpButton.setLayoutY((double) width * 0.5 + 100);
		pane.getChildren().addAll(imageView, startButton, continueButton, helpButton);
		Scene modescene = new Scene(pane, height, width);

		// 设置场景
		stage.setScene(modescene);
		stage.show();

	}

	/*
	 * MAP选择的尝试 private void mapSelection(Stage stage) { final int mapSceneHeight =
	 * 500; final int mapSceneWidth = 800;
	 * 
	 * Canvas canvas = new Canvas(mapSceneWidth, mapSceneHeight); GraphicsContext gc
	 * = canvas.getGraphicsContext2D();
	 * 
	 * MapController.displayMap();
	 * 
	 * Pane pane = new Pane(); Scene mapDisplayScene = new Scene(pane,
	 * mapSceneWidth, mapSceneHeight);
	 * 
	 * mapDisplayScene.setOnMouseClicked(event -> { double mouseX =
	 * event.getSceneX(); double mouseY = event.getSceneY(); });
	 * stage.setScene(mapDisplayScene); stage.show(); }
	 */

	// 单、双人模式选择界面
	private void modeSelection(Stage stage) {
		Image backgroundImage = new Image("file:src/img/mystart.png");
		ImageView imageView = new ImageView(backgroundImage);
		Button singlePlayerMode = new Button("Single Player Game");
		Button multiPlayerMode = new Button("Multiplayer Game");
		singlePlayerMode.setPrefWidth(200);
		singlePlayerMode.setPrefHeight(30);
		multiPlayerMode.setPrefWidth(200);
		multiPlayerMode.setPrefHeight(30);

		singlePlayerMode.setOnAction(event -> {
			MapController.playerNum = 1;
			difficultySelection(stage);
		});

		multiPlayerMode.setOnAction(event -> {
			MapController.playerNum = 2;
			difficultySelection(stage);
		});

		Pane pane = new Pane();

		singlePlayerMode.setLayoutX((double) width * 0.5 - 100);
		singlePlayerMode.setLayoutY((double) width * 0.5 - 100);

		multiPlayerMode.setLayoutX((double) width * 0.5 - 100);
		multiPlayerMode.setLayoutY((double) width * 0.5 + 100);

		pane.getChildren().addAll(imageView, singlePlayerMode, multiPlayerMode);

		Scene modescene = new Scene(pane, height, width);

		stage.setScene(modescene);
		stage.show();
	}

	// 难度选择界面
	private void difficultySelection(Stage stage) {
		Image backgroundImage = new Image("file:src/img/mystart.png");
		ImageView imageView = new ImageView(backgroundImage);
		imageView.setFitWidth(width);
		imageView.setFitHeight(height);
		Button level1 = new Button("LEVEL 1");
		Button level2 = new Button("LEVEL 2");
		Button level3 = new Button("LEVEL 3");
		Button confirm = new Button("Confirm");

		level1.getStyleClass().add("myButton");
		level2.getStyleClass().add("myButton");
		level3.getStyleClass().add("myButton");
		confirm.getStyleClass().add("myButton");

		level1.setOnAction(event -> MapController.setPath(1));
		level2.setOnAction(event -> MapController.setPath(2));
		level3.setOnAction(event -> MapController.setPath(3));

		confirm.setOnAction(event -> {
			if (MapController.getPath() == null) {
                MapController.showMistakeScene("请先选择难度！");
            } else {
				MapController.readMap(MapController.map1);
				if (MapController.playerNum == 2) {
					MapController.readMap(MapController.map2);
				}
				MapController.getPlayerName();
			}
		});

		Pane pane = new Pane();

		level1.setLayoutX((double) width * 0.5 - 50);
		level1.setLayoutY((double) width * 0.5 - 200);

		level2.setLayoutX((double) width * 0.5 - 50);
		level2.setLayoutY((double) width * 0.5 - 100);

		level3.setLayoutX((double) width * 0.5 - 50);
		level3.setLayoutY((double) width * 0.5);

		confirm.setLayoutX((double) width * 0.5 - 50);
		confirm.setLayoutY((double) width * 0.5 + 100);

		pane.getChildren().addAll(imageView, level1, level2, level3, confirm);
		Scene startScene = new Scene(pane, height, width);
		startScene.getStylesheets().add("file:src/myButton.css");

		stage.setScene(startScene);
		stage.show();
	}

	// 判断文件是否为空
	public boolean isFileEmpty() {
		File file = new File(MapController.saveFilePath);
		return file.length() == 0;
	}
	
	
	public static void main(String[] args){
		launch();
	}


}
