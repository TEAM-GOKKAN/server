package com.gokkan.gokkan.domain.expertComment.repository;

import com.gokkan.gokkan.domain.expertComment.domain.ExpertComment;
import com.gokkan.gokkan.domain.expertInfo.domain.ExpertInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpertCommentRepository extends JpaRepository<ExpertComment, Long> {


	List<ExpertComment> findAllByExpertInfo(ExpertInfo expertInfo);
}