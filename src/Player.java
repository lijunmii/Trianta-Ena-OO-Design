/**
 * Created by Jiatong Hao, Xiankang Wu and Lijun Chen on 10/05/2019.
 */

import java.util.ArrayList;
import java.util.List;

public class Player<E extends Hand> {

    private int id;

    private List<E> hands;

    public Player() {
        hands = new ArrayList<E>();
    }

    public Player(int id) {
        this.id = id;
        hands = new ArrayList<E>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<E> getHands() {
        return hands;
    }

    public void clearHands() {
        hands = new ArrayList<E>();
    }

    public void addHand(E hand) {
        hands.add(hand);
    }
}