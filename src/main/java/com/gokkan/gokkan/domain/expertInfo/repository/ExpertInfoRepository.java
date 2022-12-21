package com.gokkan.gokkan.domain.expertInfo.repository;

import com.gokkan.gokkan.domain.expertInfo.domain.ExpertInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpertInfoRepository extends JpaRepository<ExpertInfo, Long> {

	boolean existsByMemberId(Long memberId);

	Optional<ExpertInfo> findByMemberId(Long memberId);
}