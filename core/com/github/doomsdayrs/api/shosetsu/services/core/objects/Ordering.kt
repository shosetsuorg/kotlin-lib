package com.github.Doomsdayrs.api.shosetsu.services.core.objects;

public enum Ordering {
    TopBottomLatestOldest(0),
    BottomTopLatestOldest(1);
    private final int a;

    Ordering(int a) {
        this.a = a;
    }

    @Override
    public String toString() {
        return "Ordering{" +
                "a=" + a +
                '}';
    }
}
