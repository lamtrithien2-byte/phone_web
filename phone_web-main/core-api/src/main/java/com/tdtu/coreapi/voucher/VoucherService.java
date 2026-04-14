package com.tdtu.coreapi.voucher;

import com.tdtu.coreapi.common.BusinessException;
import com.tdtu.coreapi.voucher.dto.VoucherUpsertRequest;
import com.tdtu.coreapi.voucher.dto.VoucherValidationView;
import com.tdtu.coreapi.voucher.dto.VoucherView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
public class VoucherService {

    private static final String CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final VoucherProcedureRepository voucherProcedureRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public VoucherService(VoucherProcedureRepository voucherProcedureRepository) {
        this.voucherProcedureRepository = voucherProcedureRepository;
    }

    public List<VoucherView> getAll(String keyword, String status, String voucherType, Boolean expiredOnly) {
        return voucherProcedureRepository.getAll(keyword, status, voucherType, expiredOnly);
    }

    public VoucherView getById(Long voucherId) {
        return voucherProcedureRepository.getById(voucherId);
    }

    @Transactional
    public Long create(VoucherUpsertRequest request) {
        validateUpsertRequest(request);
        return voucherProcedureRepository.create(normalize(request));
    }

    @Transactional
    public void update(Long voucherId, VoucherUpsertRequest request) {
        validateUpsertRequest(request);
        voucherProcedureRepository.update(voucherId, normalize(request));
    }

    @Transactional
    public void delete(Long voucherId) {
        voucherProcedureRepository.delete(voucherId);
    }

    @Transactional
    public void toggle(Long voucherId, boolean active) {
        voucherProcedureRepository.toggle(voucherId, active);
    }

    public VoucherValidationView validatePublic(String voucherCode, int subtotalMoney, int shippingFee) {
        if (voucherCode == null || voucherCode.isBlank()) {
            throw new BusinessException("Voucher code is required.");
        }
        return voucherProcedureRepository.validatePublic(voucherCode.trim().toUpperCase(), subtotalMoney, shippingFee);
    }

    public String generateCode() {
        StringBuilder builder = new StringBuilder("VC-");
        for (int i = 0; i < 8; i++) {
            builder.append(CODE_ALPHABET.charAt(secureRandom.nextInt(CODE_ALPHABET.length())));
        }
        return builder.toString();
    }

    private void validateUpsertRequest(VoucherUpsertRequest request) {
        String type = request.voucherType() == null ? "" : request.voucherType().trim().toUpperCase();
        if (!type.equals("PERCENT") && !type.equals("FIXED_AMOUNT") && !type.equals("FREE_SHIPPING")) {
            throw new BusinessException("Unsupported voucher type.");
        }
        if (request.startsAt() != null && request.endsAt() != null && request.startsAt().isAfter(request.endsAt())) {
            throw new BusinessException("Voucher start time must be before end time.");
        }
    }

    private VoucherUpsertRequest normalize(VoucherUpsertRequest request) {
        String type = request.voucherType().trim().toUpperCase();
        int discountValue = "FREE_SHIPPING".equals(type) ? 0 : request.discountValue();
        return new VoucherUpsertRequest(
                request.voucherCode().trim().toUpperCase(),
                request.voucherName().trim(),
                type,
                discountValue,
                request.active(),
                request.startsAt(),
                request.endsAt(),
                request.maxUsage(),
                request.minOrderValue()
        );
    }
}
