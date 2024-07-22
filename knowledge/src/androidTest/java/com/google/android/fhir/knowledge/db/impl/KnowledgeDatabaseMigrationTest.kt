/*
 * Copyright 2023-2024 Google LLC
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

package com.google.android.fhir.knowledge.db.impl

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.knowledge.db.KnowledgeDatabase
import com.google.android.fhir.knowledge.db.KnowledgeDatabase.Companion.MIGRATION_1_2
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KnowledgeDatabaseMigrationTest {

  @get:Rule
  val helper: MigrationTestHelper =
    MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), KnowledgeDatabase::class.java)

  @Test
  fun migrate1To2_should_execute_with_no_exception(): Unit = runBlocking {
    helper.createDatabase(DB_NAME, 1).apply {
      execSQL(
        "INSERT INTO ResourceMetadataEntity (resourceType, url, version, resourceFile) VALUES ('Library', 'abc.com', '1', '1.json');",
      )
      execSQL(
        "INSERT INTO ResourceMetadataEntity (resourceType, url, version, resourceFile) VALUES ('Library', 'abc.com', '1', '2.json');",
      )
      close()
    }

    val migratedDatabase = helper.runMigrationsAndValidate(DB_NAME, 8, true, MIGRATION_1_2)

    var count: Long

    migratedDatabase.let { database ->
      database
        .query("SELECT COUNT(*) FROM LocalChangeEntity WHERE url = 'abc.com' AND version = '1'")
        .let {
          it.moveToFirst()
          count = it.getLong(0)
        }
    }
    migratedDatabase.close()
    assertThat(count).isEqualTo(1)
  }

  companion object {
    const val DB_NAME = "migration_tests.db"
  }
}
