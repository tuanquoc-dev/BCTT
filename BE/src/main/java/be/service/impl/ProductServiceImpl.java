package be.service.impl;

import be.dto.request.CreateProductRequest;
import be.dto.request.UpdateProductRequest;
import be.dto.response.ImageResponse;
import be.dto.response.ProductResponse;
import be.dto.response.ProductVariantResponse;
import be.entity.*;
import be.enums.CommonStatus;
import be.enums.DiscountType;
import be.exception.AppException;
import be.exception.ErrorCode;
import be.repository.*;
import be.service.CloudinaryService;
import be.service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final DiscountRepository discountRepository;
    private final ImageRepository imageRepository;
    private final CloudinaryService cloudinaryService;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    // =====================================================
    // CREATE
    // =====================================================

    @Override
    public ProductResponse create(CreateProductRequest request,
                                  MultipartFile thumbnail,
                                  List<MultipartFile> images) {

        if (images != null && images.size() > 10) {
            throw new AppException(ErrorCode.IMAGE_LIMIT_EXCEEDED);
        }

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() ->
                        new AppException(ErrorCode.BRAND_NOT_FOUND));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Product product = Product.builder()
                .name(request.getName())
                .slug(generateVariantSlug(request))
                .parentSlug(
                        request.getParentSlug() != null
                                ? request.getParentSlug()
                                : generateParentSlug(request.getName())
                )
                .description(request.getDescription())
                .price(BigDecimal.valueOf(request.getPrice()))
                .stock(request.getStock() != null ? request.getStock() : 0)
                .sku(request.getSku())
                .color(request.getColor())
                .ram(request.getRam())
                .brand(brand)
                .category(category)
                .status(
                        request.getStatus() != null
                                ? request.getStatus()
                                : CommonStatus.ACTIVE
                )
                .soldQuantity(0)
                .viewCount(0)
                .rating(0f)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // ================= DISCOUNT =================

        if (request.getDiscountId() != null) {

            Discount discount = discountRepository
                    .findById(request.getDiscountId())
                    .orElseThrow(() ->
                            new AppException(ErrorCode.DISCOUNT_NOT_FOUND));

            product.setDiscount(discount);
        }

        // ================= THUMBNAIL =================

        if (thumbnail != null && !thumbnail.isEmpty()) {

            validateImage(thumbnail);

            Map<String, String> upload =
                    cloudinaryService.upload(thumbnail);

            product.setThumbnail(upload.get("url"));
            product.setThumbnailPublicId(upload.get("publicId"));
        }

        productRepository.save(product);

        // ================= IMAGES =================

        if (images != null && !images.isEmpty()) {

            int index = 0;

            for (MultipartFile file : images) {

                if (file == null || file.isEmpty()) continue;

                validateImage(file);

                Map<String, String> upload =
                        cloudinaryService.upload(file);

                Image image = Image.builder()
                        .product(product)
                        .imgUrl(upload.get("url"))
                        .publicId(upload.get("publicId"))
                        .sortOrder(index++)
                        .build();

                imageRepository.save(image);
            }
        }

        return getById(product.getId());
    }

    // =====================================================
    // UPDATE
    // =====================================================

    @Override
    public ProductResponse update(Integer id,
                                  UpdateProductRequest request,
                                  MultipartFile thumbnail,
                                  List<MultipartFile> newImages,
                                  List<Integer> deleteImageIds) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // 🔥 validate image limit
        int currentImageCount =
                imageRepository.findByProductId(product.getId()).size();

        int deleteCount =
                deleteImageIds != null ? deleteImageIds.size() : 0;

        int newCount =
                newImages != null ? newImages.size() : 0;

        int finalCount =
                currentImageCount - deleteCount + newCount;

        if (finalCount > 10) {
            throw new AppException(ErrorCode.IMAGE_LIMIT_EXCEEDED);
        }

        // ================= BASIC =================

        if (request.getName() != null &&
                !request.getName().isBlank()) {

            product.setName(request.getName());
            String finalName =
                    request.getName() != null
                            ? request.getName()
                            : product.getName();

            String finalColor =
                    request.getColor() != null
                            ? request.getColor()
                            : product.getColor();

            String finalRam =
                    request.getRam() != null
                            ? request.getRam()
                            : product.getRam();

            String rawSlug = finalName;

            if (finalColor != null && !finalColor.isBlank()) {
                rawSlug += "-" + finalColor;
            }

            if (finalRam != null && !finalRam.isBlank()) {
                rawSlug += "-" + finalRam;
            }

            product.setSlug(generateSlug(rawSlug));
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if (request.getPrice() != null) {
            product.setPrice(BigDecimal.valueOf(request.getPrice()));
        }

        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }

        if (request.getSku() != null) {
            product.setSku(request.getSku());
        }

        if (request.getColor() != null) {
            product.setColor(request.getColor());
        }

        if (request.getRam() != null) {
            product.setRam(request.getRam());
        }

        if (request.getName() != null &&
                !request.getName().isBlank()) {

            product.setName(request.getName());

            if (request.getParentSlug() == null ||
                    request.getParentSlug().isBlank()) {

                product.setParentSlug(
                        generateParentSlug(request.getName())
                );
            }
        }

        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }

        // ================= BRAND =================

        if (request.getBrandId() != null) {

            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() ->
                            new AppException(ErrorCode.BRAND_NOT_FOUND));

            product.setBrand(brand);
        }

        // ================= CATEGORY =================

        if (request.getCategoryId() != null) {

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() ->
                            new AppException(ErrorCode.CATEGORY_NOT_FOUND));

            product.setCategory(category);
        }

        // ================= DISCOUNT =================

        if (request.getDiscountId() != null) {

            Discount discount =
                    discountRepository.findById(request.getDiscountId())
                            .orElseThrow(() ->
                                    new AppException(ErrorCode.DISCOUNT_NOT_FOUND));

            product.setDiscount(discount);

        } else {

            product.setDiscount(null);
        }

        // ================= THUMBNAIL =================

        if (thumbnail != null && !thumbnail.isEmpty()) {

            validateImage(thumbnail);

            if (product.getThumbnailPublicId() != null) {

                cloudinaryService.delete(
                        product.getThumbnailPublicId()
                );
            }

            Map<String, String> upload =
                    cloudinaryService.upload(thumbnail);

            product.setThumbnail(upload.get("url"));
            product.setThumbnailPublicId(upload.get("publicId"));
        }

        // ================= DELETE IMAGES =================

        if (deleteImageIds != null &&
                !deleteImageIds.isEmpty()) {

            List<Image> deleteImages =
                    imageRepository.findAllById(deleteImageIds);

            for (Image image : deleteImages) {

                // delete cloudinary
                if (image.getPublicId() != null) {

                    cloudinaryService.delete(
                            image.getPublicId()
                    );
                }
            }

            imageRepository.deleteAll(deleteImages);
        }

        // ================= ADD NEW IMAGES =================

        if (newImages != null &&
                !newImages.isEmpty()) {

            int index =
                    imageRepository.findByProductId(product.getId()).size();

            for (MultipartFile file : newImages) {

                if (file == null || file.isEmpty()) continue;

                validateImage(file);

                Map<String, String> upload =
                        cloudinaryService.upload(file);

                Image image = Image.builder()
                        .product(product)
                        .imgUrl(upload.get("url"))
                        .publicId(upload.get("publicId"))
                        .sortOrder(index++)
                        .build();

                imageRepository.save(image);
            }
        }

        product.setUpdatedAt(LocalDateTime.now());

        productRepository.save(product);

        return getById(product.getId());
    }

    // =====================================================
    // DELETE
    // =====================================================

    @Override
    public void delete(Integer id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        product.setStatus(CommonStatus.INACTIVE);

        product.setUpdatedAt(LocalDateTime.now());

        productRepository.save(product);
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    @Override
    public ProductResponse getById(Integer id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        return mapProduct(product);
    }

    // =====================================================
    // SEARCH
    // =====================================================

    @Override
    public Page<ProductResponse> search(String keyword,
                                        Integer brandId,
                                        Integer categoryId,
                                        Double minPrice,
                                        Double maxPrice,
                                        String sort,
                                        int page,
                                        int size) {

        Pageable pageable = PageRequest.of(page, size);

        return productRepository.search(
                keyword,
                brandId,
                categoryId,
                minPrice,
                maxPrice,
                sort,
                pageable
        ).map(this::mapProduct);
    }

    // customer search
    @Override
    public Page<ProductResponse> search(String keyword,
                                        Integer brandId,
                                        Integer categoryId,
                                        Double minPrice,
                                        Double maxPrice,
                                        Float minRating,
                                        String sort,
                                        int page,
                                        int size) {

        Pageable pageable = PageRequest.of(page, size);

        return productRepository.search(
                keyword,
                brandId,
                categoryId,
                minPrice,
                maxPrice,
                minRating,
                sort,
                pageable
        ).map(this::mapProduct);
    }

    @Override
    public ProductResponse getBySlug(String slug) {

        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() ->
                        new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // tăng view
        product.setViewCount(product.getViewCount() + 1);

        productRepository.save(product);

        return mapProduct(product);
    }

    // =====================================================
    // MAP PRODUCT
    // =====================================================

    private ProductResponse mapProduct(Product product) {

        List<ImageResponse> images =
                imageRepository.findByProductId(product.getId())
                        .stream()
                        .map(img -> ImageResponse.builder()
                                .id(img.getId())
                                .imgUrl(img.getImgUrl())
                                .sortOrder(img.getSortOrder())
                                .build())
                        .toList();

        BigDecimal finalPrice =
                calculateFinalPrice(
                        product.getPrice(),
                        product.getDiscount()
                );

        List<ProductVariantResponse> variants =
                productRepository
                        .findByParentSlug(product.getParentSlug())
                        .stream()
                        .map(p -> ProductVariantResponse.builder()
                                .id(p.getId())
                                .color(p.getColor())
                                .ram(p.getRam())
                                .slug(p.getSlug())
                                .price(p.getPrice())
                                .finalPrice(
                                        calculateFinalPrice(
                                                p.getPrice(),
                                                p.getDiscount()
                                        )
                                )
                                .stock(p.getStock())
                                .thumbnail(p.getThumbnail())
                                .build())
                        .toList();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())

                .thumbnail(product.getThumbnail())

                .price(product.getPrice())
                .finalPrice(finalPrice)

                .stock(product.getStock())
                .sku(product.getSku())
                .color(product.getColor())
                .ram(product.getRam())
                .parentSlug(product.getParentSlug())
                .variants(variants)

                .discountId(
                        product.getDiscount() != null
                                ? product.getDiscount().getId()
                                : null
                )

                .discountValue(
                        product.getDiscount() != null
                                ? product.getDiscount().getDiscountValue()
                                : null
                )

                .discountType(
                        product.getDiscount() != null
                                ? product.getDiscount().getDiscountType().name()
                                : null
                )

                .brandId(product.getBrand().getId())
                .brandName(product.getBrand().getName())

                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())

                .soldQuantity(product.getSoldQuantity())
                .viewCount(product.getViewCount())
                .rating(product.getRating())

                .status(product.getStatus())

                .images(images)

                .build();
    }

    // =====================================================
    // FINAL PRICE
    // =====================================================

    private BigDecimal calculateFinalPrice(
            BigDecimal price,
            Discount discount
    ) {

        if (price == null) {
            return BigDecimal.ZERO;
        }

        if (discount == null) {
            return price;
        }

        // inactive
        if (discount.getStatus() != CommonStatus.ACTIVE) {
            return price;
        }

        LocalDateTime now = LocalDateTime.now();

        // not started
        if (
                discount.getStartDate() != null
                        &&
                        now.isBefore(discount.getStartDate())
        ) {
            return price;
        }

        // expired
        if (
                discount.getEndDate() != null
                        &&
                        now.isAfter(discount.getEndDate())
        ) {
            return price;
        }

        // =====================================================
        // PERCENT
        // =====================================================

        if (discount.getDiscountType() == DiscountType.PERCENT) {

            BigDecimal discountValue =
                    BigDecimal.valueOf(
                            discount.getDiscountValue()
                    );

            BigDecimal discountAmount =
                    price.multiply(discountValue)
                            .divide(
                                    BigDecimal.valueOf(100)
                            );

            // max discount
            if (discount.getMaxDiscount() != null) {

                BigDecimal maxDiscount =
                        BigDecimal.valueOf(
                                discount.getMaxDiscount()
                        );

                if (
                        discountAmount.compareTo(maxDiscount)
                                > 0
                ) {
                    discountAmount = maxDiscount;
                }
            }

            BigDecimal finalPrice =
                    price.subtract(discountAmount);

            return finalPrice.compareTo(BigDecimal.ZERO) < 0
                    ? BigDecimal.ZERO
                    : finalPrice;
        }

        // =====================================================
        // AMOUNT
        // =====================================================

        if (discount.getDiscountType() == DiscountType.AMOUNT) {

            BigDecimal discountAmount =
                    BigDecimal.valueOf(
                            discount.getDiscountValue()
                    );

            BigDecimal finalPrice =
                    price.subtract(discountAmount);

            return finalPrice.compareTo(BigDecimal.ZERO) < 0
                    ? BigDecimal.ZERO
                    : finalPrice;
        }

        return price;
    }

    // =====================================================
    // GENERATE SLUG
    // =====================================================

    private String generateSlug(String name) {

        String baseSlug = name.toLowerCase()
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9-]", "");

        String slug = baseSlug;

        int i = 1;

        while (productRepository.existsBySlug(slug)) {

            slug = baseSlug + "-" + i++;
        }

        return slug;
    }

    private String generateParentSlug(String name) {

        return name.toLowerCase()
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9-]", "");
    }

    private String generateVariantSlug(CreateProductRequest request) {

        String raw = request.getName();

        if (request.getColor() != null) {
            raw += "-" + request.getColor();
        }

        if (request.getRam() != null) {
            raw += "-" + request.getRam();
        }

        return generateSlug(raw);
    }

    private void validateImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return;
        }

        // size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new AppException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        // type
        String contentType = file.getContentType();

        if (contentType == null ||
                !contentType.startsWith("image/")) {

            throw new AppException(ErrorCode.FILE_TYPE_INVALID);
        }
    }
}