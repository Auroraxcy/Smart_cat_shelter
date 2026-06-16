package com.doctor.service.impl;

import com.doctor.entity.Cat;
import com.doctor.exception.BusinessException;
import com.doctor.repository.CatRepository;
import com.doctor.service.CatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatServiceImpl implements CatService {

    @Autowired
    private CatRepository catRepository;

    @Override
    public List<Cat> getAllCats() {
        return catRepository.findAll();
    }

    @Override
    public Cat getCatById(Long id) {
        return catRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "猫咪不存在"));
    }

    @Override
    public Cat createCat(Cat cat) {
        return catRepository.save(cat);
    }

    @Override
    public Cat updateCat(Long id, Cat catDetails) {
        Cat cat = getCatById(id);
        cat.setName(catDetails.getName());
        cat.setBreed(catDetails.getBreed());
        cat.setColor(catDetails.getColor());
        cat.setAge(catDetails.getAge());
        cat.setGender(catDetails.getGender());
        cat.setWeight(catDetails.getWeight());
        cat.setHealthStatus(catDetails.getHealthStatus());
        cat.setPhoto(catDetails.getPhoto());
        cat.setDescription(catDetails.getDescription());
        return catRepository.save(cat);
    }

    @Override
    public void deleteCat(Long id) {
        Cat cat = getCatById(id);
        catRepository.delete(cat);
    }
}
