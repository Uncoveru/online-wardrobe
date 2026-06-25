package com.wardrobe.backend.controller;

import com.wardrobe.backend.dto.Result;
import com.wardrobe.backend.entity.Clothes;
import com.wardrobe.backend.entity.Size;
import com.wardrobe.backend.entity.Type;
import com.wardrobe.backend.enums.RolePermission;
import com.wardrobe.backend.exception.ForbiddenException;
import com.wardrobe.backend.mapper.SizeMapper;
import com.wardrobe.backend.mapper.TypeMapper;
import com.wardrobe.backend.service.ClothesService;
import com.wardrobe.backend.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ClothesController {

    private final ClothesService clothesService;
    private final TypeMapper typeMapper;
    private final SizeMapper sizeMapper;

    public ClothesController(ClothesService clothesService, TypeMapper typeMapper, SizeMapper sizeMapper) {
        this.clothesService = clothesService;
        this.typeMapper = typeMapper;
        this.sizeMapper = sizeMapper;
    }

    @GetMapping("/clothes")
    public Result<List<Clothes>> listClothes(HttpServletRequest request) {
        Integer operatorId = getOperatorIdForFilter(request);
        return Result.ok(clothesService.getClothesList(operatorId));
    }

    @GetMapping("/clothes/{id}")
    public Result<Clothes> getClothes(@PathVariable Integer id) {
        Clothes clothes = clothesService.getClothesById(id);
        if (clothes == null) {
            return Result.fail("服装不存在");
        }
        return Result.ok(clothes);
    }

    @GetMapping("/clothes/search")
    public Result<List<Clothes>> searchClothes(
            @RequestParam(required = false) String clothName,
            @RequestParam(required = false) String style,
            @RequestParam(required = false) Integer typeId,
            HttpServletRequest request) {
        Integer operatorId = getOperatorIdForFilter(request);
        return Result.ok(clothesService.getClothesByParams(clothName, style, typeId, operatorId));
    }

    @PostMapping("/clothes")
    public Result<Clothes> addClothes(
            @RequestParam String clothName,
            @RequestParam Integer typeId,
            @RequestParam String style,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) MultipartFile file,
            HttpServletRequest request) {
        requirePermission(request, RolePermission.Permission.CLOTHES_MANAGE);
        Integer operatorId = AuthUtils.getUserId(request);
        Clothes clothes = new Clothes();
        clothes.setClothName(clothName);
        clothes.setTypeId(typeId);
        clothes.setStyle(style);
        clothes.setPrice(price);
        Clothes saved = clothesService.addClothes(clothes, file, operatorId);
        return Result.ok(saved);
    }

    @PostMapping("/clothes/{id}")
    public Result<Clothes> updateClothes(
            @PathVariable Integer id,
            @RequestParam String clothName,
            @RequestParam Integer typeId,
            @RequestParam String style,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) MultipartFile file,
            HttpServletRequest request) {
        requirePermission(request, RolePermission.Permission.CLOTHES_MANAGE);
        Integer operatorId = getOperatorIdForFilter(request);
        Clothes clothes = new Clothes();
        clothes.setId(id);
        clothes.setClothName(clothName);
        clothes.setTypeId(typeId);
        clothes.setStyle(style);
        clothes.setPrice(price);
        Clothes updated = clothesService.updateClothes(clothes, file, operatorId);
        return Result.ok(updated);
    }

    @DeleteMapping("/clothes/{id}")
    public Result<Void> deleteClothes(@PathVariable Integer id, HttpServletRequest request) {
        requirePermission(request, RolePermission.Permission.CLOTHES_MANAGE);
        Integer operatorId = getOperatorIdForFilter(request);
        clothesService.deleteClothes(id, operatorId);
        return Result.ok();
    }

    @GetMapping("/types")
    public Result<List<Type>> listTypes() {
        return Result.ok(typeMapper.findAll());
    }

    @GetMapping("/sizes")
    public Result<List<Size>> listSizes(@RequestParam Integer typeId) {
        return Result.ok(sizeMapper.findByTypeId(typeId));
    }

    private Integer getOperatorIdForFilter(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role == 1 || role == 2) {
            return null;
        }
        Integer userId = (Integer) request.getAttribute("userId");
        return userId;
    }

    private void requirePermission(HttpServletRequest request, String permission) {
        Integer role = AuthUtils.getRole(request);
        if (!RolePermission.fromId(role).hasPermission(permission)) {
            throw new ForbiddenException("无权限，仅管理员可操作");
        }
    }
}
