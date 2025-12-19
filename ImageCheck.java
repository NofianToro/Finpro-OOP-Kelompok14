
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;

public class ImageCheck {
    public static void main(String[] args) {
        try {
            BufferedImage b = ImageIO.read(new File("assets/player/Body/Run1.png"));
            System.out.println("SIZE:" + b.getWidth() + "x" + b.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
