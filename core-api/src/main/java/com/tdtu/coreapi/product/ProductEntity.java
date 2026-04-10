package com.tdtu.coreapi.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bar_code")
    private String barCode;

    private String name;

    @Column(name = "screen_size")
    private String screenSize;

    private String ram;

    private String rom;

    @Column(name = "import_price")
    private Integer importPrice;

    @Column(name = "price_sale")
    private Integer priceSale;

    private String description;

    @Column(name = "image_link")
    private String imageLink;

    @Column(name = "sale_number")
    private Integer saleNumber;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "category_id")
    private Long categoryId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBarCode() { return barCode; }
    public void setBarCode(String barCode) { this.barCode = barCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getScreenSize() { return screenSize; }
    public void setScreenSize(String screenSize) { this.screenSize = screenSize; }
    public String getRam() { return ram; }
    public void setRam(String ram) { this.ram = ram; }
    public String getRom() { return rom; }
    public void setRom(String rom) { this.rom = rom; }
    public Integer getImportPrice() { return importPrice; }
    public void setImportPrice(Integer importPrice) { this.importPrice = importPrice; }
    public Integer getPriceSale() { return priceSale; }
    public void setPriceSale(Integer priceSale) { this.priceSale = priceSale; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageLink() { return imageLink; }
    public void setImageLink(String imageLink) { this.imageLink = imageLink; }
    public Integer getSaleNumber() { return saleNumber; }
    public void setSaleNumber(Integer saleNumber) { this.saleNumber = saleNumber; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public Date getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}
