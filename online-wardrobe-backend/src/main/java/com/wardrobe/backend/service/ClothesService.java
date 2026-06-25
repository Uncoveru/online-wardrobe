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
import com.wardrobe.backend.exception.ForbiddenException;

/**
 * 商品服务：CRUD + 图片上传
 */
@Service
public class ClothesService {

    private static final Logger log = LoggerFactory.getLogger(ClothesService.class);

    // 允许的图片扩展名
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    private final ClothesMapper clothesMapper;
    private final Path uploadPath;

    public ClothesService(ClothesMapper clothesMapper, @Value("${upload.path:upload/images}") String uploadDir) {
        this.clothesMapper = clothesMapper;
        this.uploadPath = Paths.get(uploadDir);
    }

    // 商品列表（operationId 不为空时只查该操作员的商品）
    public List<Clothes> getClothesList(Integer operatorId) {
        return clothesMapper.findAll(operatorId);
    }

    // 多条件搜索
    public List<Clothes> getClothesByParams(String clothName, String style, Integer typeId, Integer operatorId) {
        return clothesMapper.findByParams(clothName, style, typeId, operatorId);
    }

    // 商品详情
    public Clothes getClothesById(Integer id) {
        return clothesMapper.findById(id);
    }

    // 新增商品（含图片上传）
    public Clothes addClothes(Clothes clothes, MultipartFile file, Integer operatorId) {
        if (file != null && !file.isEmpty()) {
            String filename = saveFile(file);
            clothes.setImage(filename);
        }
        clothes.setOperatorId(operatorId);
        clothesMapper.insert(clothes);
        return clothes;
    }

    // 更新商品（含图片替换，操作员只能改自己的商品）
    public Clothes updateClothes(Clothes clothes, MultipartFile file, Integer operatorId) {
        Clothes existing = clothesMapper.findById(clothes.getId());
        if (existing == null) {
            throw new BusinessException(404, "服装不存在");
        }

        if (operatorId != null && !operatorId.equals(existing.getOperatorId())) {
            throw new ForbiddenException("无权修改他人的服装");
        }

        String oldImage = existing.getImage();

        if (file != null && !file.isEmpty()) {
            String filename = saveFile(file);
            clothes.setImage(filename);
        } else {
            clothes.setImage(existing.getImage());
        }

        clothesMapper.update(clothes);

        // 替换图片后删除旧文件
        if (file != null && !file.isEmpty() && oldImage != null) {
            try {
                Files.deleteIfExists(uploadPath.resolve(oldImage));
            } catch (IOException e) {
                log.warn("删除旧图片失败: {}", e.getMessage());
            }
        }

        return clothes;
    }

    // 删除商品（同时删除图片文件，操作员只能删自己的商品）
    public void deleteClothes(Integer id, Integer operatorId) {
        Clothes clothes = clothesMapper.findById(id);
        if (clothes == null) {
            throw new BusinessException(404, "服装不存在");
        }

        if (operatorId != null && !operatorId.equals(clothes.getOperatorId())) {
            throw new ForbiddenException("无权删除他人的服装");
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

    // 保存上传文件到磁盘，返回随机文件名
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
            file.transferTo(target.toAbsolutePath().toFile());
            return filename;
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.toString());
            throw new BusinessException(500, "文件上传失败: " + e.getMessage());
        }
    }
}
