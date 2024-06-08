package com.asm.controller.admin;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.asm.entity.Orders;
import com.asm.entity.Products;
import com.asm.entity.dao.OrdersDAO;
import com.asm.entity.dao.ProductsDAO;

@Controller
@RequestMapping("admin")
public class AdminController {
	
	@Autowired
	OrdersDAO ordersDAO;
	
	@Autowired
	ProductsDAO productDAO;
	
	@RequestMapping("/home")
    public String home(Model model) {
		 BigDecimal totalTodayAmount = ordersDAO.sumTotalAmountByDate();
		 model.addAttribute("totalTodayAmount", totalTodayAmount != null ? totalTodayAmount : BigDecimal.ZERO);
		 
		long pendingOrdersCount = ordersDAO.countByOrderStatus("Pending");
        model.addAttribute("pendingOrdersCount", pendingOrdersCount);
        
		List<Orders> orders = ordersDAO.findAll();
		model.addAttribute("orders", orders);
		 
		List<Products> products = productDAO.findAll();
		model.addAttribute("products", products);
        return "admin/home";
    }

}