package com.example.taskmanager.repository;

import com.example.taskmanager.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Hämta alla kategorier som tillhör en viss användare
    List<Category> findByUserUserid(Long userid);

    // Hitta en specifik kategori om den tillhör en viss användare (för säkerhetskontroller)
    Optional<Category> findByIdAndUserUserid(Long id, Long userid);

    // Slå upp på namn för en användare (används vid validering — finns kategorin redan?)
    Optional<Category> findByNameAndUserUserid(String name, Long userid);
}