package com.gokkan.gokkan.domain.style.repository;

import com.gokkan.gokkan.domain.style.domain.Style;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StyleRepository extends JpaRepository<Style, Long> {

}