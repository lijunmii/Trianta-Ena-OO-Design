/**
 * Created by Jiatong Hao, Xiankang Wu and Lijun Chen on 9/27/2019.
 */

public interface PlayerAction {

    /**
     * Deals one card
     *
     * @param deck instance of deck
     * @param hand instance of hand
     */
    void hit(TriantaEnaDeck deck, TriantaEnaHand hand);

    /**
     * Player stands.
     */
    void stand();

    /**
     * Ask player to make a bet
     *
     * @param player - instance of player
     */
    void makeBet(TriantaEnaPlayer player);

    /**
     * Ask the player if he/she would like to cash out.
     *
     * @param player - instance of player
     * @return true if player cashes out, false otherwise
     */
    boolean cashOut(TriantaEnaPlayer player);
}