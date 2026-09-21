package com.fiap.ariachallenge.di

import com.fiap.ariachallenge.data.repository.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.fiap.ariachallenge.data.repository.FakeAiRepository
import com.fiap.ariachallenge.data.repository.IdeaRepositoryImpl
import com.fiap.ariachallenge.data.repository.OrientationRepositoryImpl
import com.fiap.ariachallenge.data.repository.ProjectRepositoryImpl
import com.fiap.ariachallenge.data.repository.FakeUserRepository
import com.fiap.ariachallenge.domain.repository.IAiRepository
import com.fiap.ariachallenge.domain.repository.IAuthRepository
import com.fiap.ariachallenge.domain.repository.IIdeaRepository
import com.fiap.ariachallenge.domain.repository.IOrientationRepository
import com.fiap.ariachallenge.domain.repository.IProjectRepository
import com.fiap.ariachallenge.domain.repository.IUserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAiRepository(impl: FakeAiRepository): IAiRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): IAuthRepository

    @Binds
    @Singleton
    abstract fun bindIdeaRepository(impl: IdeaRepositoryImpl): IIdeaRepository

    @Binds
    @Singleton
    abstract fun bindProjectRepository(impl: ProjectRepositoryImpl): IProjectRepository

    @Binds
    @Singleton
    abstract fun bindOrientationRepository(impl: OrientationRepositoryImpl): IOrientationRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: FakeUserRepository): IUserRepository
}
