package com.project.identity_service.repository;

import com.project.identity_service.model.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, Integer> {

    Optional<UserCredential> findByName(String username);
}
