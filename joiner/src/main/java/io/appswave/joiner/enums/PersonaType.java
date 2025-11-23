package io.appswave.joiner.enums;

public enum PersonaType {
    INDIVIDUAL("Individual"),
    BUSINESS("Business"),
    GOVERNMENT("Government");

    private final String label;

    PersonaType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
