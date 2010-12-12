package images;

import java.awt.Image;

import javax.swing.ImageIcon;

import com.daohoangson.GameIO;

public class SpriteManager {
	public static Image getSprite(String id) {
		ImageIcon i = new ImageIcon(SpriteManager.class
				.getResource(id + ".gif"));

		if (i != null) {
			return i.getImage();
		} else {
			GameIO.debug("Sprite not found: #" + id);
			if (!id.equals("default")) {
				return SpriteManager.getDefaultSprite();
			} else {
				return null;
			}
		}
	}

	public static Image getSprite(int id) {
		return SpriteManager.getSprite(String.valueOf(id));
	}

	public static Image getDefaultSprite() {
		return SpriteManager.getSprite("default");
	}
}
