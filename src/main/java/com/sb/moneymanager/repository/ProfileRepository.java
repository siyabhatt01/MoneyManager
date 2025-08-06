package com.sb.moneymanager.repository;

import com.sb.moneymanager.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface ProfileRepository extends JpaRepository <ProfileEntity,Long>{

    Optional<ProfileEntity> findByEmail(String email);
    Optional<ProfileEntity> findByActivationToken(String activationToken);
}
