package com.tdtu.coreapi.invoice;

import com.tdtu.coreapi.common.ApiResponse;
import com.tdtu.coreapi.invoice.dto.CreateInvoiceRequest;
import com.tdtu.coreapi.invoice.dto.InvoiceCreatedView;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tdtu.coreapi.customer.CustomerProcedureRepository;
import com.tdtu.coreapi.customer.dto.CustomerView;
import com.tdtu.coreapi.customer.dto.PurchaseHistoryView;
import com.tdtu.coreapi.invoice.dto.InvoiceDetailView;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final CustomerProcedureRepository customerRepository;
    private final InvoicePdfService invoicePdfService;

    public InvoiceController(InvoiceService invoiceService,
                             CustomerProcedureRepository customerRepository,
                             InvoicePdfService invoicePdfService) {
        this.invoiceService = invoiceService;
        this.customerRepository = customerRepository;
        this.invoicePdfService = invoicePdfService;
    }

    @PostMapping
    public ApiResponse<InvoiceCreatedView> create(@Valid @RequestBody CreateInvoiceRequest request) {
        return ApiResponse.success(invoiceService.createInvoice(request));
    }

    @GetMapping("/customer/{phoneNumber}")
    public ApiResponse<CustomerView> getCustomer(@PathVariable String phoneNumber) {
        return ApiResponse.success(customerRepository.findByPhoneNumber(phoneNumber));
    }

    @GetMapping("/customer/{customerId}/history")
    public ApiResponse<List<PurchaseHistoryView>> getHistory(@PathVariable Long customerId) {
        return ApiResponse.success(customerRepository.getPurchaseHistory(customerId));
    }

    @GetMapping("/{invoiceCode}/details")
    public ApiResponse<List<InvoiceDetailView>> getInvoiceDetail(@PathVariable String invoiceCode) {
        return ApiResponse.success(invoiceService.getInvoiceDetail(invoiceCode));
    }

    @GetMapping("/{invoiceCode}/pdf")
    public ResponseEntity<Resource> downloadInvoicePdf(@PathVariable String invoiceCode) {
        Path pdfPath = invoicePdfService.resolve(invoiceCode);
        if (!Files.exists(pdfPath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(pdfPath);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + invoiceCode + ".pdf\"")
                .body(resource);
    }
}
