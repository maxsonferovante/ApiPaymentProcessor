package com.maal.apipaymentprocessor.domain.model;


import java.time.Instant;
import java.util.Objects;

public class HealthStatus {
    private final boolean failing;
    private final int minResponseTime;
    private final Instant lastCheckedAt; // Para controle de cache e validade

    public HealthStatus(boolean failing, int minResponseTime, Instant lastCheckedAt) {
        this.failing = failing;
        this.minResponseTime = minResponseTime;
        this.lastCheckedAt = Objects.requireNonNull(lastCheckedAt, "lastCheckedAt cannot be null");
    }

    public boolean isFailing() {
        return failing;
    }

    public int getMinResponseTime() {
        return minResponseTime;
    }

    public Instant getLastCheckedAt() {
        return lastCheckedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HealthStatus that = (HealthStatus) o;
        return failing == that.failing &&
                minResponseTime == that.minResponseTime &&
                lastCheckedAt.equals(that.lastCheckedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(failing, minResponseTime, lastCheckedAt);
    }

    @Override
    public String toString() {
        return "HealthStatus{" +
                "failing=" + failing +
                ", minResponseTime=" + minResponseTime +
                ", lastCheckedAt=" + lastCheckedAt +
                '}';
    }
}