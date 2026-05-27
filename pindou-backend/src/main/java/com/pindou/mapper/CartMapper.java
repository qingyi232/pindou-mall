package com.pindou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pindou.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    @Select("SELECT c.*, p.name as product_name, p.main_image as product_image, " +
            "p.price as product_price, p.stock as product_stock " +
            "FROM carts c LEFT JOIN products p ON c.product_id = p.id " +
            "WHERE c.user_id = #{userId} ORDER BY c.create_time DESC")
    List<Cart> selectCartWithProduct(Long userId);
}
