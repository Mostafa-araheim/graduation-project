package com.example.pharma.service.pharmacy;

import com.example.pharma.dto.Location.CoordinateDto;
import com.example.pharma.dto.common.PageResponse;
import com.example.pharma.dto.pharmacy.owner.CreatePharmacyRequest;
import com.example.pharma.dto.pharmacy.PharmacyDto;
import com.example.pharma.dto.pharmacy.PharmacyInfo;
import com.example.pharma.dto.pharmacy.PharmacySearchFilter;
import com.example.pharma.dto.pharmacyProduct.PharmacyProductDto;
import com.example.pharma.dto.review.CreateRatingDto;
import com.example.pharma.dto.review.CreateReviewDto;
import com.example.pharma.dto.review.ReviewDto;
import com.example.pharma.exception.resource.EntityNotFoundException;
import com.example.pharma.mapper.pharmacy.PharmacyMapper;
import com.example.pharma.mapper.PharmacyProductMapper;
import com.example.pharma.model.entity.catalog.Category;
import com.example.pharma.model.entity.core.CustomerProfile;
import com.example.pharma.model.entity.pharmacy.Pharmacy;
import com.example.pharma.model.entity.pharmacy.PharmacyAddress;
import com.example.pharma.model.entity.review.PharmacyRating;
import com.example.pharma.model.entity.review.PharmacyReview;
import com.example.pharma.repository.Catalog.CategoryRepository;
import com.example.pharma.repository.Inventory.PharmacyProductRepository;
import com.example.pharma.repository.Pharmacy.PharmacyAddressRepository;
import com.example.pharma.repository.Pharmacy.PharmacyRepository;
import com.example.pharma.repository.Pharmacy.PharmacySpecifications;
import com.example.pharma.repository.Review.PharmacyRatingRepository;
import com.example.pharma.repository.Review.PharmacyReviewRepository;
import com.example.pharma.service.interfaces.IPharmacyService;
import com.example.pharma.service.LocationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PharmacyService implements IPharmacyService {
    private final PharmacyRepository pharmacyRepository;
    private final CategoryRepository categoryRepository;
    private final PharmacyReviewRepository pharmacyReviewRepository;
    private final PharmacyAddressRepository pharmacyAddressRepository;
    private final PharmacyProductRepository pharmacyProductRepository;
    private final PharmacyRatingRepository pharmacyRatingRepository;
    private final PharmacyMapper pharmacyMapper;
    private final PharmacyProductMapper pharmacyProductMapper;
    private final LocationService locationService;
    public PageResponse<PharmacyDto> getPharmacies( PharmacySearchFilter pharmacySearchFilter,
                                                    Pageable pageable)
    {
        Specification<Pharmacy> spec = Specification.where(PharmacySpecifications.hasName(pharmacySearchFilter.name()))
                .and(PharmacySpecifications.hasMinRating(pharmacySearchFilter.minRating()))
                .and(PharmacySpecifications.isOpenNow(pharmacySearchFilter.isOpen()))
                .and(PharmacySpecifications.orderByDistance(pharmacySearchFilter.latitude(),pharmacySearchFilter.longitude()));
//        .and(PharmacySpecifications.withinDistance(
//            latitude,
//            longitude,
//            maxDistanceKm,
//            locationService
//    ))
        Page<Pharmacy> pharmacies = pharmacyRepository.findAll(spec, pageable);

        List<Double> distances = null;
        if (pharmacySearchFilter.latitude() != null && pharmacySearchFilter.longitude() != null && !pharmacies.isEmpty()) {
            List<CoordinateDto> coordinates = pharmacies.getContent().stream()
                    .map(p -> new CoordinateDto(p.getLongitude(), p.getLatitude()))
                    .toList();
            distances = locationService.getRoadDistances(pharmacySearchFilter.latitude(), pharmacySearchFilter.longitude(), coordinates);
        }

        List<PharmacyDto> dtoList = mapToDtos(pharmacies.getContent(), distances);
        return PageResponse.from(new PageImpl<>(dtoList, pageable, pharmacies.getTotalElements()));
    }

    public PharmacyInfo getPharmacyInfo(Long pharmacyId)
    {
        List<Category> categories = categoryRepository.findAll();
        PharmacyAddress pharmacyAddress = pharmacyAddressRepository.findById(pharmacyId).orElseThrow();
        Pharmacy pharmacy = pharmacyAddress.getPharmacy();
        PharmacyDto pharmacyDto = pharmacyMapper.toDto(pharmacy);
        List<ReviewDto> pharmacyReviewDtos = pharmacyReviewRepository.findReviewDtosByPharmacyId(pharmacyId);
        return new PharmacyInfo(categories, pharmacyAddress, pharmacyDto, pharmacyReviewDtos);
    }
    public PageResponse<PharmacyProductDto> getPharmacyProductsUnderACategory(Long pharmacyId, Long categoryId, Pageable pageable)
    {
       Page<PharmacyProductDto> pharmacyProducts = pharmacyProductRepository.findProductsByPharmacyAndCategory(pharmacyId, categoryId, pageable);
       return PageResponse.from(pharmacyProducts);
    }

    @Transactional
    public void createPharmacies(List<CreatePharmacyRequest> requests)
    {
        List<Pharmacy> pharmacies = requests.stream().map(request -> {

            Pharmacy pharmacy = pharmacyMapper.toPharmacy(request);
            PharmacyAddress address = pharmacyMapper.toPharmacyAddress(request);
            address.setPharmacy(pharmacy);
            pharmacy.setAddress(address);
            return pharmacy;

        }).toList();

        pharmacyRepository.saveAll(pharmacies);
    }
    @Transactional
    public PharmacyRating createRating(CreateRatingDto createRatingDto, CustomerProfile customerProfile)
    {
        Pharmacy pharmacy = pharmacyRepository.findById(createRatingDto.pharmacyId())
                .orElseThrow(() -> new EntityNotFoundException("Pharmacy not found"));
        PharmacyRating pharmacyRating = PharmacyRating.builder()
                .pharmacy(pharmacy)
                .customer(customerProfile)
                .rating(createRatingDto.rating())
                .build();
        PharmacyRating saved = pharmacyRatingRepository.save(pharmacyRating);
        updatePharmacyRating(pharmacy, createRatingDto.rating());
        return saved;
    }
    @Transactional
    public ReviewDto createPharmacyReview(CreateReviewDto createReviewDto, CustomerProfile customerProfile)
    {
        Pharmacy pharmacy = pharmacyRepository.findById(createReviewDto.pharmacyId())
                .orElseThrow(() -> new EntityNotFoundException("Pharmacy not found"));
        PharmacyReview pharmacyReview = PharmacyReview.builder()
                .pharmacy(pharmacy)
                .customer(customerProfile)
                .comment(createReviewDto.comment())
                .build();
        pharmacyReviewRepository.save(pharmacyReview);

        long currentReviewCount = pharmacy.getReviewCount() == null ? 0 : pharmacy.getReviewCount();
        pharmacy.setReviewCount(currentReviewCount + 1);
        return new ReviewDto(customerProfile.getUser().getName(), pharmacyReview.getComment());

    }
    public List<PharmacyDto> getAllPharmacies()
    {
        return pharmacyMapper.toDtoList(pharmacyRepository.findAll());
    }

    private void updatePharmacyRating(Pharmacy pharmacy, int newRating) {

        long count = pharmacy.getRatingCount() == null ? 0 : pharmacy.getRatingCount();
        BigDecimal avg = pharmacy.getAverageRating() == null ? BigDecimal.ZERO : pharmacy.getAverageRating();

        BigDecimal newAverage = avg.multiply(BigDecimal.valueOf(count))
                .add(BigDecimal.valueOf(newRating))
                .divide(BigDecimal.valueOf(count + 1), 2, RoundingMode.HALF_UP);

        pharmacy.setAverageRating(newAverage);
        pharmacy.setRatingCount(count + 1);
    }
    private List<PharmacyDto> mapToDtos(List<Pharmacy> pharmacies, List<Double> distances) {
        List<PharmacyDto> result = new ArrayList<>();
        for (int i = 0; i < pharmacies.size(); i++) {
            PharmacyDto dto = pharmacyMapper.toDto(pharmacies.get(i));
            Float distance = (distances != null) ? distances.get(i).floatValue() : null;
            result.add(new PharmacyDto(
                    dto.pharmacyId(),
                    dto.name(),
                    dto.imageUrl(),
                    dto.averageRating(),
                    distance,
                    dto.openingTime(),
                    dto.closingTime(),
                    dto.latitude(),
                    dto.longitude(),
                    dto.reviewCount(),
                    dto.isClosed(),
                    dto.address()
            ));
        }
        return result;
    }

}
