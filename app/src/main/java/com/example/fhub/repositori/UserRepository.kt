package com.example.fhub.repositori

import com.example.fhub.data.dao.UserDao
import com.example.fhub.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun insertUser(user: UserEntity)
    suspend fun updateUser(user: UserEntity)
    suspend fun deleteUser(user: UserEntity)
    suspend fun getUserByEmail(email: String): UserEntity?
    fun getUserByIdStream(id: Int): Flow<UserEntity?>
}

class OfflineUserRepository(
    private val userDao: UserDao
): UserRepository {
    override suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    override suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)
    override suspend fun deleteUser(user: UserEntity) = userDao.deleteUser(user)
    override suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email)
    override fun getUserByIdStream(id: Int): Flow<UserEntity?> = userDao.getUserByIdStream(id)
}