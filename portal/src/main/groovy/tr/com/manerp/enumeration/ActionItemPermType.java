package tr.com.manerp.enumeration;

public enum ActionItemPermType {
    AUTHORIZED("AUTHORIZED"),
    UNAUTHORIZED("UNAUTHORIZED");

    final String value;

    private ActionItemPermType(String value) { this.value = value; }

    public String toString() {
        return value; }
}
