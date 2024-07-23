package com.ssafy.meshroom.backend.domain.OVToken.dao;

import com.ssafy.meshroom.backend.domain.OVToken.domain.OVToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OVTokenRepository extends MongoRepository<OVToken, String> {
}
