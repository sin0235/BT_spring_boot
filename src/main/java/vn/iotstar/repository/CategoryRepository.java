package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iotstar.entity.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    List<Category> findByUserId(Integer userId);
    
    @Query("SELECT c FROM Category c WHERE c.catename LIKE %:name%")
    List<Category> findByCatenameContaining(@Param("name") String name);
    
    @Query("SELECT c FROM Category c WHERE c.userId = :userId AND c.catename LIKE %:name%")
    List<Category> findByUserIdAndCatenameContaining(@Param("userId") Integer userId, @Param("name") String name);
}