package lab10.Entity;


import java.util.ArrayList;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

public class MovePerformer {

	private static ArrayList<KeyCode> moveComRecoder1 = new ArrayList<>();
	private static ArrayList<KeyCode> moveComRecoder2 = new ArrayList<>();
	private static KeyCode moveCode;

	// current开头的变量都是当前运行的对象
	private static Player currentPlayer = MapController.singlePlayer;
	private static List<Element[]> currentMap = MapController.map1;
	private static ArrayList<KeyCode> currentComRecoder = moveComRecoder1;

	private static int playerNum = MapController.playerNum;// 游戏中的玩家数量
	private static boolean isTwoToOne = false;// 是否由双人模式转换为单人模式

	// 设置固定移动模式
	public static void setFixed(Player fixedPlayer, List<Element[]> fixedMap) {
		playerNum = 1;
		isTwoToOne = true;
		currentPlayer = fixedPlayer;
		currentMap = fixedMap;

		if (currentPlayer.equals(MapController.singlePlayer))
			currentComRecoder = moveComRecoder1;
		else {
			currentComRecoder = moveComRecoder2;
		}
	}

	// 取消固定移动模式
	public static void cancelFixed() {
		if ((!MapController.singlePlayer.getIsWin()) && (!MapController.secondPlayer.getIsWin())
				&& ((MapController.singlePlayer.isQuit == false) && (MapController.secondPlayer.isQuit == false))) {
			playerNum = 2;
			isTwoToOne = false;
		}

	}

	public static void getKeyCode(Player player1, Player player2, GraphicsContext gc, Scene scene) {
		scene.setOnKeyPressed(event -> {
			moveCode = event.getCode();

			if (playerNum == 1 && moveCode != KeyCode.B && moveCode != KeyCode.H && moveCode != KeyCode.SHIFT) {
				currentComRecoder.add(moveCode);
			}

			if (playerNum == 2)
				alternateMovement(player1, player2, gc);
			else if (playerNum == 1)
				singleMovement(currentPlayer, currentMap, gc);

		});
	}

	private static void singleMovement(Player player, List<Element[]> map, GraphicsContext gc) {
		int moveResult = moveDirection();
		boolean isAllFinish = (MapController.singlePlayer.getIsWin() || MapController.singlePlayer.isQuit)
				&& (MapController.secondPlayer.getIsWin() || MapController.secondPlayer.isQuit);
		if (isTwoToOne)
			MapController.drawMap(gc, player);
		else
			MapController.drawMap(gc, null);

		if ((moveResult == 1 && !isTwoToOne) || (isTwoToOne && isAllFinish))
			MapController.winnerJudge(player);
		else if (moveResult == 1)
			changeMoveTurn();
	}

	private static void changeMoveTurn() {
		if (currentPlayer.equals(MapController.singlePlayer)) {
			currentPlayer = MapController.secondPlayer;
			currentMap = MapController.map2;
		} else if (currentPlayer.equals(MapController.secondPlayer)) {
			currentPlayer = MapController.singlePlayer;
			currentMap = MapController.map1;
		}
	}

	private static void alternateMovement(Player player1, Player player2, GraphicsContext gc) {
		int moveResult = moveDirection();
		if (moveResult != -1) {
			changeMoveTurn();
		}
		MapController.drawMap(gc, currentPlayer);
		if (moveResult == 1) {
			playerNum--;
			isTwoToOne = true;
		}
	}

	private static int moveDirection() {
		int xoffset = 0;
		int yoffset = 0;
		switch (moveCode) {
		case W: {
			yoffset = -1;
			break;
		}
		case A: {
			xoffset = -1;
			break;
		}
		case S: {
			yoffset = 1;
			break;
		}
		case D: {
			xoffset = 1;
			break;
		}
		case B: {
			if (MapController.playerNum == 2 && isTwoToOne == false)
				return -1;

			if (MapController.isContinue) {
				MapController.showMistakeScene("继续游戏时不允许使用回退功能！");
				return -1;
			}

			if ((MapController.playerNum == 1 || isTwoToOne == true) && currentComRecoder.size() > 0
					&& currentPlayer.PosX.size() > 0)
				back();
			return 0;
		}
		case Q: {
			currentPlayer.isQuit = true;
			MapController.attachSingleElementEI(currentPlayer);
			return 1;// 玩家退出，返回1
		}
		case H: {
			currentPlayer.useProp();
			// return -1;
		}
		default:
			return -1; // 错误输入，直接返回-1
		}

		isChangeCoordinate(xoffset, yoffset);
		if (currentPlayer.winCheck(currentMap))
			return 1;// 到达返回1，否则返回0；
		else
			return 0;
	}

