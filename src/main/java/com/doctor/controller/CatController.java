package com.doctor.controller;

import com.doctor.dto.ApiResponse;
import com.doctor.entity.Cat;
import com.doctor.service.CatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cats")
public class CatController {

    @Autowired
    private CatService catService;

    @GetMapping
    public ApiResponse<?> getAllCats() {
        try {
            List<Cat> cats = catService.getAllCats();
            return ApiResponse.success(cats);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getCatById(@PathVariable Long id) {
        try {
            Cat cat = catService.getCatById(id);
            return ApiResponse.success(cat);
        } catch (Exception e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<?> createCat(@RequestBody Cat cat) {
        try {
            Cat createdCat = catService.createCat(cat);
            return ApiResponse.success("添加成功", createdCat);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<?> updateCat(@PathVariable Long id, @RequestBody Cat cat) {
        try {
            Cat updatedCat = catService.updateCat(id, cat);
            return ApiResponse.success("更新成功", updatedCat);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteCat(@PathVariable Long id) {
        try {
            catService.deleteCat(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
}
