/**
 * Created by Jiatong Hao, Xiankang Wu and Lijun Chen on 10/05/2019.
 */

public class Card {
    private String suit;
    private int value;

    public Card(String suit, int value) {
        this.suit = suit;
        this.value = value;
    }

    public String getSuit() {
        return suit;
    }

    public int getValue() {
        return value;
    }

    // returns a string with suit and value to print
    @Override
    public String toString() {
        return getSuit() + " " + getValue();
    }
}
