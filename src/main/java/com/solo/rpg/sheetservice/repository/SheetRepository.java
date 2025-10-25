package com.solo.rpg.sheetservice.repository;

import com.solo.rpg.sheetservice.model.SheetForm;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SheetRepository extends MongoRepository<SheetForm, String> {
    Optional<SheetForm> findByOwnerId(String userId);


}
