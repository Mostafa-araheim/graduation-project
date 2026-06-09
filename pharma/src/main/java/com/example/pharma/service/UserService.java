package com.example.pharma.service;

import com.example.pharma.dto.user.AddressDto;
import com.example.pharma.dto.user.UpdateProfileDto;
import com.example.pharma.dto.user.UserProfileDto;
import com.example.pharma.exception.access.AccessDeniedException;
import com.example.pharma.exception.resource.EntityNotFoundException;
import com.example.pharma.model.entity.core.User;
import com.example.pharma.model.entity.core.UserAddress;
import com.example.pharma.repository.Core.UserAddressRepository;
import com.example.pharma.repository.Core.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;

    private final String uploadDir = System.getProperty("user.dir") + "/images/";

    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return mapToUserProfileDto(user);
    }

    @Transactional
    public UserProfileDto updateUserProfile(Long userId, UpdateProfileDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (dto.name() != null && !dto.name().isBlank()) {
            user.setName(dto.name());
        }
        if (dto.phone() != null) {
            user.setPhone(dto.phone());
        }

        user = userRepository.save(user);
        return mapToUserProfileDto(user);
    }

    @Transactional
    public UserProfileDto uploadProfilePicture(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String newFilename = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(newFilename);
            
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            user.setImageUrl("/images/" + newFilename);
            user = userRepository.save(user);

            return mapToUserProfileDto(user);

        } catch (IOException e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage(), e);
        }
    }

    @Transactional
    public AddressDto addAddress(Long userId, AddressDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        UserAddress address = new UserAddress();
        address.setUser(user);
        address.setStreet(dto.street());
        address.setCity(dto.city());
        address.setPostalCode(dto.postalCode());
        address.setCountry(dto.country());
        address.setApartmentNumber(dto.apartmentNumber());

        address = userAddressRepository.save(address);
        return mapToAddressDto(address);
    }

    @Transactional(readOnly = true)
    public List<AddressDto> getUserAddresses(Long userId) {
        List<UserAddress> addresses = userAddressRepository.findByUser_UserId(userId);
        return addresses.stream().map(this::mapToAddressDto).collect(Collectors.toList());
    }

    @Transactional
    public AddressDto updateAddress(Long userId, Long addressId, AddressDto dto) {
        UserAddress address = userAddressRepository.findByUserAddressIdAndUser_UserId(addressId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found or does not belong to you"));

        if (dto.street() != null) address.setStreet(dto.street());
        if (dto.city() != null) address.setCity(dto.city());
        if (dto.postalCode() != null) address.setPostalCode(dto.postalCode());
        if (dto.country() != null) address.setCountry(dto.country());
        if (dto.apartmentNumber() != null) address.setApartmentNumber(dto.apartmentNumber());

        address = userAddressRepository.save(address);
        return mapToAddressDto(address);
    }

    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        UserAddress address = userAddressRepository.findByUserAddressIdAndUser_UserId(addressId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found or does not belong to you"));
        
        userAddressRepository.delete(address);
    }

    private UserProfileDto mapToUserProfileDto(User user) {
        return new UserProfileDto(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getImageUrl(),
                user.getRoles()
        );
    }

    private AddressDto mapToAddressDto(UserAddress address) {
        return new AddressDto(
                address.getUserAddressId(),
                address.getStreet(),
                address.getCity(),
                address.getPostalCode(),
                address.getCountry(),
                address.getApartmentNumber()
        );
    }
}
