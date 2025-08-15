package com.monesh.venkateswaramotors.features.vmservice.servicecenter.customers.controller;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.service.AdminAuthenticationService;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.customers.dto.CustomerHistoryResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.customers.dto.CustomerListResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.customers.service.CustomerHistoryService;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.customers.service.CustomerQueryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vm/api/service-center/customers")
public class CustomerHistoryController {

    @Autowired
    private CustomerHistoryService customerHistoryService;

    @Autowired
    private CustomerQueryService customerQueryService;

    @Autowired
    private AdminAuthenticationService adminAuthenticationService;

    @GetMapping
    public ResponseEntity<?> listCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            HttpServletRequest httpRequest) {

        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        CustomerListResponse response = customerQueryService.listCustomers(page, size, search);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getCustomerHistoryByRegistration(
            @RequestParam("registration") String registration,
            HttpServletRequest httpRequest) {

        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        CustomerHistoryResponse response = customerHistoryService.getHistoryByVehicleRegistration(registration);
        return ResponseEntity.ok(response);
    }
}
