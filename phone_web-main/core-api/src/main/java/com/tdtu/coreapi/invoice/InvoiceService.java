package com.tdtu.coreapi.invoice;

import com.tdtu.coreapi.common.BusinessException;
import com.tdtu.coreapi.invoice.dto.CreateInvoiceRequest;
import com.tdtu.coreapi.invoice.dto.InvoiceCreatedView;
import com.tdtu.coreapi.invoice.dto.InvoiceDetailView;
import com.tdtu.coreapi.voucher.VoucherProcedureRepository;
import com.tdtu.coreapi.voucher.dto.VoucherValidationView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceProcedureRepository invoiceProcedureRepository;
    private final InvoicePdfService invoicePdfService;
    private final VoucherProcedureRepository voucherProcedureRepository;

    public InvoiceService(InvoiceProcedureRepository invoiceProcedureRepository,
                          InvoicePdfService invoicePdfService,
                          VoucherProcedureRepository voucherProcedureRepository) {
        this.invoiceProcedureRepository = invoiceProcedureRepository;
        this.invoicePdfService = invoicePdfService;
        this.voucherProcedureRepository = voucherProcedureRepository;
    }

    @Transactional
    public InvoiceCreatedView createInvoice(CreateInvoiceRequest request) {
        int subtotalMoney = request.items().stream()
                .mapToInt(item -> item.totalMoney())
                .sum();
        int quantity = request.items().stream()
                .mapToInt(item -> item.quantity())
                .sum();
        VoucherValidationView voucher = null;
        int discountMoney = 0;
        if (request.voucherCode() != null && !request.voucherCode().isBlank()) {
            voucher = voucherProcedureRepository.validatePublic(request.voucherCode().trim().toUpperCase(), subtotalMoney, 0);
            discountMoney = voucher.discountMoney();
        }
        int totalMoney = Math.max(subtotalMoney - discountMoney, 0);
        if (request.receiveMoney() < totalMoney) {
            throw new BusinessException("Receive money is not enough to complete the invoice.");
        }

        Long customerId = invoiceProcedureRepository.ensureCustomer(
                request.phoneNumber(),
                request.fullName(),
                request.address() == null ? "" : request.address()
        );

        CreateInvoiceRequest normalizedRequest = new CreateInvoiceRequest(
                request.staffId(),
                request.phoneNumber(),
                request.fullName(),
                request.address(),
                quantity,
                subtotalMoney,
                discountMoney,
                totalMoney,
                request.receiveMoney(),
                Math.max(request.receiveMoney() - totalMoney, 0),
                voucher != null ? voucher.voucherCode() : null,
                request.items()
        );

        InvoiceCreatedView invoice = invoiceProcedureRepository.createInvoice(normalizedRequest, customerId);
        request.items().forEach(item -> {
            invoiceProcedureRepository.addInvoiceItem(invoice.invoiceId(), item);
        });
        if (voucher != null) {
            invoiceProcedureRepository.consumeVoucherForInvoice(
                    invoice.invoiceId(),
                    voucher.voucherCode(),
                    subtotalMoney,
                    0
            );
        }
        invoiceProcedureRepository.completeCheckout(request.staffId());
        String pdfLink = invoicePdfService.generate(invoice, normalizedRequest);
        invoiceProcedureRepository.attachPdf(invoice.invoiceCode(), pdfLink);
        return new InvoiceCreatedView(
                invoice.invoiceId(),
                invoice.invoiceCode(),
                invoice.customerId(),
                pdfLink,
                subtotalMoney,
                discountMoney,
                totalMoney,
                voucher != null ? voucher.voucherCode() : null
        );
    }

    public List<InvoiceDetailView> getInvoiceDetail(String invoiceCode) {
        return invoiceProcedureRepository.getInvoiceDetail(invoiceCode);
    }
}
