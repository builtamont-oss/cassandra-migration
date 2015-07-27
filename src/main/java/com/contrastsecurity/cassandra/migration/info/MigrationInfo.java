package com.contrastsecurity.cassandra.migration.info;

import com.contrastsecurity.cassandra.migration.utils.ObjectUtils;

import java.util.Date;

public class MigrationInfo implements Comparable<MigrationInfo> {
    /**
     * The resolved migration to aggregate the info from.
     */
    private final ResolvedMigration resolvedMigration;

    /**
     * The applied migration to aggregate the info from.
     */
    private final AppliedMigration appliedMigration;

    /**
     * The current context.
     */
    private final MigrationInfoContext context;

    /**
     * Creates a new MigrationInfoImpl.
     *
     * @param resolvedMigration The resolved migration to aggregate the info from.
     * @param appliedMigration  The applied migration to aggregate the info from.
     * @param context           The current context.
     */
    public MigrationInfo(ResolvedMigration resolvedMigration, AppliedMigration appliedMigration,
                             MigrationInfoContext context) {
        this.resolvedMigration = resolvedMigration;
        this.appliedMigration = appliedMigration;
        this.context = context;
    }

    /**
     * @return The resolved migration to aggregate the info from.
     */
    public ResolvedMigration getResolvedMigration() {
        return resolvedMigration;
    }

    /**
     * @return The applied migration to aggregate the info from.
     */
    public AppliedMigration getAppliedMigration() {
        return appliedMigration;
    }

    public Integer getChecksum() {
        if (appliedMigration != null) {
            return appliedMigration.getChecksum();
        }
        return resolvedMigration.getChecksum();
    }

    public MigrationVersion getVersion() {
        if (appliedMigration != null) {
            return appliedMigration.getVersion();
        }
        return resolvedMigration.getVersion();
    }

    public String getDescription() {
        if (appliedMigration != null) {
            return appliedMigration.getDescription();
        }
        return resolvedMigration.getDescription();
    }

    public String getScript() {
        if (appliedMigration != null) {
            return appliedMigration.getScript();
        }
        return resolvedMigration.getScript();
    }

    public MigrationState getState() {
        if (appliedMigration == null) {
            if (resolvedMigration.getVersion().compareTo(context.baseline) < 0) {
                return MigrationState.BELOW_BASELINE;
            }
            if (resolvedMigration.getVersion().compareTo(context.target) > 0) {
                return MigrationState.ABOVE_TARGET;
            }
            if ((resolvedMigration.getVersion().compareTo(context.lastApplied) < 0)) {
                return MigrationState.IGNORED;
            }
            return MigrationState.PENDING;
        }

        if (resolvedMigration == null) {
            if (getVersion().compareTo(context.lastResolved) < 0) {
                if (appliedMigration.isSuccess()) {
                    return MigrationState.MISSING_SUCCESS;
                }
                return MigrationState.MISSING_FAILED;
            }
            if (getVersion().compareTo(context.lastResolved) > 0) {
                if (appliedMigration.isSuccess()) {
                    return MigrationState.FUTURE_SUCCESS;
                }
                return MigrationState.FUTURE_FAILED;
            }
        }
        return MigrationState.FAILED;
    }

    public Date getInstalledOn() {
        if (appliedMigration != null) {
            return appliedMigration.getInstalledOn();
        }
        return null;
    }

    public Integer getExecutionTime() {
        if (appliedMigration != null) {
            return appliedMigration.getExecutionTime();
        }
        return null;
    }

    /**
     * Validates this migrationInfo for consistency.
     *
     * @return The error message, or {@code null} if everything is fine.
     */
    public String validate() {
        if (!context.pendingOrFuture
                && (resolvedMigration == null)) {
            return "Detected applied migration not resolved locally: " + getVersion();
        }

        if ((!context.pendingOrFuture && (MigrationState.PENDING == getState()))
                || (MigrationState.IGNORED == getState())) {
            return "Detected resolved migration not applied to database: " + getVersion();
        }

        if (resolvedMigration != null && appliedMigration != null) {
            if (getVersion().compareTo(context.baseline) > 0) {
                if (!ObjectUtils.nullSafeEquals(resolvedMigration.getChecksum(), appliedMigration.getChecksum())) {
                    return createMismatchMessage("Checksum", appliedMigration.getVersion(),
                            appliedMigration.getChecksum(), resolvedMigration.getChecksum());
                }
                if (!resolvedMigration.getDescription().equals(appliedMigration.getDescription())) {
                    return createMismatchMessage("Description", appliedMigration.getVersion(),
                            appliedMigration.getDescription(), resolvedMigration.getDescription());
                }
            }
        }
        return null;
    }

    /**
     * Creates a message for a mismatch.
     *
     * @param mismatch The type of mismatch.
     * @param version  The offending version.
     * @param applied  The applied value.
     * @param resolved The resolved value.
     * @return The message.
     */
    private String createMismatchMessage(String mismatch, MigrationVersion version, Object applied, Object resolved) {
        return String.format("Migration " + mismatch + " mismatch for migration %s\n" +
                        "-> Applied to database : %s\n" +
                        "-> Resolved locally    : %s",
                version, applied, resolved);
    }

    @SuppressWarnings("NullableProblems")
    public int compareTo(MigrationInfo o) {
        return getVersion().compareTo(o.getVersion());
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MigrationInfo that = (MigrationInfo) o;

        if (appliedMigration != null ? !appliedMigration.equals(that.appliedMigration) : that.appliedMigration != null)
            return false;
        if (!context.equals(that.context)) return false;
        return !(resolvedMigration != null ? !resolvedMigration.equals(that.resolvedMigration) : that.resolvedMigration != null);
    }

    @Override
    public int hashCode() {
        int result = resolvedMigration != null ? resolvedMigration.hashCode() : 0;
        result = 31 * result + (appliedMigration != null ? appliedMigration.hashCode() : 0);
        result = 31 * result + context.hashCode();
        return result;
    }
}
