package com.app.finance_tracker.model.repository;

import com.app.finance_tracker.model.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    Optional<Category> findByName(String name);

    Optional<Category> findCategoryFromUserByNameAndUserId(String name, long userId);

    Optional<Category> findByIdAndUserId( long categoryId, long userId);

    Optional<Category> findByNameAndUserIsNull(String name);

    List<Category> findAllByUserId(long userId);

    List<Category> findAllByUserIsNull();
}
