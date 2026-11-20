package com.universal.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.universal.entities.AppUser;
import com.universal.entities.Role;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

	Optional<AppUser> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByEmailAndIdNot(String email, Long id);

	boolean existsByRole(Role role);

	long countByRole(Role role);

	List<AppUser> findByRoleOrderByFullNameAsc(Role role);

	List<AppUser> findTop5ByRoleOrderByCreatedAtDesc(Role role);

	List<AppUser> findByRoleIn(Collection<Role> roles);
}
