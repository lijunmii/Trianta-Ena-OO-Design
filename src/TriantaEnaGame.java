/**
 * Created by Jiatong Hao, Xiankang Wu and Lijun Chen on 9/27/2019.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;
import java.util.Collections;
import java.util.Scanner;

/**
 * Class encapsulates a Trianta Ena card game.
 */
public class TriantaEnaGame extends Game implements PlayerAction {
    private final int WIN_VAL = 31;
    private final int BANKER_VAL = 27;
    private final int MIN_BANKER_VAL = 16;
    private final int MAX_BANKER_VAL = 18;
    private final int MAX_PLAYER = 7;
    private final int BALANCE = 100;
    private final int INITIAL_CARD_NUM = 3;

    private List<TriantaEnaPlayer> playerList;
    private TriantaEnaPlayer banker;
    private TriantaEnaDeck deck;
    private TriantaEnaJudge judge;
    private TriantaEnaGameLogger logger;
    private final String[] actions = {"hit", "stand"};

    private int winVal = WIN_VAL;
    private int bankerVal = BANKER_VAL;
    private int balance = BALANCE;
    private int playerCount;

    public TriantaEnaGame() {
        logger = new TriantaEnaGameLogger();
        logger.welcomeMsg();
        setGameParams();
        setPlayerNumber();
        initGame();
    }

    /**
     * Entry method of the TriantaEnaGame.
     */
    public void start() {
        logger.msg("\nGame starts!");
        while (!playerList.isEmpty()) {
            playARound();
            resetHands();
        }
        logger.msg("\nGame ends.");
    }

    /**
     * The main workflow of players in a single round. First, each player makes their bets, and then cards are dealt,
     * each player makes their move, banker plays, and the round is wrapped up.
     */
    public void playARound() {
        logger.msg("\n*****************\nRound: " + getRound());

        // deal one initial card for all player and banker
        for (TriantaEnaPlayer player : playerList) {
            TriantaEnaHand hand = player.getHand();
            TriantaEnaCard newCard = (TriantaEnaCard) deck.dealCard();
            hand.addCard(newCard);
        }
        TriantaEnaCard newCard = (TriantaEnaCard) deck.dealCard();
        banker.getHand().addCard(newCard);

        // ask player to make bet or fold
        for (TriantaEnaPlayer player : playerList) {
            logger.displayPlayerHand(player.getHand());
            makeBet(player);
        }

        dealCards(INITIAL_CARD_NUM - 1);
        playersPlay();
        bankerPlay();
        calcRoundResult();
        rotatePlayer();
        setRound(getRound() + 1);
    }

    /**
     * Initialize game params of TriantaEna Game.
     */
    private void setGameParams() {
        Scanner sc = new Scanner(System.in);
        logger.displaySetDefaultParams();
        String choice = sc.nextLine();
        if (!choice.equals("y") && !choice.equals("Y")) {
            logger.msg("The game will use default parameters.\n");
            return;
        }

        setBankerParam();
        setPlayerBalance();
        logger.msg("The game will use " + bankerVal + " as banker stopping value and " + balance + " as balance.\n");
    }

    private void setBankerParam() {
        logger.displaySetBankerParam();
        Scanner scanner = new Scanner(System.in);
        int bankerValInput = getInteger(scanner.nextLine());
        if (bankerValInput >= MIN_BANKER_VAL && bankerValInput <= MAX_BANKER_VAL) {
            this.bankerVal = bankerValInput;
        }
    }

    private void setPlayerBalance() {
        logger.displaySetBalanceParam();
        Scanner scanner = new Scanner(System.in);
        int balanceInput = getInteger(scanner.nextLine());
        if (balanceInput > 1) {
            this.balance = balanceInput;
        }
    }


    private void setPlayerNumber() {
        logger.displaySetPlayerCountInfo(MAX_PLAYER);
        Scanner scanner = new Scanner(System.in);
        boolean isPlayerValid = false;
        int playerCountInput = -1;
        while (!isPlayerValid) {
            playerCountInput = getInteger(scanner.nextLine());
            if (1 <= playerCountInput && playerCountInput <= MAX_PLAYER) {
                logger.msg("The game will have " + playerCountInput + " player(s) in the beginning.");
                isPlayerValid = true;
            } else {
                logger.displayInvalidMsgForRange(1, MAX_PLAYER);
            }
        }
        this.playerCount = playerCountInput;
    }


    private void initGame() {
        deck = new TriantaEnaDeck();
        judge = new TriantaEnaJudge(bankerVal, winVal);
        banker = new TriantaEnaPlayer(playerCount, balance);
        playerList = new ArrayList<>(playerCount);
        for (int i = 0; i < playerCount; i++)
            playerList.add(new TriantaEnaPlayer(i, balance));
    }

