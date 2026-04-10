package com.tdtu.coreapi.invoice;

import com.tdtu.coreapi.invoice.dto.CreateInvoiceRequest;
import com.tdtu.coreapi.invoice.dto.InvoiceCreatedView;
import com.tdtu.coreapi.invoice.dto.InvoiceDetailView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceProcedureRepository invoiceProcedureRepository;
    private final InvoicePdfService invoicePdfService;

    public InvoiceService(InvoiceProcedureRepository invoiceProcedureRepository,
                          InvoicePdfService invoicePdfService) {
        this.invoiceProcedureRepository = invoiceProcedureRepository;
        this.invoicePdfService = invoicePdfService;
    }

    @Transactional
    public InvoiceCreatedView createInvoice(CreateInvoiceRequest request) {
        Long customerId = invoiceProcedureRepository.ensureCustomer(
                request.phoneNumber(),
                request.fullName(),
                request.address() == null ? "" : request.address()
        );

        InvoiceCreatedView invoice = invoiceProcedureRepository.createInvoice(request, customerId);
        request.items().forEach(item -> {
            invoiceProcedureRepository.addInvoiceItem(invoice.invoiceId(), item);
        });
        invoiceProcedureRepository.completeCheckout(request.staffId());
        String pdfLink = invoicePdfService.generate(invoice, request);
        invoiceProcedureRepository.attachPdf(invoice.invoiceCode(), pdfLink);
        return new InvoiceCreatedView(
                invoice.invoiceId(),
                invoice.invoiceCode(),
                invoice.customerId(),
                pdfLink
        );
    }

    public List<InvoiceDetailView> getInvoiceDetail(String invoiceCode) {
        return invoiceProcedureRepository.getInvoiceDetail(invoiceCode);
    }
}
