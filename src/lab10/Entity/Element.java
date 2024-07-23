package lab10.Entity;

import java.io.Serializable;

public abstract class Element implements Serializable{

	private static final long serialVersionUID = 1L;
	int x = 0;
	int y = 0;

    protected transient EntityIcons entityIcons;

		public Element(EntityIcons entityIcons ,int pos_x, int pos_y) {
			this.entityIcons = entityIcons;
			this.x = pos_x;
			this.y = pos_y;
		}
    
    public EntityIcons getEntityIcons() {
        return entityIcons;
    }

    @Override
    public String toString() {
        return this.getClass().toString();
    }
    
}