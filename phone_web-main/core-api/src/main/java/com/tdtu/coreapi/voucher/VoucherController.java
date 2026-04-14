package com.tdtu.coreapi.voucher;

import com.tdtu.coreapi.common.ApiResponse;
import com.tdtu.coreapi.common.BusinessException;
import com.tdtu.coreapi.voucher.dto.VoucherToggleRequest;
import com.tdtu.coreapi.voucher.dto.VoucherUpsertRequest;
import com.tdtu.coreapi.voucher.dto.VoucherValidationView;
import com.tdtu.coreapi.voucher.dto.VoucherView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public ApiResponse<List<VoucherView>> getAll(@RequestParam(required = false) String keyword,
                                                 @RequestParam(required = false) String status,
                                                 @RequestParam(required = false) String voucherType,
                                                 @RequestParam(required = false) Boolean expiredOnly) {
        return ApiResponse.success(voucherService.getAll(keyword, status, voucherType, expiredOnly));
    }

    @GetMapping("/{voucherId}")
    public ApiResponse<VoucherView> getById(@PathVariable Long voucherId) {
        try {
            return ApiResponse.success(voucherService.getById(voucherId));
        } catch (BusinessException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @GetMapping("/generate-code")
    public ApiResponse<String> generateCode() {
        return ApiResponse.success(voucherService.generateCode());
    }

    @GetMapping("/validate")
    public ApiResponse<VoucherValidationView> validate(@RequestParam String code,
                                                       @RequestParam @Min(0) Integer subtotalMoney,
                                                       @RequestParam(defaultValue = "0") @Min(0) Integer shippingFee) {
        try {
            return ApiResponse.success(voucherService.validatePublic(code, subtotalMoney, shippingFee));
        } catch (BusinessException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody VoucherUpsertRequest request) {
        try {
            return ApiResponse.success(voucherService.create(request));
        } catch (BusinessException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @PutMapping("/{voucherId}")
    public ApiResponse<Void> update(@PathVariable Long voucherId,
                                    @Valid @RequestBody VoucherUpsertRequest request) {
        try {
            voucherService.update(voucherId, request);
            return ApiResponse.success(null);
        } catch (BusinessException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @PutMapping("/{voucherId}/toggle")
    public ApiResponse<Void> toggle(@PathVariable Long voucherId,
                                    @Valid @RequestBody VoucherToggleRequest request) {
        try {
            voucherService.toggle(voucherId, request.active());
            return ApiResponse.success(null);
        } catch (BusinessException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @DeleteMapping("/{voucherId}")
    public ApiResponse<Void> delete(@PathVariable Long voucherId) {
        try {
            voucherService.delete(voucherId);
            return ApiResponse.success(null);
        } catch (BusinessException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }
}