    /**
     * Initialize two cards to both players and bankers in alternating sequence.
     */
    private void dealCards(int cardNum) {
        for (int idx = 0; idx < cardNum; idx++) {
            for (TriantaEnaPlayer player : playerList) {
                if (judge.isFold(player.getHand())) continue;
                TriantaEnaHand hand = player.getHand();
                TriantaEnaCard newCard = (TriantaEnaCard) deck.dealCard();
                hand.addCard(newCard);
            }
            TriantaEnaCard newCard = (TriantaEnaCard) deck.dealCard();
            banker.getHand().addCard(newCard);
        }
    }

    /**
     * The workflow of all players selecting their actions in a single round.
     */
    private void playersPlay() {
        for (TriantaEnaPlayer player : playerList) {
            logger.msg("\n#################\nPlayer " + player.getId() + " starts!");
            for (int i = 0; i < player.getHandCount(); i++) {

                TriantaEnaHand hand = player.getHandAt(i);

                if (judge.isFold(hand)) {
                    logger.msg("Player " + player.getId() + " has folded.");
                    logger.displayPlayerHand(hand);
                    logger.msg("Go to next player.");
                    continue;
                }

                logger.playHandInfo(player.getId(), player.getBalance(), i, hand.getBet());
                logger.displayBankerCard(banker.getVisibleCard());

                if (judge.isNaturalTriantaEna(hand)) {
                    logger.displayPlayerHand(hand);
                    logger.msg("Your current hand is a Natural Trianta Ena! Gorgeous!!!");
                    continue;
                }

                while (!judge.isBust(hand) && !judge.isTriantaEna(hand)) {
                    logger.displayPlayerHand(hand);

                    String next_action = getUserAction(player, hand);
                    playAction(player, next_action, hand);
                    if (next_action.equals("stand") || next_action.equals("doubleUp")) {
                        break;
                    }
                }

                logger.displayPlayerHand(hand);

                if (judge.isTriantaEna(hand) && !judge.isNaturalTriantaEna(hand)) {
                    logger.msg("Your current hand is a TriantaEna! Congrats!");
                }

                if (judge.isBust(hand)) {
                    int displayedIdx = i + 1;
                    logger.msg("Player " + player.getId() + " hand " + displayedIdx + " is Bust!");
                }
            }
        }
        logger.msg("\nAll players' terms end!");
    }

