package lab10.Entity;


import java.util.ArrayList;
import java.util.List;

public abstract class MovableElement extends Element {

	private static final long serialVersionUID = 1L;
	private Element stepElement;
	protected int lastPosX;
	protected int lastPosY;
	
	protected ArrayList<Integer> PosX=new ArrayList<>();
	protected ArrayList<Integer> PosY=new ArrayList<>();
	
	protected void removeBackPos() {
		PosX.remove(PosX.size()-1);
		PosY.remove(PosY.size()-1);
	}
	
	protected void recodeLastPos() {
		lastPosX=x;
		lastPosY=y;
	}
	
	protected void addPosXY() {
		PosX.add(lastPosX);
		PosY.add(lastPosY);
	}

	public MovableElement(EntityIcons entityIcons, int pos_x, int pos_y) {
		super(entityIcons, pos_x, pos_y);
		lastPosX=pos_x;
		lastPosY=pos_y;		
	}

	public final void moveXY(List<Element[]> map, int xAfterMove, int yAfterMove) {		
		if (stepElement == null) 
			map.get(y)[x] = new Empty(MapController.emptyEntityIcons, x, y);
		else {
			map.get(y)[x] = stepElement;
		}
		stepElement = map.get(yAfterMove)[xAfterMove];
		if (this instanceof Box)
			map.get(yAfterMove)[xAfterMove] = this;
		x = xAfterMove;
		y = yAfterMove;
	}
	
	protected final Element getStepElement() {
		return stepElement;
	}

	protected abstract boolean isMovable(List<Element[]> map, int xAfterMove, int yAfterMove, Player player);

}
