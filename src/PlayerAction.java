/**
 * Created by Jiatong Hao, Xiankang Wu and Lijun Chen on 9/27/2019.
 */

public interface PlayerAction {

    /**
     * Deals one card
     * @param deck instance of deck
     * @param hand instance of hand
     */
    void hit(TriantaEnaDeck deck, TriantaEnaHand hand);

    /**
     * The player could split into two hands, if the initial two cards are the same rank
     * @param player instance of player
     * @param hand instance of hand
     */
    void split(TriantaEnaPlayer player, TriantaEnaHand hand);

    /**
     * The player double up their bets and immediately followed by a hit and stand
     * @param deck instance of deck
     * @param hand instance of hand
     */
    void doubleUp(TriantaEnaDeck deck, TriantaEnaPlayer player, TriantaEnaHand hand);

    /**
     * the player ends and maintains the value of current hand
     */
    void stand();

    /**
     * Ask player to make initial bet
     * @param player - a player instance
     */
    void makeBet(TriantaEnaPlayer player);

    /**
     * Ask the player if he/she would like to cash out.
     * @param player - a player instance
     * @return true if player cash out, false otherwise
     */
    boolean cashOut(TriantaEnaPlayer player);
}