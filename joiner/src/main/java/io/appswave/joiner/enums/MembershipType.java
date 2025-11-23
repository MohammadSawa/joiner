package io.appswave.joiner.enums;

public enum MembershipType {
    INTERNAL("Internal Member"),
    EXTERNAL("External Member");

    private final String label;

    MembershipType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
