package lab10.Entity;
import java.util.List;

public class Player extends MovableElement {

	private static final long serialVersionUID = 1L;
	/** 玩家姓名 */
	private String name = null;
	/** 正确移动输入计数 */
	protected int count = 0;
	/** 道具数量 */
	private int prop;
	/** 是否处在使用道具状态 */
	protected boolean isUsingProp = false;
	/** 是否获胜 */
	private boolean isWin = false;
	/** 到达终点的箱子个数 */
	protected int endBox;

	protected boolean isQuit = false;

	public Player(EntityIcons entityIcons, int pos_x, int pos_y, int level) {
		super(entityIcons, pos_x, pos_y);
		prop = level;
	}

	@Override
	protected boolean isMovable(List<Element[]> map, int pxAfterMove, int pyAfterMove, Player player) {
		if (map.get(pyAfterMove)[pxAfterMove] instanceof Wall) {
			return false;// 撞墙,返回false
		}
		return true;// 正常情况返回true
	}

	protected boolean winCheck(List<Element[]> map) {
		endBox = 0;
		isWin = true;// 假设玩家获胜
		for (Element[] elements:map) {
			for (Element e : elements) {
				if (e instanceof Box) {
					if (!(((Box) e).getStepElement() instanceof End))
						isWin = false;// 地图的这个地方是箱子，但这里之前不是终点，则未胜利
					else
						endBox++;
				}
			}
		}

		return isWin;// 玩家获胜，返回true,否则返回false
	}

	protected void useProp() {
		if (isUsingProp == false && prop > 0) {
			isUsingProp = true;
		}
	}

	protected boolean propUsed() {
		if (isUsingProp) {
			prop--;// 使用道具后保持状态一回合
			isUsingProp = false;// 使用了道具，此后又回到未使用道具的状态
			return true;
		} else {
			return false;// 未使用道具，返回false
		}
	}

	protected boolean getIsWin() {
		return isWin;
	}

	public void setEntityIcons(EntityIcons entityIcons) {// 设置图像
		this.entityIcons = entityIcons;
	}


	public EntityIcons getEntityIcons() {// 获取图像
		return entityIcons;
	}

	protected void setName(String name) {// 设置姓名
		this.name = name;
	}

	protected String getName() {// 获取姓名
		return name;
	}

	protected int getProp() {
		return prop;
	}

}
