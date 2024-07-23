package lab10.Entity;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class MapController {
	public static int playerNum;// 游戏人数
	public static boolean isContinue=false;//是否是继续游戏的情况
	public static final String saveFilePath = "ModeAndDifficulty.txt";//存档文件名
	public static List<Element[]> map1 = new ArrayList<>();//玩家一的地图
	public static List<Element[]> map2 = new ArrayList<>();//玩家二的地图
	public static Player singlePlayer;//玩家一
	public static Player secondPlayer;//玩家二	
	private static String Path = null;
	private static int height;//主窗口高度
	private static int width;//主窗口宽度
	private static int tipSceneHeight;//游戏提示信息界面高度
	private static int level;// 游戏等级	
	private static Stage mainStage;//主界面	
	private static final int MAX_INPUT = 5;//最大玩家昵称长度
	private static final int mapSize = 10;//10*10大小的地图
	private static EntityIcons boxEntityIcons, endEntityIcons, playerEntityIcons, quitEntityIcons, wallEntityIcons,
			barrierEntityIcons;
	protected static EntityIcons emptyEntityIcons;

	/*
	 * public static void displayMap(GraphicsContext gc) {
	 * 
	 * try { BufferedReader in; String str; int[][] mapArray=new
	 * int[mapSize][mapSize]; for (int i = 1; i <= 3; i++) { if (i == 1) in = new
	 * BufferedReader(new
	 * FileReader("src/main/resources/com/example/lab10/map/map1.map")); else if
	 * (i==2) { in = new BufferedReader(new
	 * FileReader("src/main/resources/com/example/lab10/map/map2.map")); } else in =
	 * new BufferedReader(new
	 * FileReader("src/main/resources/com/example/lab10/map/map3.map")); while ((str
	 * = in.readLine()) != null) { int row=0; String[] rowArray = str.split(" ");
	 * for (int col = 0; col < mapSize; col++) { int
	 * a=Integer.parseInt(rowArray[col]); if(a==0)
	 * gc.drawImage(emptyEntityIcons.getImage(), col, row); else if(a==1)
	 * gc.drawImage(wallEntityIcons.getImage(), col, row); } row++; }
	 * drawMap(gc,mapArray,i);
	 * 
	 * } } catch (IOException e) { e.printStackTrace(); } }
	 * 
	 * private static void drawMap(GraphicsContext gc,int [][]mapArray,int i) {
	 * 
	 * }
	 */

	//读取地图，初始化各个element
	public static void readMap(List<Element[]> map) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(Path));
			String str;
			int y_axis = 0;
			while ((str = in.readLine()) != null) {
				String[] row = str.split(" ");
				Element[] mapelements = new Element[row.length];
				for (int x_axis = 0; x_axis < row.length; x_axis++) {
					int mapnum = Integer.parseInt(row[x_axis]);

					Element myElement = null;

					switch (mapnum) {
					case 0:
						myElement = new Empty(emptyEntityIcons, x_axis, y_axis);
						break;
					case 1:
						myElement = new Wall(wallEntityIcons, x_axis, y_axis);
						break;
					case 2:
						myElement = new Empty(emptyEntityIcons, x_axis, y_axis);
						if (map.equals(map1))// 无论如何，都初始化player2
						{
							singlePlayer = new Player(playerEntityIcons, x_axis, y_axis, level);
							secondPlayer = new Player(playerEntityIcons, x_axis, y_axis, level);
						}
						break;
					case 3:
						myElement = new End(endEntityIcons, x_axis, y_axis);
						break;
					case 4:
						myElement = new Barrier(barrierEntityIcons, x_axis, y_axis);
						break;
					case 5:
						myElement = new Box(boxEntityIcons, x_axis, y_axis);
						break;
					}

					mapelements[x_axis] = myElement;
				}
				map.add(mapelements);
				y_axis++;
			}
			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//绘制地图
	public static void drawMap(GraphicsContext gc, Player player) {
		gc.clearRect(0, 0, width * playerNum, height + tipSceneHeight * (playerNum - 1));
		List<Element[]> currentDrawMap;
		Player currentDrawPlayer;
		for (int copyDraw = playerNum - 1; copyDraw >= 0; copyDraw--)// 双人游戏的copyDraw=1，要进行两次绘图
		{// 有玩家2存在先画地图2，此后再画地图1
			if (copyDraw == 1) {
				currentDrawMap = map2;
				currentDrawPlayer = secondPlayer;
			} else {
				currentDrawMap = map1;
				currentDrawPlayer = singlePlayer;
			}

			int widthOffset = width * copyDraw;// 画图位置的横向偏移量
			for (int i = 0; i < mapSize; i++) {
				Element[] elements = currentDrawMap.get(i);
				for (int j = 0; j < mapSize; j++) {
					gc.drawImage(elements[j].entityIcons.getImage(), (double) (j * width) / mapSize + widthOffset,
							(double) (i * height) / mapSize);
				}
			}
			//单独绘制游戏玩家
			gc.drawImage(currentDrawPlayer.entityIcons.getImage(), (double) (currentDrawPlayer.x * width) / mapSize + widthOffset,
					(double) (currentDrawPlayer.y * height) / mapSize);
			drawTip(gc, player);
		}
	}

	//游戏界面下方的提示信息
	private static void drawTip(GraphicsContext gc, Player player) {

		// tip背景色
		gc.setFill(Color.ROSYBROWN);
		gc.fillRect(0, height, playerNum * width, height + tipSceneHeight);
		// 在 GraphicsContext 上设置文本属性
		gc.setFill(Color.PINK);
		gc.setFont(javafx.scene.text.Font.font("Arial", 24));
		// 双人模式的tip
		if (player != null) {
			EntityIcons redSpotEntityIcons = new EntityIcons("src/img/redSpot.png",
					tipSceneHeight, tipSceneHeight);
			if (player.equals(singlePlayer)) {
                gc.drawImage(redSpotEntityIcons.getImage(), 0, height);
            } else if (player.equals(secondPlayer)) {
                gc.drawImage(redSpotEntityIcons.getImage(), width, height);
            }
			// 输出文本
			gc.fillText("1号玩家" + singlePlayer.getName() + "的推箱子次数： " + singlePlayer.count, (double) width / 5,
					height + tipSceneHeight * 0.5);
			gc.fillText("到达终点的箱子个数：" + singlePlayer.endBox, (double) width / 5, height + tipSceneHeight * 0.8);
			gc.fillText("2号玩家" + secondPlayer.getName() + "的推箱子次数： " + secondPlayer.count, (double) width / 5 + width,
					height + tipSceneHeight * 0.5);
			gc.fillText("到达终点的箱子个数：" + secondPlayer.endBox, (double) width / 5 + width, height + tipSceneHeight * 0.8);
		}
		// 单人模式的tip
		else {
			gc.fillText("玩家" + singlePlayer.getName() + "目前的推箱子次数： " + singlePlayer.count, (double) width / 5,
					height + tipSceneHeight * 0.5);
			gc.fillText("到达终点的箱子个数： " + singlePlayer.endBox, (double) width / 5 + 50, height + tipSceneHeight * 0.8);
		}
	}

	//设置地图路径
	public static void setPath(int a) {
		level = a;
		switch (a) {
		case 1: {
			Path = "src/map/map1.map";
			break;
		}
		case 2: {
			Path = "src/map/map2.map";
			break;
		}
		case 3: {
			Path = "src/map/map3.map";
			break;
		}
		}
		initEI();
	}

	// 游戏玩家姓名输入的子窗口构建
	public static void getPlayerName() {
		final int secondWidth = 600;
		final int secondHeight = 600;
		Stage inputNameStage = new Stage();
		inputNameStage.initOwner(mainStage);
		Button clear = new Button("清除");
		Button start = new Button("开始");
		GridPane gridPane = new GridPane();
        setGridBGImage(gridPane, "file:src/img/nameInputBG.png",
                secondWidth, secondHeight);
        gridPane.setVgap(30);
		gridPane.setHgap(50);
		gridPane.add(clear, 0, 2);
		gridPane.add(start, 1, 2);
		GridPane.setMargin(start, new Insets(0, 0, 0, 115));

		clear.getStyleClass().add("myNameInputButton");
		start.getStyleClass().add("myNameInputButton");
		if (playerNum == 2) {
			Text tip1 = new Text("请为玩家1创建昵称：");
			Text tip2 = new Text("请为玩家2创建昵称：");
			tip1.setStyle("-fx-font-size: 20px; -fx-fill: #273961;");
			tip2.setStyle("-fx-font-size: 20px; -fx-fill: #273961;");

			TextField field1 = new TextField(), field2 = new TextField(); // 创建一个单行输入框
			field1.setPromptText("玩家昵称请勿超过" + MAX_INPUT + "个字"); // 设置单行输入框的提示语
			field2.setPromptText("玩家昵称请勿超过" + MAX_INPUT + "个字"); // 设置单行输入框的提示语
			gridPane.add(tip1, 0, 0);
			gridPane.add(tip2, 1, 0);
			gridPane.add(field1, 0, 1);
			gridPane.add(field2, 1, 1);

			gridPane.setAlignment(Pos.CENTER);

			clear.setOnAction(event -> {
				field1.clear();
				field2.clear();
			});

			start.setOnAction(event -> {
				if (field1.getText().isEmpty() || field2.getText().isEmpty()) {
                    showMistakeScene("请先为两位玩家设置昵称！");
                } else {
					if (field1.getText().length() > MAX_INPUT || field2.getText().length() > MAX_INPUT) {
                        showMistakeScene("请缩减昵称长度！");
                    } else {
						singlePlayer.setName(field1.getText());
						secondPlayer.setName(field2.getText());
						inputNameStage.close();
						showGameStage();
					}
				}
			});
		} else {//单人模式
			Text tip = new Text("请为玩家创建昵称：");
			tip.setStyle("-fx-font-size: 20px; -fx-fill: #273961;");
			TextField field = new TextField();
			field.setPromptText("玩家昵称请勿超过" + MAX_INPUT + "个字"); 

			gridPane.add(tip, 0, 0);
			gridPane.add(field, 0, 1);
			gridPane.setAlignment(Pos.CENTER);
			GridPane.setHalignment(tip, javafx.geometry.HPos.CENTER);
			GridPane.setHalignment(field, javafx.geometry.HPos.CENTER);
			clear.setOnAction(event -> field.clear());
			start.setOnAction(event -> {
				if (field.getText().isEmpty()) {
                    showMistakeScene("请先为玩家设置昵称！");
                } else {
					if (field.getText().length() > MAX_INPUT) {
                        showMistakeScene("请缩减昵称长度！");
                    } else {
						singlePlayer.setName(field.getText());
						inputNameStage.close();
						showGameStage();
					}
				}
			});

		}
		Scene inputNameScene = new Scene(gridPane, secondWidth, secondHeight);
		inputNameScene.getStylesheets().add("file:src/myNameInputButton.css");
		inputNameStage.initModality(Modality.WINDOW_MODAL);
		inputNameStage.setScene(inputNameScene);
		inputNameStage.show();
	}

	// 游戏界面显示
	public static void showGameStage() {
		Canvas canvas = new Canvas(playerNum * width, height + tipSceneHeight);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		// 菜单栏（单人模式菜单栏没有移动相关的选项）
		Menu fileMenu = new Menu("设置");
		MenuItem saveGame = new MenuItem("保存进度");
		MenuItem help = new MenuItem("帮助");
		if (playerNum == 2) {
			MenuItem turnToOne = new MenuItem("固定玩家1移动");
			MenuItem turnToTwo = new MenuItem("固定玩家2移动");
			MenuItem defaultMode = new MenuItem("交替移动(默认模式)");
			fileMenu.getItems().addAll(turnToOne, turnToTwo, defaultMode, saveGame, help);
			turnToOne.setOnAction(event -> {
				if (!singlePlayer.getIsWin() && !singlePlayer.isQuit) {
                    MovePerformer.setFixed(singlePlayer, map1);
                }
			});

			turnToTwo.setOnAction(event -> {
				if (!secondPlayer.getIsWin() && !secondPlayer.isQuit) {
                    MovePerformer.setFixed(secondPlayer, map2);
                }
			});

			defaultMode.setOnAction(event -> {
				MovePerformer.cancelFixed();
			});
			mainStage.setTitle("双人模式 —— Level" + level);
			drawMap(gc, MapController.singlePlayer);
		}

		else {
			mainStage.setTitle("单人模式 —— Level" + level);
			fileMenu.getItems().addAll(saveGame, help);
			drawMap(gc, null);
		}

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().add(fileMenu);

		saveGame.setOnAction(event -> {
			saveGame();
		});

		help.setOnAction(event -> {
			getHelp();
		});
		Pane pane = new Pane();
		pane.getChildren().addAll(canvas, menuBar);
		Scene gameScene = new Scene(pane, playerNum * width, height + tipSceneHeight);

		mainStage.setScene(gameScene);
		mainStage.show();
		MovePerformer.getKeyCode(singlePlayer, secondPlayer, gc, gameScene);
	}

	// 游戏保存
	protected static void saveGame() {
		String filePath = "ModeAndDifficulty.txt";
		// 把内容写入文件，并清空原有内容
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
			writer.write(playerNum + " " + level);
			writer.flush();
		} catch (IOException e) {
			showMistakeScene("写入文件时发生错误: " + e.getMessage());
		}

		List<Player> playerList = new ArrayList<>();
		playerList.add(singlePlayer);
		playerList.add(secondPlayer);
		List<List<Element[]>> mapList = new ArrayList<>();
		mapList.add(map1);
		mapList.add(map2);

		//保存玩家信息
		try (FileOutputStream fileOut = new FileOutputStream("player.ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
			out.writeObject(playerList);
		} catch (IOException e) {
			showMistakeScene("保存对象时发生错误: " + e.getMessage());
		}
		// 保存地图
		try (FileOutputStream fileOut = new FileOutputStream("map.ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
			out.writeObject(mapList);
		} catch (IOException e) {
			showMistakeScene("保存对象时发生错误: " + e.getMessage());
		}
	}

	// 读取保存的信息
	@SuppressWarnings("unchecked")
	public static void readSaving() {
		try (BufferedReader reader = new BufferedReader(new FileReader(saveFilePath))) {
			String line;
			if ((line = reader.readLine()) != null) {
				String[] data = line.split(" "); // 根据空格分隔字符串

				playerNum = Integer.parseInt(data[0]);
				level = Integer.parseInt(data[1]);
				// mapSize = level + LEVEL_MAPSIZE;
				initEI();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<Player> loadedPlayerList = null;
		List<List<Element[]>> loadedmapList = null;
		try (FileInputStream fileIn = new FileInputStream("player.ser");
				ObjectInputStream in = new ObjectInputStream(fileIn)) {
			loadedPlayerList = (List<Player>) in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		try (FileInputStream fileIn = new FileInputStream("map.ser");
				ObjectInputStream in = new ObjectInputStream(fileIn)) {
			loadedmapList = (List<List<Element[]>>) in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		// 从集合对象中获取所需的实例
		singlePlayer = loadedPlayerList.get(0);
		secondPlayer = loadedPlayerList.get(1);
		map1 = loadedmapList.get(0);
		attachEI(map1);
		attachSingleElementEI(singlePlayer);
		//System.out.println("here");
		if (playerNum == 2) {
			map2 = loadedmapList.get(1);
			attachEI(map2);
			attachSingleElementEI(secondPlayer);
			if (singlePlayer.isQuit || singlePlayer.getIsWin()) {
                MovePerformer.setFixed(secondPlayer, map2);
            } else if (secondPlayer.isQuit || secondPlayer.getIsWin()) {
                MovePerformer.setFixed(singlePlayer, map1);
            }
		}
	}

	// 遍历地图，为各个元素附上EI的值
	private static void attachEI(List<Element[]> map) {
		for (Element[] elements : map) {
			for (Element element : elements) {
				attachSingleElementEI(element);
			}
		}
	}

	// 根据元素类型，为单个元素附上EI，对于player与box的stepElement，递归调用该方法一次即可
	protected static void attachSingleElementEI(Element element) {
		if (element instanceof Wall) {
            element.entityIcons = wallEntityIcons;
        } else if (element instanceof Player) {
			attachSingleElementEI(((Player) element).getStepElement());
			if (((Player) element).isQuit) {
                element.entityIcons = quitEntityIcons;
            } else {
                element.entityIcons = playerEntityIcons;
            }
		} else if (element instanceof Empty) {
            element.entityIcons = emptyEntityIcons;
        } else if (element instanceof Box) {
			attachSingleElementEI(((Box) element).getStepElement());
			element.entityIcons = boxEntityIcons;
		} else if (element instanceof Barrier) {
            element.entityIcons = barrierEntityIcons;
        } else if (element instanceof End) {
            element.entityIcons = endEntityIcons;
        }
	}

	// 获胜方的判断
	protected static void winnerJudge(Player lastEndPlayer) {
		String endMessageString = null;
		if (playerNum == 2) {
			boolean isAllQuit = singlePlayer.isQuit && secondPlayer.isQuit;
			boolean isAllNotQuit = !(singlePlayer.isQuit || secondPlayer.isQuit);
			if (isAllQuit) {
                endMessageString = " 势均力敌,不分胜负!";
            } else if (secondPlayer.isQuit || (isAllNotQuit && singlePlayer.count < secondPlayer.count)
					|| (isAllNotQuit && singlePlayer.count > secondPlayer.count)
					|| (isAllNotQuit && singlePlayer.count == secondPlayer.count
							&& singlePlayer.getProp() > secondPlayer.getProp())
					|| (singlePlayer.count == secondPlayer.count && singlePlayer.getProp() == secondPlayer.getProp()
							&& lastEndPlayer.equals(secondPlayer))) {
                endMessageString = "恭喜1号玩家" + singlePlayer.getName() + "获胜！";
            } else if (singlePlayer.isQuit || (isAllNotQuit && singlePlayer.count > secondPlayer.count)
					|| (isAllNotQuit && singlePlayer.count == secondPlayer.count
							&& singlePlayer.getProp() < secondPlayer.getProp())
					|| (singlePlayer.count == secondPlayer.count && singlePlayer.getProp() == secondPlayer.getProp()
							&& lastEndPlayer.equals(singlePlayer))) {
                endMessageString = "恭喜2号玩家" + secondPlayer.getName() + "获胜！";
            }

		} else if (playerNum == 1 && !singlePlayer.isQuit) {
			endMessageString = "  恭喜！您通关了！";
		} else if (playerNum == 1 && singlePlayer.isQuit) {
			endMessageString = "   感谢您的游玩！";
		}
		outPutEnd(endMessageString);
	}

//游戏胜利画面的展示
	private static void outPutEnd(String endMessageString) {
		final int thirdWidth = 700;
		final int thirdHeight = 700;
		final int tipWidth = 300;

		Label label = new Label(endMessageString);
		label.setStyle("-fx-text-fill: brown;-fx-font-family:'KaiTi' ; -fx-font-size: 36px;");

		Stage winnerOutputStage = new Stage();
		winnerOutputStage.initOwner(mainStage);
		winnerOutputStage.initModality(Modality.WINDOW_MODAL);
		Pane pane = new Pane();
		label.setLayoutX(185);
		label.setLayoutY(330);
		BackgroundImage background = new BackgroundImage(
				new Image("file:src/img/winnerOutput.png"), BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				new BackgroundSize(thirdWidth, thirdHeight, false, false, true, false));
		Background backgroundStyle = new Background(background);
		pane.setBackground(backgroundStyle);
		pane.getChildren().addAll(label, outputEndTip(thirdWidth, thirdHeight, tipWidth));
		Scene winnerOutputsScene = new Scene(pane, thirdWidth + tipWidth, thirdHeight);
		winnerOutputStage.setScene(winnerOutputsScene);
		winnerOutputStage.show();

		winnerOutputsScene.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.Q) {
				winnerOutputStage.close();
				mainStage.close();
			}
		});

		winnerOutputStage.setOnCloseRequest(event -> {
			mainStage.close();

		});

	}

	// 胜利画面右侧有游戏详细信息结算，在此方法中绘制
	private static Canvas outputEndTip(int thirdWidth, int thirdHeight, int tipWidth) {
		final int hGap = 50;
		Player player;
		Canvas canvas = new Canvas(tipWidth + thirdWidth, thirdHeight);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.ROSYBROWN);
		gc.fillRect(thirdWidth, 0, tipWidth, thirdHeight);
		gc.setFill(Color.PINK);
		gc.setFont(javafx.scene.text.Font.font("Arial", 24));
		gc.fillText("------------------------------------------------------------", thirdHeight, (double) thirdHeight / 2);
		for (int copy = playerNum; copy > 0; copy--) {
			if (copy == 2) {
                player = secondPlayer;
            } else {
                player = singlePlayer;
            }
			gc.fillText("玩家 " + player.getName() + " 的游戏结算:", thirdWidth + 20, (double) ((copy - 1) * thirdHeight) / 2 + hGap);
			gc.fillText("推箱子次数：" + player.count, thirdWidth + 20, (double) ((copy - 1) * thirdHeight) / 2 + 2 * hGap);
			gc.fillText("道具使用次数：" + (level - player.getProp()), thirdWidth + 20,
					(double) ((copy - 1) * thirdHeight) / 2 + 3 * hGap);
			if (player.isQuit) {
                gc.fillText("未完成关卡   ＞︿＜", thirdWidth + 20, (double) ((copy - 1) * thirdHeight) / 2 + 4 * hGap);
            } else {
                gc.fillText("成功通关！   (～￣▽￣)～", thirdWidth + 20, (double) ((copy - 1) * thirdHeight) / 2 + 4 * hGap);
            }
		}

		return canvas;
	}

	// 初始化EI
	private static void initEI() {
		int EI_height = height / (mapSize);
		int EI_width = width / (mapSize);
		emptyEntityIcons = new EntityIcons("src/img/empty.png", EI_width, EI_height);
		wallEntityIcons = new EntityIcons("src/img/wall.png", EI_width, EI_height);
		barrierEntityIcons = new EntityIcons("src/img/barrier.png", EI_width,
				EI_height);
		quitEntityIcons = new EntityIcons("src/img/player_gray.png", EI_width,
				EI_height);
		playerEntityIcons = new EntityIcons("src/img/player.png", EI_width, EI_height);
		endEntityIcons = new EntityIcons("src/img/end.png", EI_width, EI_height);
		boxEntityIcons = new EntityIcons("src/img/box.png", EI_width, EI_height);
	}

	//为gridPane设置背景图片
	//没有必要返回 gridPane,已经通过传引用的方式将背景图片设置到了传入的 gridPane 上
	private static void setGridBGImage(GridPane gridPane, String path, int imageWidth, int imageHeight) {
		Image image = new Image(path, imageWidth, imageHeight, false, true);
		BackgroundImage bImg = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
		Background bGround = new Background(bImg);
		gridPane.setBackground(bGround);
	}	
	
	public static void setMainStage(Stage stage) {
		mainStage = stage;
	}

	//初始化变量
	public static void setHW(int inputHeight, int inputWidth, int inputTipSceneHeight) {
		height = inputHeight;
		width = inputWidth;
		tipSceneHeight = inputTipSceneHeight;
	}

	
	public static String getPath() {
		return Path;
	}

	// 展示帮助界面
	public static void getHelp() {
		final int helpWidth = 800;
		final int helpHeight = 600;
		final int textWidth = 350;
		final int lineSpacing = 7;
		Stage helpStage = new Stage();
		helpStage.initOwner(mainStage);

		GridPane gridPane = new GridPane();
        setGridBGImage(gridPane, "file:src/img/help.png", helpWidth,
                helpHeight);
        Text helpMessage = new Text("亲爱的玩家你好！这是一个推箱子小游戏，你可以自由地选择单双人模式和游戏难度。你可以使用WSAD进行上下左右四个方向的移动；按下H使用道具（你有" + level
				+ "个道具可以使用），它可以让你在下一步把箱子移动到障碍物上；按B回退进度；按下Q退出游戏。就像常见的推箱子游戏，当你把所有箱子推到终点时，游戏就结束了。在双人模式中，如果双方都到达终点，推箱子次数更少的那位会获胜哦！祝你游玩愉快！     ༼ つ ◕_◕ ༽つ");
		helpMessage.setStyle("-fx-font-size: 20px; -fx-fill: pink;");
		TextFlow textFlow = new TextFlow(helpMessage);
		textFlow.setTextAlignment(TextAlignment.JUSTIFY);
		textFlow.setPrefWidth(textWidth); // 设置宽度，根据实际情况调整
		textFlow.setLineSpacing(lineSpacing); // 设置行间距，根据实际情况调整
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setVgap(30);
		gridPane.setHgap(50);
		gridPane.add(textFlow, 0, 1);

		Scene helpScene = new Scene(gridPane, helpWidth, helpHeight);
		helpStage.initModality(Modality.WINDOW_MODAL);
		helpStage.setTitle("帮助与说明");
		helpStage.setScene(helpScene);
		helpStage.show();
	}

	// 报错信息的界面展示
	public static void showMistakeScene(String mistake) {

		final int mistakeWidth = 800;
		final int mistakeHeight = 600;
		Stage mistakeStage = new Stage();
		mistakeStage.initOwner(mainStage);

		GridPane gridPane = new GridPane();
        setGridBGImage(gridPane, "file:src/img/mistake.png", mistakeWidth,
                mistakeHeight);

        Text mistaketeText = new Text("X﹏X " + mistake);
		mistaketeText.setStyle("-fx-font-size: 40px; -fx-fill: #6cc3c8;");
		TextFlow textFlow = new TextFlow(mistaketeText);
		textFlow.setTextAlignment(TextAlignment.JUSTIFY);
		Button confirmButton = new Button("我知道了...     o((>ω< ))o");
		confirmButton.setPrefWidth(400);
		confirmButton.setPrefHeight(20);
		confirmButton.getStyleClass().add("myButton");
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setHgap(50);
		gridPane.setVgap(30);
		gridPane.add(textFlow, 0, 0, 1, 1);
		gridPane.add(confirmButton, 0, 3);
		gridPane.setStyle("-fx-font-family: 'KaiTi';-fx-font-size: 30;");
		Scene mistakeScene = new Scene(gridPane, mistakeWidth, mistakeHeight);
		mistakeScene.getStylesheets().add("file:src/myButton.css");
		confirmButton.setOnAction(( event) -> {
			mistakeStage.close();
		});
		mistakeStage.initModality(Modality.WINDOW_MODAL);
		mistakeStage.setScene(mistakeScene);
		mistakeStage.show();
		
	}

}
