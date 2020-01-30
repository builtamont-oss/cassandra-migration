/**
 * File     : KeyspaceConfiguration.kt
 * License  :
 *   Original   - Copyright (c) 2015 - 2016 Contrast Security
 *   Derivative - Copyright (c) 2016 - 2018 cassandra-migration Contributors
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.hhandoko.cassandra.migration.api.configuration

import com.datastax.oss.driver.api.core.ConsistencyLevel
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract

/**
 * Keyspace configuration.
 */
class KeyspaceConfiguration {

    /**
     * Cluster configuration.
     */
//    var clusterConfig: ClusterConfiguration = ClusterConfiguration()

    /**
     * Cassandra keyspace name.
     */
    var name: String? = null
      get set

    var prefix: String? = null
      get set

    /**
     * Keyspace consistency level.
     */
    var consistency: ConsistencyLevel? = null
      get set

    /**
     * KeyspaceConfiguration initialization.
     */
    init {
        ConfigFactory.invalidateCaches()
        ConfigFactory.load().let {
            it.extract<String?>(ConfigurationProperty.KEYSPACE_NAME.namespace)?.let {
                this.name = it.trim()
            }

            it.extract<String?>(ConfigurationProperty.CONSISTENCY_LEVEL.namespace)?.let {
                this.consistency = DefaultConsistencyLevel.valueOf(it.trim().toUpperCase())
            }
            it.extract<String?>(ConfigurationProperty.CASSANDRA_PREFIX.namespace)?.let {
                this.prefix = it.trim()
            }
        }
    }

}
