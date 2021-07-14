/*
 * Copyright (C) 2020 Bosch.IO GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package org.ossreviewtoolkit.model

/**
 * The result of a single vulnerability check of a single package.
 */
data class AdvisorResult(
    /**
     * The found vulnerabilities.
     */
    val vulnerabilities: List<Vulnerability>,

    /**
     * Details about the used advisor.
     */
    val advisor: AdvisorDetails,

    /**
     * A summary of the advisor results.
     */
    val summary: AdvisorSummary
)
