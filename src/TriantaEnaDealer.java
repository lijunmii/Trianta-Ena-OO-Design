/**
 * Created by Jiatong Hao, Xiankang Wu and Lijun Chen on 9/23/2019.
 */

public class TriantaEnaDealer extends Player {

    TriantaEnaDealer() {
        super();
        addHand(new TriantaEnaHand());
    }

    /**
     * Get the face-up card in dealer hand. In default it returns the second one.
     * First one is treated as face-down.
     *
     * @return the card that is face-down.
     */
    public TriantaEnaCard getVisibleCard() {
        return ((TriantaEnaHand)getHands().get(0)).getCardAt(1);
    }

    /**
     * Get the first hand within hands.
     * @return a hand instance
     */
    public TriantaEnaHand getHand() {
        return (TriantaEnaHand) getHands().get(0);
    }
}
