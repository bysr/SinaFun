package hipad.animal;

import java.io.Serializable;

public class Sample implements Serializable {

    final int color;
    private final String name;

    public Sample(int color, String name) {
        this.color = color;
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }


}

