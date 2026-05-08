package be.service.impl;

import be.dto.request.CreateProductRequest;
import be.dto.request.UpdateProductRequest;
import be.dto.response.ImageResponse;
import be.dto.response.ProductResponse;
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
                .slug(generateSlug(request.getName()))
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock() != null ? request.getStock() : 0)
                .sku(request.getSku())
                .color(request.getColor())
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
            product.setSlug(generateSlug(request.getName()));
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
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

        Double finalPrice =
                calculateFinalPrice(
                        product.getPrice(),
                        product.getDiscount()
                );

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

    private Double calculateFinalPrice(Double price,
                                       Discount discount) {

        if (price == null) {
            return 0.0;
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
        if (discount.getStartDate() != null &&
                now.isBefore(discount.getStartDate())) {

            return price;
        }

        // expired
        if (discount.getEndDate() != null &&
                now.isAfter(discount.getEndDate())) {

            return price;
        }

        // percent
        if (discount.getDiscountType() == DiscountType.PERCENT) {

            double discountAmount =
                    price * discount.getDiscountValue() / 100;

            if (discount.getMaxDiscount() != null) {

                discountAmount =
                        Math.min(
                                discountAmount,
                                discount.getMaxDiscount()
                        );
            }

            return Math.max(price - discountAmount, 0);
        }

        // amount
        if (discount.getDiscountType() == DiscountType.AMOUNT) {

            return Math.max(
                    price - discount.getDiscountValue(),
                    0
            );
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