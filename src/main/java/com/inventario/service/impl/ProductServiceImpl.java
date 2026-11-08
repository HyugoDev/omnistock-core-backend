package com.inventario.service.impl;

import com.inventario.dto.ProductRequestDTO;
import com.inventario.dto.ProductResponseDTO;
import com.inventario.entity.Category;
import com.inventario.entity.Product;
import com.inventario.entity.enums.ProductType;
import com.inventario.entity.enums.UnitOfMeasure;
import com.inventario.exception.BadRequestException;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.repository.CategoryRepository;
import com.inventario.repository.ProductRepository;
import com.inventario.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        Product product = findActiveProductById(id);
        return mapToResponseDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductByBarcode(String barcode) {
        Product product = productRepository.findByBarcode(barcode.trim())
                .filter(Product::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with barcode: " + barcode));
        return mapToResponseDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getLowStockProducts() {
        return productRepository.findLowStockProducts()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {
        String cleanBarcode = requestDTO.getBarcode().trim();

        // 1. Regla de negocio: El código de barras no puede repetirse
        if (productRepository.findByBarcode(cleanBarcode).isPresent()) {
            throw new BadRequestException("El código de barras ya está registrado: " + cleanBarcode);
        }

        // 2. Regla de negocio: Validar SKU si se provee
        if (requestDTO.getSku() != null && !requestDTO.getSku().isBlank()) {
            String cleanSku = requestDTO.getSku().trim();
            if (productRepository.findBySku(cleanSku).isPresent()) {
                throw new BadRequestException("El SKU ya está registrado: " + cleanSku);
            }
        }

        // 3. Regla de negocio: El precio de venta debe ser mayor al de compra
        validatePrices(requestDTO);

        // 4. Buscar la categoría asociada
        Category category = findActiveCategoryById(requestDTO.getCategoryId());

        // 5. Construir la entidad universal
        ProductType type = requestDTO.getProductType() != null ? requestDTO.getProductType() : ProductType.STANDARD;
        UnitOfMeasure uom = requestDTO.getUnitOfMeasure() != null ? requestDTO.getUnitOfMeasure() : UnitOfMeasure.UNIT;

        Product product = Product.builder()
                .barcode(cleanBarcode)
                .sku(requestDTO.getSku() != null ? requestDTO.getSku().trim() : null)
                .name(requestDTO.getName().trim())
                .description(requestDTO.getDescription())
                .productType(type)
                .unitOfMeasure(uom)
                .purchasePrice(requestDTO.getPurchasePrice())
                .salePrice(requestDTO.getSalePrice())
                .currentStock(requestDTO.getCurrentStock())
                .minStock(requestDTO.getMinStock())
                .attributesJson(requestDTO.getAttributesJson())
                .active(true)
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponseDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO) {
        Product product = findActiveProductById(id);
        String cleanBarcode = requestDTO.getBarcode().trim();

        // Si cambió el código de barras, verificar que no pertenezca a OTRO producto
        Optional<Product> existingWithBarcode = productRepository.findByBarcode(cleanBarcode);
        if (existingWithBarcode.isPresent() && !existingWithBarcode.get().getId().equals(id)) {
            throw new BadRequestException("El código de barras ya pertenece a otro producto: " + cleanBarcode);
        }

        validatePrices(requestDTO);
        Category category = findActiveCategoryById(requestDTO.getCategoryId());

        // Actualizar datos
        product.setBarcode(cleanBarcode);
        if (requestDTO.getSku() != null) {
            product.setSku(requestDTO.getSku().trim());
        }
        product.setName(requestDTO.getName().trim());
        product.setDescription(requestDTO.getDescription());
        if (requestDTO.getProductType() != null) {
            product.setProductType(requestDTO.getProductType());
        }
        if (requestDTO.getUnitOfMeasure() != null) {
            product.setUnitOfMeasure(requestDTO.getUnitOfMeasure());
        }
        product.setPurchasePrice(requestDTO.getPurchasePrice());
        product.setSalePrice(requestDTO.getSalePrice());
        product.setCurrentStock(requestDTO.getCurrentStock());
        product.setMinStock(requestDTO.getMinStock());
        if (requestDTO.getAttributesJson() != null) {
            product.setAttributesJson(requestDTO.getAttributesJson());
        }
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);
        return mapToResponseDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = findActiveProductById(id);
        product.setActive(false);
        productRepository.save(product);
    }

    // --- MÉTODOS AUXILIARES ---
    private Product findActiveProductById(Long id) {
        return productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private Category findActiveCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .filter(Category::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Active category not found with id: " + categoryId));
    }

    private void validatePrices(ProductRequestDTO requestDTO) {
        if (requestDTO.getPurchasePrice() != null && requestDTO.getSalePrice() != null) {
            if (requestDTO.getSalePrice().compareTo(requestDTO.getPurchasePrice()) <= 0) {
                throw new BadRequestException("El precio de venta debe ser estrictamente mayor al precio de compra");
            }
        }
    }

    private ProductResponseDTO mapToResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .barcode(product.getBarcode())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .productType(product.getProductType())
                .unitOfMeasure(product.getUnitOfMeasure())
                .purchasePrice(product.getPurchasePrice())
                .salePrice(product.getSalePrice())
                .currentStock(product.getCurrentStock())
                .minStock(product.getMinStock())
                .attributesJson(product.getAttributesJson())
                .active(product.getActive())
                .requiresRestock(product.requiresRestock())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
