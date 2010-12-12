package images;

import java.awt.Image;

import javax.swing.ImageIcon;

import com.daohoangson.GameIO;

public class SpriteManager {
	public static String CFG_SPRITE_EXTENSION = "png";
	public static String CFG_DEFAULT_SPRITE = "default.gif";

	public static Image getSprite(String file) {
		ImageIcon i = new ImageIcon(SpriteManager.class.getResource(file));

		if (i != null) {
			return i.getImage();
		} else {
			GameIO.debug("Sprite not found: " + file);
			if (!file.equals(SpriteManager.CFG_DEFAULT_SPRITE)) {
				return SpriteManager.getDefaultSprite();
			} else {
				return null;
			}
		}
	}

	public static Image getSprite(int id) {
		return SpriteManager.getSprite(id + "."
				+ SpriteManager.CFG_SPRITE_EXTENSION);
	}

	public static Image getDefaultSprite() {
		return SpriteManager.getSprite(SpriteManager.CFG_DEFAULT_SPRITE);
	}
}
