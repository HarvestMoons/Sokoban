package lab10.Entity;

import javafx.scene.image.Image;

public class EntityIcons  {
   
	private final int width;
    private final int height;
    private final Image image;

    public EntityIcons(String imagePath, int width, int height) {
        this.width = width;
        this.height = height;
        this.image = new Image("file:" + imagePath, width, height, true, true);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image getImage() {
        return image;
    }
}