    private int getInteger(String str) {
        try {
            int res = Integer.parseInt(str);
            return res;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Ask the player to select the next action, and decide if it is valid.
     *
     * @param player current player.
     * @param hand   current hand the player is dealing with.
     * @return string of the action.
     */
    private String getUserAction(TriantaEnaPlayer player, TriantaEnaHand hand) {
        Scanner sc = new Scanner(System.in);
        boolean isValid = false;
        int input = -1;
        while (!isValid) {
            logger.displayActionChoices(actions);
            // if input is valid, change isValid = true
            input = getInteger(sc.nextLine());
            if (1 <= input && input <= actions.length && judge.isActionValid(player, hand, actions[input - 1])) {
                logger.msg("Your action: " + actions[input - 1]);
                isValid = true;
            } else {
                logger.displayInvalidMsgForRange(1, actions.length);
            }
        }
        return actions[input - 1];
    }

    /**
     * Execute the action selected by a single player with factory design pattern.
     *
     * @param player current player.
     * @param action current action selected by the player.
     * @param hand   current hand the player is dealing with.
     */
    private void playAction(TriantaEnaPlayer player, String action, TriantaEnaHand hand) {
        switch (action) {
            case "hit":
                hit(deck, hand);
                break;
            case "stand":
                stand();
                break;
        }
    }

    /**
     * logic of banker. Our banker will keep on hitting until his/her cards reaches the bankerVal, or bust.
     */
    private void bankerPlay() {
        logger.msg("\n#################\nBanker starts!");

        TriantaEnaHand bankerHand = banker.getHand();

        logger.displayBankerHand(bankerHand);

        if (judge.isNaturalTriantaEna(bankerHand)) {
            logger.msg("Banker's current hand is a Natural TriantaEna! Gorgeous!!!");
        }

        while (judge.canBankerHit(banker)) {
            hit(deck, banker.getHand());
            logger.msg("Banker hits!");
            logger.displayBankerHand(bankerHand);
        }

        if (judge.isTriantaEna(bankerHand) && !judge.isNaturalTriantaEna(bankerHand)) {
            logger.msg("Your current hand is a TriantaEna! Congrats!");
        }

        if (judge.isBust(bankerHand)) {
            logger.msg("Banker hand is Bust!");
        }

        logger.msg("Banker's term ends!");
        logger.msg("#################\n");
    }

    /**
     * Remove players with $0 balance, and ask other players if they would like to cash out or join the next round.
     */
    private void calcRoundResult() {
        List<TriantaEnaPlayer> toRemove = new ArrayList<>();
        logger.printBankerBalance(banker.getId(), banker.getBalance(), getRound());
        for (TriantaEnaPlayer player : playerList) {
            if (!judge.isFold(player.getHand())) {
                int roundBalance = judge.checkWinner(player, banker);
                logger.printPlayerBalance(player.getId(), roundBalance, player.getBalance(), getRound());
            }
            if (player.getBalance() == 0) {
                logger.playerLeaves(player);
                toRemove.add(player);
            } else if (cashOut(player)) {
                logger.playerLeaves(player);
                toRemove.add(player);
            }
        }
        for (TriantaEnaPlayer player : toRemove) {
            playerList.remove(player);
        }
    }

    /**
     * Rotate the banker with the player who has balance larger than the dealer and agrees to rotate.
     * If they decline, the player with the next greatest amount is given the same option.
     */
    private void rotatePlayer() {
        Scanner sc = new Scanner(System.in);
        TreeMap<Integer, Integer> sortedBalanceMap = new TreeMap<>(Collections.reverseOrder());
        int count = 0;

        // TreeMap sort the keys in descending order. Key is the playerBalance, and the value is the playerID
        for (TriantaEnaPlayer player : playerList) {
            sortedBalanceMap.put(player.getBalance(), player.getId());
        }

        for (Map.Entry<Integer, Integer> entry : sortedBalanceMap.entrySet()) {
            Integer balance = entry.getKey();
            Integer playerID = entry.getValue();
            if (++count == 1 && balance <= banker.getBalance()) {
                logger.msg("Current balance of banker " + banker.getId() + " is: " + banker.getBalance());
                logger.msg("This round, no one is eligible to become the new Banker.");
                return;
            }
            logger.msg("Player " + playerID + ", your current balance = $" + balance +
                    " exceeds that of the Banker.");
            logger.msg("If you want to become the Banker, please enter Y/y to rotate. " +
                    "Enter other inputs to decline.");
            String choice = sc.nextLine();
            if (!choice.equals("y") && !choice.equals("Y")) {
                logger.msg("Player " + playerID + "did not become the Banker.\n");
            } else {
                logger.msg("Player " + playerID + " is now the new Banker!\n");
                logger.msg("Banker " + banker.getId() + " now is Player " + banker.getId() + "!\n");
                playerList.add(banker);
                for (TriantaEnaPlayer player : playerList) {
                    if (player.getId() == playerID) {
                        banker = player;
                        playerList.remove(player);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Reset hands of players and banker.
     */
    private void resetHands() {
        banker.clearHands();
        banker.addHand(new TriantaEnaHand());
        for (TriantaEnaPlayer player : playerList) {
            player.clearHands();
            player.addHand(new TriantaEnaHand());
        }
    }

    /**
     * Deals one card
     *
     * @param deck instance of deck
     * @param hand instance of hand
     */
    @Override
    public void hit(TriantaEnaDeck deck, TriantaEnaHand hand) {
        TriantaEnaCard newCard = (TriantaEnaCard) deck.dealCard();
        hand.addCard(newCard);
    }

    /**
     * Player stands means that he/she finish the action of the current hand.
     */
    @Override
    public void stand() {
        return;
    }

    /**
     * Ask player to make initial bet
     *
     * @param player - a player instance
     */
    @Override
    public void makeBet(TriantaEnaPlayer player) {
        Scanner sc = new Scanner(System.in);
        int input;
        boolean isValid = false;
        logger.msg("Current balance of player " + player.getId() + " is: " + player.getBalance());
        logger.msg("Player " + player.getId() + ", please enter an integer between 1 and " + player.getBalance() + " as bet.");
        logger.msg("Enter 0 if you decide to fold.");

        while (!isValid) {
            input = getInteger(sc.nextLine());
            if (input >= 0 && input <= player.getBalance()) {
                isValid = true;
                player.getHandAt(0).setBet(input);
                player.setBalance(-input);
            } else {
                logger.msg("Invalid input. Please enter an integer between 0 and " + player.getBalance() + " as bet: ");
            }
        }
    }

    /**
     * Ask the player if he/she would like to cash out.
     *
     * @param player - instance of player
     * @return true if player cash out, false otherwise
     */
    @Override
    public boolean cashOut(TriantaEnaPlayer player) {

        Scanner scanner = new Scanner(System.in);
        boolean isCashOut = false;
        logger.msg("Player " + player.getId() + ", do you want to cash out? Please enter Y/y for yes. All other input means no.");
        String choice = scanner.nextLine();
        if (choice.equals("y") || choice.equals("Y")) {
            isCashOut = true;
        }
        return isCashOut;
    }
}