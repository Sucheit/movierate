package arthur.inzhilov.movierate.utility;

import arthur.inzhilov.movierate.service.slopeone.Item;
import arthur.inzhilov.movierate.service.slopeone.User;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static arthur.inzhilov.movierate.utility.Constants.IMAGE_HEIGHT;
import static arthur.inzhilov.movierate.utility.Constants.IMAGE_WIDTH;
import static java.util.Map.Entry.comparingByValue;

public class Utility {

    public static String MultipartFileToImageString(MultipartFile file) {
        String image = "";
        try {
            image = resizeImageForUse(Base64.getEncoder().encodeToString(file.getBytes()), IMAGE_WIDTH, IMAGE_HEIGHT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static BufferedImage base64ToBufferedImage(String base64Image) {
        BufferedImage bufferedImage;
        byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
        try {
            bufferedImage = ImageIO.read(new ByteArrayInputStream(decodedBytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bufferedImage;
    }

    public static BufferedImage resizeImage(BufferedImage bufferedImage, int width, int height) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Thumbnails.of(bufferedImage)
                    .size(width, height)
                    .outputFormat("JPEG")
                    .outputQuality(1)
                    .toOutputStream(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] data = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        return ImageIO.read(inputStream);
    }

    public static String bufferedImageToBase64(BufferedImage bufferedImage) throws UnsupportedEncodingException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "JPEG", Base64.getEncoder().wrap(outputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputStream.toString(StandardCharsets.ISO_8859_1);
    }

    public static String resizeImageForUse(String image, int width, int height) {
        BufferedImage bufferedImage = base64ToBufferedImage(image);
        try {
            bufferedImage = resizeImage(bufferedImage, width, height);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            return bufferedImageToBase64(bufferedImage);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void print(HashMap<Item, Double> hashMap) {
        NumberFormat FORMAT = new DecimalFormat("#0.000");
        hashMap
                .entrySet()
                .stream()
                .sorted(comparingByValue())
                .forEach(entry -> System.out.println("\t\tФильм:" + entry.getKey().getItemId() + " --> " + FORMAT.format(entry.getValue())));
    }

    public static void printData(Map<User, HashMap<Item, Double>> data) {
        data.forEach((key, value) -> {
            System.out.println("\tПользователь " + key.getUserId() + ":");
            print(data.get(key));
        });
    }
}
