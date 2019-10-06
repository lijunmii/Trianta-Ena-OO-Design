/**
 * Created by Jiatong Hao, Xiankang Wu and Lijun Chen on 9/27/2019.
 */

public class TriantaEnaHand extends Hand<TriantaEnaCard> {

    private int bet;
    private int winValue = 31;
    private int hardValue = 10;

    public TriantaEnaHand() {
        super();
    }

    public TriantaEnaHand(TriantaEnaCard card) {
        super(card);
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int newBet) {
        bet = newBet;
    }

    /**
     * Compute the total value of cards within the current hand.
     *
     * @return total value of cards within the current hand.
     */
    public int getTotalValue() {
        int value = 0;
        int aceCount = 0;
        int cardCount = getCardCount();

        for (int i = 0; i < cardCount; i++) {
            // Add the value of each card in the hand
            TriantaEnaCard card = (TriantaEnaCard) getCardAt(i);
            int cardSoftValue = card.getSoftValue();
            value += cardSoftValue;

            if (card.getHardValue() == hardValue + 1) {
                aceCount += 1;
            }
        }

        // If there is only one aceCard, pick a hard value or soft value.
        if (aceCount == 1) {
            if (value + hardValue <= winValue) {
                value += hardValue;
            } else if (value + hardValue > winValue && value + 1 <= winValue) {
                value += 1;
            } else {
                value += hardValue;
            }
        }

        // If a hand has more than one Ace cards, use the hard value as long as the hand is not busted
//      while (aceCount > 1 && value + hardValue <= winValue) {
        while (aceCount > 1) {
            value += hardValue;
            aceCount -= 1;
        }

        return value;
    }
}

