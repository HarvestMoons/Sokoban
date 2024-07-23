package lab10.Entity;

import java.util.List;

public class Box extends MovableElement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Box(EntityIcons entityIcons, int pos_x, int pos_y) {
		super(entityIcons, pos_x, pos_y);
	}

	/** 是否已到达终点的标志 */
	protected boolean winFlag = false;
	@Override
	protected boolean isMovable(List<Element[]>map,int bxAfterMove, int byAfterMove,Player player) {
		Element element=map.get(byAfterMove)[bxAfterMove];
		// 无法移动返回false，正常情况返回true
        return !(element instanceof Wall) && !(element instanceof Box) && (!(element instanceof Barrier) || (player.propUsed()));
    }
	
}
