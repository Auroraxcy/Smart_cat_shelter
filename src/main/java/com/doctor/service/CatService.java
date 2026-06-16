package com.doctor.service;

import com.doctor.entity.Cat;
import java.util.List;

public interface CatService {
    List<Cat> getAllCats();
    Cat getCatById(Long id);
    Cat createCat(Cat cat);
    Cat updateCat(Long id, Cat cat);
    void deleteCat(Long id);
}
