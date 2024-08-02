package com.ssafy.meshroom.backend.domain.contents.dao;

import com.ssafy.meshroom.backend.domain.contents.domain.ContentsOrder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;

public interface ContentsOrderRepository extends MongoRepository<ContentsOrder, String> {
    List<ContentsOrder> findAllBySessionId(String sessionSid);

    @Query("{ '_id' : ?0 }")
    @Update(" {'$set': { 'isDone' : ?1 }} ")
    void updateById(String _id, Boolean isDone);
}
