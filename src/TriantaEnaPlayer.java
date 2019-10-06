/**
 * Created by Jiatong Hao, Xiankang Wu and Lijun Chen on 10/05/2019.
 */

public class TriantaEnaPlayer extends Player {
    private int balance;

    /**
     * Constructor.
     *
     * @param id      player id.
     * @param balance Initial balance for the player.
     */
    public TriantaEnaPlayer(int id, int balance) {
        super(id);
        this.balance = balance;
        addHand(new TriantaEnaHand());
    }

    /**
     * Copy constructor of the player.
     * @param other instance of the player to be copied from.
     */
    public TriantaEnaPlayer(TriantaEnaPlayer other) {
        System.out.println("Copy constructor called");
        this.balance = other.balance; // you can access
        super.setId(other.getId());
    }

    /**
     * Getter for player's balance
     *
     * @return player balance
     */
    public int getBalance() {
        return balance;
    }

    /**
     * Setter for player's balance
     *
     * @param currency player balance
     */
    public void setBalance(int currency) {
        balance += currency;
    }

    /**
     * Getter for hands.
     *
     * @return the number of hands.
     */
    public int getHandCount() {
        return getHands().size();
    }

    /**
     * Get the hand.
     *
     * @param idx index of the hand.
     * @return an instance of TriantaEnaHand.
     */
    public TriantaEnaHand getHandAt(int idx) {
        return (TriantaEnaHand) getHands().get(idx);
    }

    /**
     * Get the face-up card in dealer hand. In default it returns the second one.
     * First one is treated as face-down.
     *
     * @return the card that is face-down.
     */
    public TriantaEnaCard getVisibleCard() {
        return ((TriantaEnaHand) getHands().get(0)).getCardAt(1);
    }

    /**
     * Get the first hand within hands.
     *
     * @return a hand instance
     */
    public TriantaEnaHand getHand() {
        return (TriantaEnaHand) getHands().get(0);
    }
}
