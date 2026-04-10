package com.tdtu.coreapi.customer;

import com.tdtu.coreapi.customer.dto.CustomerView;
import com.tdtu.coreapi.customer.dto.PurchaseHistoryView;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerProcedureRepository customerProcedureRepository;

    public CustomerService(CustomerProcedureRepository customerProcedureRepository) {
        this.customerProcedureRepository = customerProcedureRepository;
    }

    public CustomerView findByPhoneNumber(String phoneNumber) {
        return customerProcedureRepository.findByPhoneNumber(phoneNumber);
    }

    public List<PurchaseHistoryView> getPurchaseHistory(Long customerId) {
        return customerProcedureRepository.getPurchaseHistory(customerId);
    }
}
