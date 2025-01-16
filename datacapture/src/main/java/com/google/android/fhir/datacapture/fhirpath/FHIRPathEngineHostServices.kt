/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.datacapture.fhirpath

import org.hl7.fhir.r4.fhirpath.FHIRPathEngine
import org.hl7.fhir.r4.fhirpath.FHIRPathUtilityClasses
import org.hl7.fhir.r4.fhirpath.TypeDetails
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.ValueSet

/**
 * Resolves constants defined in the fhir path expressions beyond those defined in the specification
 */
internal object FHIRPathEngineHostServices : FHIRPathEngine.IEvaluationContext {
  override fun resolveConstant(
    engine: FHIRPathEngine?,
    appContext: Any?,
    name: String?,
    beforeContext: Boolean,
    explicitConstant: Boolean,
  ) = ((appContext as? Map<*, *>)?.get(name) as? Base)?.let { listOf(it) } ?: emptyList()

  override fun resolveConstantType(
    engine: FHIRPathEngine?,
    appContext: Any?,
    name: String?,
    explicitConstant: Boolean,
  ): TypeDetails {
    TODO("Not yet implemented")
  }

  override fun log(argument: String?, focus: MutableList<Base>?): Boolean {
    TODO("Not yet implemented")
  }

  override fun resolveFunction(
    engine: FHIRPathEngine?,
    functionName: String?,
  ): FHIRPathUtilityClasses.FunctionDetails {
    TODO("Not yet implemented")
  }

  override fun checkFunction(
    engine: FHIRPathEngine?,
    appContext: Any?,
    functionName: String?,
    focus: TypeDetails?,
    parameters: MutableList<TypeDetails>?,
  ): TypeDetails {
    TODO("Not yet implemented")
  }

  override fun executeFunction(
    engine: FHIRPathEngine?,
    appContext: Any?,
    focus: MutableList<Base>?,
    functionName: String?,
    parameters: MutableList<MutableList<Base>>?,
  ): MutableList<Base> {
    TODO("Not yet implemented")
  }

  override fun resolveReference(
    engine: FHIRPathEngine?,
    appContext: Any?,
    url: String?,
    refContext: Base?,
  ): Base {
    TODO("Not yet implemented")
  }

  override fun conformsToProfile(
    engine: FHIRPathEngine?,
    appContext: Any?,
    item: Base?,
    url: String?,
  ): Boolean {
    TODO("Not yet implemented")
  }

  override fun resolveValueSet(engine: FHIRPathEngine?, appContext: Any?, url: String?): ValueSet {
    TODO("Not yet implemented")
  }
}
