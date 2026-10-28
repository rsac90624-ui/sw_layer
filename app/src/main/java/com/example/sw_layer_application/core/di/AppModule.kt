package com.example.sw_layer_application.core.di

import com.example.sw_layer_application.core.database.MIGRATION_2_3
import android.content.Context
import androidx.room.Room
import com.example.sw_layer_application.core.database.AppDatabase
import com.example.sw_layer_application.core.database.MIGRATION_1_2
import com.example.sw_layer_application.core.database.MIGRATION_3_4
import com.example.sw_layer_application.core.database.MIGRATION_4_5
import com.example.sw_layer_application.data.local.FeatureDao
import com.example.sw_layer_application.data.local.ProjectDao
import com.example.sw_layer_application.data.repository.ProjectRepositoryImpl
import com.example.sw_layer_application.domain.repository.ProjectRepository
import com.example.sw_layer_application.domain.usecase.CreateProjectUseCase
import com.example.sw_layer_application.domain.usecase.GetProjectsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.sw_layer_application.data.local.LayerDao
import com.example.sw_layer_application.data.repository.LayerFieldRepositoryImpl
import com.example.sw_layer_application.data.repository.LayerRepositoryImpl
import com.example.sw_layer_application.domain.repository.LayerFieldRepository
import com.example.sw_layer_application.domain.repository.LayerRepository
import com.example.sw_layer_application.domain.usecase.CreateLayerUseCase
import com.example.sw_layer_application.domain.usecase.GetFieldsForLayerUseCase
import com.example.sw_layer_application.domain.usecase.GetLayersForProjectUseCase
import com.example.sw_layer_application.data.local.LayerFieldDao
import com.example.sw_layer_application.data.repository.FeatureRepositoryImpl
import com.example.sw_layer_application.domain.repository.FeatureRepository

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sw_layer_database"
        )
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5
            )           .build()
    }
    @Provides
    fun provideLayerFieldDao(
        database: AppDatabase
    ): LayerFieldDao {

        return database.layerFieldDao()
    }


    @Provides
    fun provideProjectDao(
        database: AppDatabase
    ): ProjectDao {
        return database.projectDao()
    }

    @Provides
    @Singleton
    fun provideProjectRepository(
        projectDao: ProjectDao
    ): ProjectRepository {

        return ProjectRepositoryImpl(
            projectDao = projectDao
        )
    }

    @Provides
    fun provideCreateProjectUseCase(
        repository: ProjectRepository
    ): CreateProjectUseCase {

        return CreateProjectUseCase(repository)
    }

    @Provides
    fun provideGetProjectsUseCase(
        repository: ProjectRepository
    ): GetProjectsUseCase {

        return GetProjectsUseCase(repository)
    }
    @Provides
    fun provideLayerDao(
        database: AppDatabase
    ): LayerDao {
        return database.layerDao()
    }
    @Provides
    @Singleton
    fun provideLayerRepository(
        layerDao: LayerDao
    ): LayerRepository {

        return LayerRepositoryImpl(
            layerDao = layerDao
        )
    }

    @Provides
    fun provideCreateLayerUseCase(
        repository: LayerRepository
    ): CreateLayerUseCase {

        return CreateLayerUseCase(
            repository = repository
        )
    }

    @Provides
    fun provideGetLayersForProjectUseCase(
        repository: LayerRepository
    ): GetLayersForProjectUseCase {

        return GetLayersForProjectUseCase(
            repository = repository
        )
    }

    @Provides
    @Singleton
    fun provideFeatureDao(
        database: AppDatabase
    ): FeatureDao {

        return database.featureDao()
    }

    @Provides
    @Singleton
    fun provideFeatureRepository(
        featureDao: FeatureDao
    ): FeatureRepository {

        return FeatureRepositoryImpl(
            featureDao = featureDao
        )
    }




    @Provides
    @Singleton
    fun provideLayerFieldRepository(
        layerFieldDao: LayerFieldDao
    ): LayerFieldRepository {

        return LayerFieldRepositoryImpl(
            layerFieldDao = layerFieldDao
        )
    }
}