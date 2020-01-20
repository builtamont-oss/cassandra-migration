/**
 * File     : ConfigurationProperty.kt
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

/**
 * Cassandra migration configuration properties.
 *
 * @param namespace The property namespace.
 * @param description The property description.
 */
enum class ConfigurationProperty(val namespace: String, val description: String) {

    // Scripts configuration properties
    // ~~~~~~
    SCRIPTS_ENCODING(
            "cassandra.migration.scripts.encoding",
            "Encoding for CQL scripts"
    ),

    SCRIPTS_LOCATIONS(
            "cassandra.migration.scripts.locations",
            "Locations of the migration scripts in CSV format"
    ),

    SCRIPTS_TIMEOUT(
            "cassandra.migration.scripts.timeout",
            "CQL scripts timeout in seconds"
    ),

    ALLOW_OUT_OF_ORDER(
            "cassandra.migration.scripts.allowoutoforder",
            "Allow out of order migration"
    ),

    // Table configuration properties
    // ~~~~~~
    TABLE_PREFIX(
            "cassandra.migration.table.prefix",
            "Prefix to be prepended to cassandra_migration_version* table names"
    ),

    // Baseline version configuration properties
    // ~~~~~~
    BASELINE_VERSION(
            "cassandra.migration.baseline.version",
            "Version to apply for an existing schema when baseline is run"
    ),

    BASELINE_DESCRIPTION(
            "cassandra.migration.baseline.description",
            "Description to apply to an existing schema when baseline is run"
    ),

    // Version target configuration properties
    // ~~~~~~
    TARGET_VERSION(
            "cassandra.migration.version.target",
            "The target version. Migrations with a higher version number will be ignored."
    ),

    // Cluster configuration properties
    // ~~~~~~
    CONTACT_POINTS(
            "cassandra.migration.cluster.contactpoints",
            "Comma separated values of node IP addresses"
    ),

    PORT(
            "cassandra.migration.cluster.port",
            "CQL native transport port"
    ),

    USERNAME(
            "cassandra.migration.cluster.username",
            "Username for password authenticator"
    ),

    PASSWORD(
            "cassandra.migration.cluster.password",
            "Password for password authenticator"
    ),

    ENABLE_SSL(
            "cassandra.migration.cluster.enablessl",
            "Enable SSL"
    ),

    ENABLE_JMX(
            "cassandra.migration.cluster.enablejmx",
            "Enable JMX"
    ),

    ENABLE_METRICS(
            "cassandra.migration.cluster.enablemetrics",
            "Enable metrics"
    ),

    TRUSTSTORE(
            "cassandra.migration.cluster.truststore",
            "Path to the truststore for client SSL"
    ),

    TRUSTSTORE_PASSWORD(
            "cassandra.migration.cluster.truststore_password",
            "Password for the truststore"
    ),

    KEYSTORE(
            "cassandra.migration.cluster.keystore",
            "Path to the keystore for client SSL certificate authentication"
    ),

    KEYSTORE_PASSWORD(
            "cassandra.migration.cluster.keystore_password",
            "Password for the keystore"
    ),

    // Keyspace name configuration properties
    // ~~~~~~
    KEYSPACE_NAME(
            "cassandra.migration.keyspace.name",
            "Name of Cassandra keyspace"
    ),

    CONSISTENCY_LEVEL(
            "cassandra.migration.keyspace.consistency",
            "Keyspace write consistency levels for migrations schema tracking"
    ),
    CASSANDRA_PREFIX(
            "cassandra.migration.prefix",
            "Load Prefix Configuration for migration"
    )

}
