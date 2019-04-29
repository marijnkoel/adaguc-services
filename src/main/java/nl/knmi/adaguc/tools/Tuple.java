package nl.knmi.adaguc.tools;

public class Tuple<TFirst, TSecond> {
    private TFirst first;
    private TSecond second;


    public Tuple(TFirst first, TSecond second) {
        this.first = first;
        this.second = second;
    }

    public TFirst getFirst() {
        return first;
    }

    public void setFirst(TFirst first) {
        this.first = first;
    }

    public TSecond getSecond() {
        return second;
    }

    public void setSecond(TSecond second) {
        this.second = second;
    }
}
