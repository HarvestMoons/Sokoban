package lab10.Entity;

import java.io.Serializable;

public class Wall extends Element implements Serializable{
	private static final long serialVersionUID = 1L;
	public Wall(EntityIcons entityIcons, int pos_x, int pos_y) {
		super(entityIcons, pos_x, pos_y);
	}
}
