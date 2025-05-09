package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ResourceLoader {
    public static BufferedImage loadImage(String path) {
        String fullPath = "resources/images/" + path;
        InputStream is = ResourceLoader.class.getClassLoader().getResourceAsStream(fullPath);

        if (is == null) {
            String absolutePath = "/" + fullPath;
            is = ResourceLoader.class.getClassLoader().getResourceAsStream(absolutePath);
        }
        try {
            BufferedImage image = ImageIO.read(is);
            is.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}
