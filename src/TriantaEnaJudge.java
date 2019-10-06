/**
 * Created by Jiatong Hao, Xiankang Wu and Lijun Chen on 9/27/2019.
 */

import java.util.List;

public class TriantaEnaJudge extends Judge<TriantaEnaPlayer, TriantaEnaPlayer> {

    private int bankerValue;
    private int winValue;
    private int INITIAL_CARD_NUM = 3;
    private int FACE_CARD_MIN_VALUE = 11;

    /**
     * Constructor.
     *
     * @param bankerValue value that the banker will stop hitting when his/her hand reaches. In default it is 17.
     * @param winValue    value that the Blackjack refers to. In default it is 21.
     */
    public TriantaEnaJudge(int bankerValue, int winValue) {
        this.bankerValue = bankerValue;
        this.winValue = winValue;
    }

    public int getBankerValue() {
        return bankerValue;
    }

    public void setBankerValue(int bankerValue) {
        this.bankerValue = bankerValue;
    }

    public int getWinValue() {
        return winValue;
    }

    public void setWinValue(int winValue) {
        this.winValue = winValue;
    }

    /**
     * Tells if the action for a specific hand of a player is valid.
     *
     * @param player current player.
     * @param hand   hand that the current player is holding.
     * @param action string represents the player action.
     * @return boolean. Returns true if the action is valid, false otherwise.
     */
    public boolean isActionValid(TriantaEnaPlayer player, TriantaEnaHand hand, String action) {
        switch (action) {
            case "hit":
                return !isBust(hand);
            case "split":
                return isSplittable(player, hand);
            case "doubleUp":
                return isEnoughBalance(player, hand.getBet());
        }
        return true;
    }

    /**
     * Tells if the current hand is bust.
     *
     * @param hand the hand instance.
     * @return True if bust, false otherwise.
     */
    public boolean isBust(TriantaEnaHand hand) {
        return hand.getTotalValue() > this.winValue;
    }


    /**
     * Tells if the current hand is fold.
     *
     * @param hand the hand instance.
     * @return True if fold, false otherwise.
     */
    public boolean isFold(TriantaEnaHand hand) {
        return hand.getBet() == 0;
    }

    private boolean isEnoughBalance(TriantaEnaPlayer player, int bet) {
        return player.getBalance() - bet >= 0;
    }

    private boolean isSplittable(TriantaEnaPlayer player, TriantaEnaHand hand) {
        // check if there is only two cards in this hand &  check if balance can afford two bets
        if (hand.getCardCount() != 2) return false;
        if (!isEnoughBalance(player, hand.getBet())) return false;
        int cardValue1 = hand.getCardAt(0).getHardValue();
        int cardValue2 = hand.getCardAt(1).getHardValue();
        return cardValue1 == cardValue2;
    }

    /**
     * Tells if the banker can still hit.
     *
     * @param banker banker instance.
     * @return True if the banker can hit, false otherwise.
     */
    public boolean canBankerHit(TriantaEnaPlayer banker) {
        return banker.getHand().getTotalValue() < bankerValue;
    }

    /**
     * Tells if the hand is a Blackjack. A Blackjack means that the total value of the hand cards is 21.
     *
     * @param hand hand instance.
     * @return if the current hand is Blackjack.
     */
    public boolean isTriantaEna(TriantaEnaHand hand) {
        return hand.getTotalValue() == this.winValue;
    }

    /**
     * Tells if the hand is a natural Blackjack. A natural Blackjack is a BlackJack with one Ace and one Face Card.
     *
     * @param hand hand instance.
     * @return if the current hand is natural Blackjack.
     */
    public boolean isNaturalTriantaEna(TriantaEnaHand hand) {
        if (hand.getCardCount() != INITIAL_CARD_NUM)
            return false;
        int aceCardCount = 0;
        int faceCardCount = 0;
        for (TriantaEnaCard card : hand.getHand()) {
            if (card.getValue() == 1) {
                aceCardCount++;
            } else if (card.getValue() >= FACE_CARD_MIN_VALUE) {
                faceCardCount++;
            }
        }
        return aceCardCount == 1 && faceCardCount == 2;
    }

    /**
     * Compaer each hand of the player and the one of the banker.
     *
     * @param player instance of player.
     * @param banker instance of banker.
     * @return Balance that the current player wins or loses. If wins or tie, it is positive, otherwise it is negative.
     */
    public int checkWinner(TriantaEnaPlayer player, TriantaEnaPlayer banker) {
        TriantaEnaHand bankerHand = banker.getHand();
        int bankerValue = bankerHand.getTotalValue();

        int roundBalance = 0;

        if (isBust(bankerHand)) {
            // if banker is bust
            for (int i = 0; i < player.getHandCount(); i++) {
                TriantaEnaHand playerHand = player.getHandAt(i);
                int bet = playerHand.getBet();

                if (!isBust(playerHand)) {
                    // if not bust, player hand wins
                    player.setBalance(bet * 2);
                    roundBalance += playerHand.getBet();
                } else {
                    // if this player hand bust, both player and banker lose, tie
                    // if tie, player loses, and banker get the bet
                    roundBalance -= playerHand.getBet();
                    banker.setBalance(bet);
                }
            }
        } else {
            // if banker does not bust
            for (int i = 0; i < player.getHandCount(); i++) {
                TriantaEnaHand playerHand = player.getHandAt(i);
                int value = playerHand.getTotalValue();
                int bet = playerHand.getBet();

                if (isBust(playerHand)) {
                    // if player hand bust, player hand loses, and banker get the bet
                    roundBalance -= bet;
                    banker.setBalance(bet);
                } else {
                    // if player folds
                    if (value == 0) {
                        roundBalance = 0;
                    }  else if (value < bankerValue) { // if player hand not bust
                        // if player hand value < banker hand value, player hand loses
                        banker.setBalance(bet);
                        roundBalance -= bet;
                    } else if (value == bankerValue) {
                        if (isNaturalTriantaEna(bankerHand) && isNaturalTriantaEna(playerHand)) {
                            // both banker hand & player hand is natural blackjack, tie
                            // if tie, player loses
                            banker.setBalance(bet);
                            roundBalance -= bet;
                        } else if (isNaturalTriantaEna(bankerHand) && !isTriantaEna(playerHand)) {
                            // banker hand == natural blackjack && player hand == blackjack, player hand loses
                            banker.setBalance(bet);
                            roundBalance -= bet;
                        } else if (isTriantaEna(bankerHand) && isNaturalTriantaEna(playerHand)) {
                            // banker hand == blackjack && player hand == natural blackjack, player hand wins
                            // If the Player wins, they keep their bet and receive their bet * 2 from the Banker
                            player.setBalance(bet * 2);
                            banker.setBalance(-bet);
                            roundBalance += bet;
                        } else {
                            // both blackjack or neither blackjack, nor natural blackjack, tie
                            // if tie, player loses
                            banker.setBalance(bet);
                            roundBalance -= bet;
                        }
                    } else {
                        // if player hand value > banker hand value, player hand wins
                        player.setBalance(bet * 2);
                        roundBalance += bet;
                    }
                }
            }
        }
        return roundBalance;
    }
}