/*
 * Copyright 2022-2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.knowledge.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.android.fhir.knowledge.db.dao.KnowledgeDao
import com.google.android.fhir.knowledge.db.entities.ImplementationGuideEntity
import com.google.android.fhir.knowledge.db.entities.ImplementationGuideResourceMetadataEntity
import com.google.android.fhir.knowledge.db.entities.ResourceMetadataEntity

/**
 * Stores knowledge artifacts metadata for implementation guides and their containing FHIR
 * Resources.
 *
 * Same FhirResource (identified as the resource with the same `url`) can be part of the different
 * IGs. To avoid duplications, [ImplementationGuideEntity] are connected with
 * [ResourceMetadataEntity] through [ImplementationGuideResourceMetadataEntity] in a
 * [many-to-many](https://developer.android.com/training/data-storage/room/relationships#many-to-many)
 * relationship.
 */
@Database(
  entities =
    [
      ImplementationGuideEntity::class,
      ResourceMetadataEntity::class,
      ImplementationGuideResourceMetadataEntity::class,
    ],
  version = 2,
  exportSchema = true,
)
@TypeConverters(DbTypeConverters::class)
internal abstract class KnowledgeDatabase : RoomDatabase() {
  abstract fun knowledgeDao(): KnowledgeDao

  companion object {
    private const val DB_NAME = "knowledge.db"

    // Drop the url + version + resourceFile index and create a new index on url + version
    internal val MIGRATION_1_2 =
      object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
          database.execSQL(
            "DROP INDEX IF EXISTS `index_KnowledgeMetadataEntity_url_version_resourceFile`",
          )

          // Remove duplicated records with the same url and version. This will lose data if
          // multiple files of the same url and version have been added to the database. But in such
          // cases the library's behavior is already undefined.
          database
            .query(
              """
              WITH data AS(
                SELECT *, ROW_NUMBER() OVER(PARTITION BY url,version) AS row_number
                FROM KnowledgeMetadataEntity
              )
              SELECT resourceMetadataId from data
              WHERE row_number > 1
              """,
            )
            .let {
              var continueIterating = it.moveToFirst()
              while (continueIterating) {
                val resourceMetadataId = it.getLong(0)
                database.execSQL(
                  "DELETE FROM KnowledgeMetadataEntity WHERE resourceMetadataId = $resourceMetadataId;",
                )
                continueIterating = it.moveToNext()
              }
            }
          database.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_KnowledgeMetadataEntity_url_version` ON `KnowledgeMetadataEntity` (`url`, `version`)",
          )
        }
      }

    fun create(
      inMemory: Boolean,
      context: Context,
    ) =
      if (inMemory) {
        Room.inMemoryDatabaseBuilder(context, KnowledgeDatabase::class.java).build()
      } else {
        Room.databaseBuilder(context, KnowledgeDatabase::class.java, DB_NAME)
          .addMigrations(MIGRATION_1_2)
          .build()
      }
  }
}
