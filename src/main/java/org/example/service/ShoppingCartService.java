package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.R;
import org.example.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    R<String> clean();
}
