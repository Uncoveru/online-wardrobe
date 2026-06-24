package com.wardrobe.backend.service;

import com.wardrobe.backend.entity.Clothes;
import com.wardrobe.backend.mapper.ClothesMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wardrobe.backend.exception.BusinessException;

@Service
public class ClothesService {

    private static final Logger log = LoggerFactory.getLogger(ClothesService.class);

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    private final ClothesMapper clothesMapper;
    private final Path uploadPath;

    public ClothesService(ClothesMapper clothesMapper, @Value("${upload.path:upload/images}") String uploadDir) {
        this.clothesMapper = clothesMapper;
        this.uploadPath = Paths.get(uploadDir);
    }

    public List<Clothes> getClothesList() {
        return clothesMapper.findAll();
    }

    public List<Clothes> getClothesByParams(String clothName, String style, Integer typeId) {
        return clothesMapper.findByParams(clothName, style, typeId);
    }

    public Clothes getClothesById(Integer id) {
        return clothesMapper.findById(id);
    }

    public Clothes addClothes(Clothes clothes, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            String filename = saveFile(file);
            clothes.setImage(filename);
        }
        clothesMapper.insert(clothes);
        return clothes;
    }

    public Clothes updateClothes(Clothes clothes, MultipartFile file) {
        Clothes existing = clothesMapper.findById(clothes.getId());
        if (existing == null) {
            throw new BusinessException(404, "服装不存在");
        }

        String oldImage = existing.getImage();

        if (file != null && !file.isEmpty()) {
            String filename = saveFile(file);
            clothes.setImage(filename);
        } else {
            clothes.setImage(existing.getImage());
        }

        clothesMapper.update(clothes);

        if (file != null && !file.isEmpty() && oldImage != null) {
            try {
                Files.deleteIfExists(uploadPath.resolve(oldImage));
            } catch (IOException e) {
                log.warn("删除旧图片失败: {}", e.getMessage());
            }
        }

        return clothes;
    }

    public void deleteClothes(Integer id) {
        Clothes clothes = clothesMapper.findById(id);
        if (clothes == null) {
            throw new BusinessException(404, "服装不存在");
        }

        clothesMapper.deleteById(id);

        if (clothes.getImage() != null) {
            try {
                Files.deleteIfExists(uploadPath.resolve(clothes.getImage()));
            } catch (IOException e) {
                log.warn("删除图片失败: {}", e.getMessage());
            }
        }
    }

    private String saveFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && !List.of("image/jpeg", "image/png", "image/gif", "image/webp")
                .contains(contentType)) {
            log.warn("文件类型异常: {}", contentType);
        }

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }
        if (ext.isEmpty() || !ALLOWED_EXTENSIONS.contains(ext)) {
            throw new BusinessException(400, "不支持的文件格式，仅允许 jpg/png/gif/webp");
        }

        try {
            Files.createDirectories(uploadPath);
            String filename = UUID.randomUUID().toString() + ext;
            Path target = uploadPath.resolve(filename);
            file.transferTo(target.toFile());
            return filename;
        } catch (IOException e) {
            throw new BusinessException(500, "文件上传失败");
        }
    }
}
