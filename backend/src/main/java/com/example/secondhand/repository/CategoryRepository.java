
package com.example.secondhand.repository;

import com.example.secondhand.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByName(String name);

    List<Category> findByParentId(Integer parentId);

    List<Category> findAllByOrderBySortOrderAsc();

    boolean existsByName(String name);
}
