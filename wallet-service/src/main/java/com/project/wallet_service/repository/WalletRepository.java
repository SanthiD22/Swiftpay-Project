package com.project.wallet_service.repository;

import com.project.wallet_service.model.UserWallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<UserWallet, Integer> {

    //Derived Query
    Optional<UserWallet> findByUserId(Integer id);

    // JPQL Query
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(" SELECT uw FROM UserWallet uw WHERE uw.userId = :userId ")
    Optional<UserWallet> findByUserIdForUpdate(Integer userId);
}
