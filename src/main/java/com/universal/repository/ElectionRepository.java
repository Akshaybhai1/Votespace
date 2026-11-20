package com.universal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.universal.entities.Election;

public interface ElectionRepository extends JpaRepository<Election, Long> {

}
