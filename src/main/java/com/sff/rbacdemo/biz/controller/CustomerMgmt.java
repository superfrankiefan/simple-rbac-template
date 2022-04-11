package com.sff.rbacdemo.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sff.rbacdemo.biz.entity.Customer;
import com.sff.rbacdemo.biz.mapper.CustomerMapper;
import com.sff.rbacdemo.common.controller.BaseController;
import com.sff.rbacdemo.common.model.APIResponse;
import com.sff.rbacdemo.common.properties.GlobalConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Frankie Fan
 * @date 2022-04-10 20:45
 */

@Slf4j
@RestController
@RequestMapping("/biz/customer")
public class CustomerMgmt extends BaseController {

    @Autowired
    private CustomerMapper customerMapper;

    @PostMapping("addOrUpdateCustomer")
    @ResponseBody
    @RequiresAuthentication
    public APIResponse addOrUpdateCustomer(@RequestBody Customer customer){
        if(customer != null & customer.getCustomerCode() != null) {
            QueryWrapper queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("CUSTOMER_CODE", customer.getCustomerCode());
            List custIns = this.customerMapper.selectList(queryWrapper);
            if(custIns.isEmpty()) {
                this.customerMapper.insert(customer);
                return APIResponse.OK("Add Customer", null);
            }else{
                this.customerMapper.updateById(customer);
                return APIResponse.OK("Update Customer", null);
            }
        } else {
            return APIResponse.ERROR(GlobalConstant.REQ_PARAM_ERROR,"客户数据非法",customer);
        }
    }

    @GetMapping("getCustomerList")
    @ResponseBody
    @RequiresAuthentication
    public APIResponse getCustomerList(@RequestParam(required = false, value = "customerCode") String customerCode,
                                   @RequestParam(required = false, value = "customerName") String customerName) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        if (customerCode != null) {
            queryWrapper.eq("CUSTOMER_CODE", customerCode);
        }
        if (customerName != null) {
            queryWrapper.like("CUSTOMER_NAME", customerName);
        }
        return APIResponse.OK("customers", this.customerMapper.selectList(queryWrapper));
    }

    @DeleteMapping("deleteCustomers")
    @ResponseBody
    @RequiresAuthentication
    public APIResponse deleteCustomers(@RequestBody Map<String, String> customerIds) {
        this.customerMapper.deleteById(customerIds.get("customerIds"));
        return APIResponse.OK("Delete Customers", null);
    }

}