package com.wardrobe.backend.config;

import com.wardrobe.backend.entity.Clothes;
import com.wardrobe.backend.mapper.ClothesMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Component
@Order(2)
public class ClothesDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ClothesDataInitializer.class);

    private final ClothesMapper clothesMapper;
    private final Path uploadPath;

    private static final int IMG_WIDTH = 600;
    private static final int IMG_HEIGHT = 800;

    public ClothesDataInitializer(ClothesMapper clothesMapper,
                                  @Value("${upload.path:upload/images}") String uploadDir) {
        this.clothesMapper = clothesMapper;
        this.uploadPath = Paths.get(uploadDir);
    }

    @Override
    public void run(String... args) {
        List<Clothes> existing = clothesMapper.findAll();
        if (existing != null && !existing.isEmpty()) {
            return;
        }

        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            log.error("无法创建上传目录: {}", e.getMessage());
            return;
        }

        List<SeedItem> items = List.of(
                new SeedItem("白色T恤", 1, "休闲", "79.00"),
                new SeedItem("条纹衬衫", 1, "商务", "169.00"),
                new SeedItem("黑色卫衣", 1, "休闲", "199.00"),
                new SeedItem("牛仔夹克", 1, "时尚", "359.00"),
                new SeedItem("修身牛仔裤", 2, "时尚", "259.00"),
                new SeedItem("黑色西裤", 2, "商务", "299.00"),
                new SeedItem("碎花连衣裙", 3, "时尚", "339.00"),
                new SeedItem("百褶半身裙", 3, "休闲", "199.00"),
                new SeedItem("白色运动鞋", 4, "休闲", "459.00"),
                new SeedItem("黑色皮鞋", 4, "商务", "599.00")
        );

        int count = 0;
        for (SeedItem item : items) {
            try {
                String filename = generateImage(item.name, item.typeId);
                Clothes clothes = new Clothes();
                clothes.setClothName(item.name);
                clothes.setImage(filename);
                clothes.setTypeId(item.typeId);
                clothes.setStyle(item.style);
                clothes.setPrice(new BigDecimal(item.price));
                clothesMapper.insert(clothes);
                count++;
            } catch (Exception e) {
                log.error("创建商品失败: {} - {}", item.name, e.getMessage());
            }
        }
        log.info("已创建 {} 件示例商品", count);
    }

    private String generateImage(String name, int typeId) throws IOException {
        BufferedImage image = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Color bgColor = getBgColor(typeId);
        g.setColor(bgColor);
        g.fillRect(0, 0, IMG_WIDTH, IMG_HEIGHT);

        int circleY = 140;
        int circleR = 80;
        g.setColor(bgColor.brighter());
        g.fillOval(IMG_WIDTH / 2 - circleR, circleY - circleR, circleR * 2, circleR * 2);

        String symbol = name.substring(0, 1);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 64));
        FontMetrics symbolFm = g.getFontMetrics();
        int symbolX = (IMG_WIDTH - symbolFm.stringWidth(symbol)) / 2;
        int symbolY = circleY - circleR / 2 + symbolFm.getAscent() / 2;
        g.drawString(symbol, symbolX, symbolY);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 42));
        FontMetrics fm = g.getFontMetrics();

        String[] lines = splitText(name);
        int totalHeight = lines.length * (fm.getHeight() + 8);
        int startY = IMG_HEIGHT / 2 - totalHeight / 2 + fm.getAscent();

        for (int i = 0; i < lines.length; i++) {
            int textWidth = fm.stringWidth(lines[i]);
            int x = (IMG_WIDTH - textWidth) / 2;
            g.drawString(lines[i], x, startY + i * (fm.getHeight() + 8));
        }

        g.dispose();

        String filename = UUID.randomUUID().toString() + ".png";
        Path target = uploadPath.resolve(filename);
        ImageIO.write(image, "PNG", target.toFile());

        return filename;
    }

    private String[] splitText(String text) {
        if (text.length() <= 6) {
            return new String[] { text };
        }
        int mid = text.length() / 2;
        return new String[] { text.substring(0, mid), text.substring(mid) };
    }

    private Color getBgColor(int typeId) {
        return switch (typeId) {
            case 1 -> new Color(74, 144, 217);
            case 2 -> new Color(139, 105, 20);
            case 3 -> new Color(232, 145, 176);
            case 4 -> new Color(51, 51, 51);
            default -> new Color(128, 128, 128);
        };
    }

    private record SeedItem(String name, int typeId, String style, String price) {
    }
}
