package com.noteit.noteit.utils;

public enum Ranks {
    Just_Started("Just Started"),
    Popandau("Popandau"),
    Code_Monkey("Code Monkey"),
    Big_Kahuna_Boss("Big Kahuna Boss"),
    God_Of_Code("God of Code");

    private final String name;

    private Ranks(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