	// 实现回退功能
	private static void back() {
		int xoffset = 0, yoffset = 0;

		KeyCode backCode = currentComRecoder.get(currentComRecoder.size() - 1);
		currentComRecoder.remove(currentComRecoder.size() - 1);
		if (backCode == KeyCode.W)
			yoffset = 1;
		else if (backCode == KeyCode.S)
			yoffset = -1;
		else if (backCode == KeyCode.A)
			xoffset = 1;
		else if (backCode == KeyCode.D)
			xoffset = -1;
		int pxBeforeMove = currentPlayer.x - xoffset;
		int pyBeforeMove = currentPlayer.y - yoffset;
		int pxAfterMove = currentPlayer.PosX.get(currentPlayer.PosX.size() - 1);
		int pyAfterMove = currentPlayer.PosY.get(currentPlayer.PosY.size() - 1);
		if (currentMap.get(pyBeforeMove)[pxBeforeMove] instanceof Box
				&& currentPlayer.isMovable(currentMap, pxAfterMove, pyAfterMove, currentPlayer)) {
			Box box = (Box) currentMap.get(pyBeforeMove)[pxBeforeMove];
			int bxAfterMove = box.PosX.get(box.PosX.size() - 1);
			int byAfterMove = box.PosY.get(box.PosY.size() - 1);
			currentPlayer.moveXY(currentMap, pxAfterMove, pyAfterMove);
			currentPlayer.removeBackPos();
			if (Math.abs(pxAfterMove - bxAfterMove) + Math.abs(pyAfterMove - byAfterMove) == 1) {
				box.moveXY(currentMap, bxAfterMove, byAfterMove);
				box.removeBackPos();
			}
			box.recodeLastPos();

		} else {
			{
				if (currentPlayer.isMovable(currentMap, pxAfterMove, pyAfterMove, currentPlayer))
					currentPlayer.moveXY(currentMap, pxAfterMove, pyAfterMove);
				currentPlayer.removeBackPos();
			}
		}
		currentPlayer.recodeLastPos();
	}

	private static void isChangeCoordinate(int xoffset, int yoffset) {
		// 假设玩家可以移动，以下先计算玩家在移动完成后的坐标
		int pxAfterMove = currentPlayer.x + xoffset;
		int pyAfterMove = currentPlayer.y + yoffset;
		// 先判断玩家面前是否有箱子
		if (currentMap.get(pyAfterMove)[pxAfterMove] instanceof Box) {
			Box box = (Box) currentMap.get(pyAfterMove)[pxAfterMove];

			int bxAfterMove = box.x + xoffset;
			int byAfterMove = box.y + yoffset;
			// 1.有箱子，则判断箱子能不能动

			if (box.isMovable(currentMap, bxAfterMove, byAfterMove, currentPlayer)) {
				box.moveXY(currentMap, bxAfterMove, byAfterMove);
				currentPlayer.moveXY(currentMap, pxAfterMove, pyAfterMove);
				currentPlayer.count++;// 箱子有移动，才会记入有效移动次数
			}
			box.addPosXY();
			box.recodeLastPos();
			currentPlayer.addPosXY();
			currentPlayer.recodeLastPos();
		}

		else // 2.无箱子，则判断玩家自己能不能动
		{

			// 若玩家没有撞墙，移动玩家
			if (currentPlayer.isMovable(currentMap, pxAfterMove, pyAfterMove, currentPlayer))
				currentPlayer.moveXY(currentMap, pxAfterMove, pyAfterMove);

			if (currentPlayer.isUsingProp)
				currentPlayer.isUsingProp = false;
			currentPlayer.addPosXY();
			currentPlayer.recodeLastPos();
		}
	}
}
