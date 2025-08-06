package com.sb.moneymanager.service;

import com.sb.moneymanager.dto.ProfileDTO;
import com.sb.moneymanager.entity.ProfileEntity;
import com.sb.moneymanager.repository.ProfileRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    private final EmailService emailService;

    public ProfileDTO registerProfile(ProfileDTO profileDTO)
    {
        ProfileEntity newProfile=toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile=profileRepository.save(newProfile);

        //set activation email
        String activationLink="http://localhost:8080/api/v1/activate?activationToken="+newProfile.getActivationToken();
        String subject="Activate your Money Manager account";
        String body="Click on the given link to activate your account : "+activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject,body);
        return toDTO(newProfile);
    }

    public boolean activateProfile(String activationToken)
    {
        return profileRepository.findByActivationToken(activationToken)
                .map(profile->{
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                }).orElse(false);
    }


    //helper functions
    public ProfileEntity toEntity(ProfileDTO profileDTO)
    {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(profileDTO.getPassword())
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity)
    {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .password(profileEntity.getPassword())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }
}
