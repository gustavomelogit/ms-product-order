package com.luizalabs.productorder.model.service;

import com.luizalabs.productorder.model.entity.UserEntity;
import com.luizalabs.productorder.model.repository.UserRepository;
import com.luizalabs.productorder.model.repository.impl.MongoBatchUpsertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MongoBatchUpsertRepository mongoBatchUpsertRepository;

    /**
     * Save all users
     *
     * @param userEntityList User entities to save
     */
    public void saveAll(List<UserEntity> userEntityList) {
        log.info("Save User | Saving users...");
        mongoBatchUpsertRepository.upsertMany(userEntityList, UserEntity.class);
    }

    /**
     * Find users by ids
     *
     * @param idList User id list to find
     * @return users found
     */
    public List<UserEntity> findUsersByIdList(List<String> idList) {
        return userRepository.findAllById(idList);
    }

}