package com.pindou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pindou.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Select("<script>" +
            "SELECT p.*, c.name as category_name FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.id " +
            "<where>" +
            "  <if test='categoryId != null'>AND p.category_id = #{categoryId}</if>" +
            "  <if test='keyword != null and keyword != \"\"'>AND (p.name LIKE CONCAT('%',#{keyword},'%') OR p.ip_series LIKE CONCAT('%',#{keyword},'%') OR p.character_name LIKE CONCAT('%',#{keyword},'%'))</if>" +
            "  <if test='status != null'>AND p.status = #{status}</if>" +
            "</where>" +
            "ORDER BY p.create_time DESC" +
            "</script>")
    IPage<Product> selectProductPage(Page<Product> page,
                                     @Param("categoryId") Long categoryId,
                                     @Param("keyword") String keyword,
                                     @Param("status") Integer status);
}
