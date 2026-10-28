package com.example.sw_layer_application.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 =
    object : Migration(1, 2) {

        override fun migrate(
            db: SupportSQLiteDatabase
        ) {

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `layers` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `projectId` INTEGER NOT NULL,
                    `name` TEXT NOT NULL,
                    `geometryType` TEXT NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    FOREIGN KEY(`projectId`)
                    REFERENCES `projects`(`id`)
                    ON UPDATE NO ACTION
                    ON DELETE CASCADE
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS
                `index_layers_projectId`
                ON `layers` (`projectId`)
                """.trimIndent()
            )
        }
    }

val MIGRATION_2_3 =
    object : Migration(2, 3) {

        override fun migrate(
            db: SupportSQLiteDatabase
        ) {

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `layer_fields` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `layerId` INTEGER NOT NULL,
                    `name` TEXT NOT NULL,
                    `fieldType` TEXT NOT NULL,
                    `isRequired` INTEGER NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    FOREIGN KEY(`layerId`)
                    REFERENCES `layers`(`id`)
                    ON UPDATE NO ACTION
                    ON DELETE CASCADE
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS
                `index_layer_fields_layerId`
                ON `layer_fields` (`layerId`)
                """.trimIndent()
            )
        }
    }

val MIGRATION_3_4 =
    object : Migration(3, 4) {

        override fun migrate(
            db: SupportSQLiteDatabase
        ) {

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `features` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `layerId` INTEGER NOT NULL,
                    `geometryType` TEXT NOT NULL,
                    `latitude` REAL,
                    `longitude` REAL,
                    `geometryJson` TEXT,
                    `createdAt` INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }


val MIGRATION_4_5 =
    object : Migration(4, 5) {

        override fun migrate(
            db: SupportSQLiteDatabase
        ) {

            db.execSQL(
                """
                ALTER TABLE features
                ADD COLUMN attributesJson TEXT
                """.trimIndent()
            )
        }
    }