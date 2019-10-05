/**
 * Created by Jiatong Hao, Xiankang Wu and Lijun Chen on 9/27/2019.
 */

import java.util.ArrayList;
import java.util.List;

public class TriantaEnaDeck extends Deck {

    /**
     * Initialize the deck with cards.
     */
    public void createDeck() {
        List<TriantaEnaCard> cards = new ArrayList<TriantaEnaCard>();
        String[] suits = new String[]{"Spade", "Heart", "Club", "Diamond"};
        for (String suit : suits) {
            cards.add(new TriantaEnaAceCard(suit));
            for (int i = 2; i <= 10; i++) {
                cards.add(new TriantaEnaCard(suit, i));
            }
            for (int i = 11; i <= 13; i++) {
                cards.add(new TriantaEnaFaceCard(suit, i));
            }
        }
        setCards(cards);
    }
}
