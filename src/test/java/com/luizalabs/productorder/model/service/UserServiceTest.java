package com.luizalabs.productorder.model.service;

import com.luizalabs.productorder.model.entity.UserEntity;
import com.luizalabs.productorder.model.repository.UserRepository;
import com.luizalabs.productorder.model.repository.impl.MongoBatchUpsertRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MongoBatchUpsertRepository mongoBatchUpsertRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    private List<UserEntity> userEntityList;

    @BeforeEach
    void setUp() {
        userEntityList = Instancio.createList(UserEntity.class);
    }

    @Test
    @DisplayName("Should save all users when saveAll is called")
    void shouldSaveAllUsers() {
        userService.saveAll(userEntityList);

        verify(mongoBatchUpsertRepository, times(1)).upsertMany(userEntityList, UserEntity.class);
    }

    @Test
    @DisplayName("Should find users by ids")
    void shouldFindUsersByIds() {
        var idList = userEntityList.stream().map(UserEntity::getId).toList();

        when(userRepository.findAllById(any())).thenReturn(userEntityList);

        var result = userService.findUsersByIdList(idList);

        assertEquals(userEntityList.size(), result.size());
        assertEquals(result, userEntityList);
    }
}