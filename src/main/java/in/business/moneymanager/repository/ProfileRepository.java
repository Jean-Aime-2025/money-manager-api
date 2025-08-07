package in.business.moneymanager.repository;

import in.business.moneymanager.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ProfileRepository extends JpaRepository<ProfileEntity,Long> {

    Optional<ProfileEntity> findByEmail(String email);
    Optional<ProfileEntity> findByActivationToken(String activationToken);
}
