package com.ssafy.meshroom.backend.domain.contents.dao;

import com.ssafy.meshroom.backend.domain.contents.domain.ContentsOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContentsOrderRepository extends MongoRepository<ContentsOrder, String> {
}
