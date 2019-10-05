/**
 * Created by Jiatong Hao, Xiankang Wu and Lijun Chen on 9/27/2019.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class encapsulates a Trianta Ena card game.
 */
public class TriantaEnaGame extends Game implements PlayerAction {
    private final int WIN_VAL = 21;
    private final int DEALER_VAL = 17;
    private final int MIN_DEALER_VAL = 16;
    private final int MAX_DEALER_VAL = 18;
    private final int MAX_PLAYER = 4;
    private final int BALANCE = 100;
    private final int INITIAL_CARD_NUM = 2;

    private List<TriantaEnaPlayer> playerList;
    private TriantaEnaDealer dealer;
    private TriantaEnaDeck deck;
    private TriantaEnaJudge judge;
    private TriantaEnaGameLogger visualizer;
    private final String[] actions = {"hit", "stand"};

    private int winVal = WIN_VAL;
    private int dealerVal = DEALER_VAL;
    private int balance = BALANCE;
    private int playerCount;


    public TriantaEnaGame() {
        visualizer = new TriantaEnaGameLogger();
        visualizer.welcomeMsg();
        setGameParams();
        setPlayerNumber();
        initGame();
    }

    /**
     * Entry method of the TriantaEnaGame.
     */
    public void start() {
        System.out.println("\nGame starts!");
        while (!playerList.isEmpty()) {
            playARound();
            resetHands();
        }
        System.out.println("\nGame ends.");
    }

    /**
     * The main workflow of players in a single round. First, each player makes their bets, and then cards are dealt,
     * each player makes their move, dealer plays, and the round is wrapped up.
     */
    public void playARound() {
        System.out.println("\n*****************\nRound: " + getRound());

        for (TriantaEnaPlayer player : playerList) {
            makeBet(player);
        }
        dealInitialCards();
        playersPlay();
        dealerPlay();
        calcRoundResult();
        setRound(getRound() + 1);
    }

    /**
     * Initialize game params of TriantaEna Game.
     */
    private void setGameParams() {
        Scanner sc = new Scanner(System.in);
        visualizer.displaySetDefaultParams();
        String choice = sc.nextLine();
        if (!choice.equals("y") && !choice.equals("Y")) {
            System.out.println("The game will use default parameters.\n");
            return;
        }

        setDealerParam();
        setPlayerBalance();
        System.out.println("The game will use " + dealerVal + " as dealer stopping value and " + balance + " as balance.\n");
    }

    private void setDealerParam() {
        visualizer.displaySetDealerParam();
        Scanner scanner = new Scanner(System.in);
        int dealerValInput = getInteger(scanner.nextLine());
        if (dealerValInput >= MIN_DEALER_VAL && dealerValInput <= MAX_DEALER_VAL) {
            this.dealerVal = dealerValInput;
        }
    }

    private void setPlayerBalance() {
        visualizer.displaySetBalanceParam();
        Scanner scanner = new Scanner(System.in);
        int balanceInput = getInteger(scanner.nextLine());
        if (balanceInput > 1) {
            this.balance = balanceInput;
        }
    }


    private void setPlayerNumber() {
        visualizer.displaySetPlayerCountInfo(MAX_PLAYER);
        Scanner scanner = new Scanner(System.in);
        boolean isPlayerValid = false;
        int playerCountInput = -1;
        while (!isPlayerValid) {
            playerCountInput = getInteger(scanner.nextLine());
            if (1 <= playerCountInput && playerCountInput <= MAX_PLAYER) {
                System.out.println("The game will have " + playerCountInput + " player(s) in the beginning.");
                isPlayerValid = true;
            } else {
                visualizer.displayInvalidMsgForRange(1, MAX_PLAYER);
            }
        }
        this.playerCount = playerCountInput;
    }


    private void initGame() {
        deck = new TriantaEnaDeck();
        judge = new TriantaEnaJudge(dealerVal, winVal);
        dealer = new TriantaEnaDealer();
        playerList = new ArrayList<>(playerCount);
        for (int i = 0; i < playerCount; i++)
            playerList.add(new TriantaEnaPlayer(i + 1, balance));
    }

    /**
     * Initialize two cards to both players and dealers in alternating sequence.
     */
    private void dealInitialCards() {
        for (int idx = 0; idx < this.INITIAL_CARD_NUM; idx++) {
            for (TriantaEnaPlayer player : playerList) {
                TriantaEnaCard newCard = (TriantaEnaCard) deck.dealCard();
                player.getHandAt(0).addCard(newCard);
            }
            TriantaEnaCard newCard = (TriantaEnaCard) deck.dealCard();
            dealer.getHand().addCard(newCard);
        }
    }

    /**
     * The workflow of all players selecting their actions in a single round.
     */
    private void playersPlay() {
        for (TriantaEnaPlayer player : playerList) {
            System.out.println("\n#################\nPlayer " + player.getId() + " starts!");
            for (int i = 0; i < player.getHandCount(); i++) {
                TriantaEnaHand hand = player.getHandAt(i);
                visualizer.playHandInfo(player.getId(), player.getBalance(), i, hand.getBet());
                visualizer.displayDealerCard(dealer.getVisibleCard());

                if (judge.isNaturalTriantaEna(hand)) {
                    visualizer.displayPlayerHand(hand);
                    System.out.println("Your current hand is a Natural TriantaEna! Gorgeous!!!");
                    continue;
                }

                while (!judge.isBust(hand) && !judge.isTriantaEna(hand)) {
                    visualizer.displayPlayerHand(hand);

                    String next_action = getUserAction(player, hand);
                    playAction(player, next_action, hand);
                    if (next_action.equals("stand") || next_action.equals("doubleUp")) {
                        break;
                    }
                }

                visualizer.displayPlayerHand(hand);

                if (judge.isTriantaEna(hand)) {
                    System.out.println("Your current hand is a TriantaEna! Congrats!");
                }
                if (judge.isBust(hand)) {
                    int displayedIdx = i + 1;
                    System.out.println("Player " + player.getId() + " hand " + displayedIdx + " is Bust!");
                }
            }
        }
        System.out.println("\nAll players' terms end!");
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
            visualizer.displayActionChoices(actions);
            // if input is valid, change isValid = true
            input = getInteger(sc.nextLine());
            if (1 <= input && input <= actions.length && judge.isActionValid(player, hand, actions[input - 1])) {
                System.out.println("Your action: " + actions[input - 1]);
                isValid = true;
            } else {
                visualizer.displayInvalidMsgForRange(1, actions.length);
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
     * logic of dealer. Our dealer will keep on hitting until his/her cards reaches the dealerVal, or bust.
     */
    private void dealerPlay() {
        System.out.println("\n#################\nDealer starts!");

        TriantaEnaHand dealerHand = dealer.getHand();

        visualizer.displayDealerHand(dealerHand);

        if (judge.isNaturalTriantaEna(dealerHand)) {
            System.out.println("Dealer's current hand is a Natural TriantaEna! Gorgeous!!!");
        }

        while (judge.canDealerHit(dealer)) {
            hit(deck, dealer.getHand());
            System.out.println("Dealer hits!");
            visualizer.displayDealerHand(dealerHand);
        }

        if (judge.isTriantaEna(dealerHand)) {
            System.out.println("Your current hand is a TriantaEna! Congrats!");
        }

        if (judge.isBust(dealerHand)) {
            System.out.println("Dealer hand is Bust!");
        }

        System.out.println("Dealer's term ends!");
        System.out.println("#################\n");
    }

    /**
     * Remove players with $0 balance, and ask other players if they would like to cash out or join the next round.
     */
    private void calcRoundResult() {
        List<TriantaEnaPlayer> toRemove = new ArrayList<>();
        for (TriantaEnaPlayer player : playerList) {
            int roundBalance = judge.checkWinner(player, dealer);
            visualizer.printPlayerBalance(player.getId(), roundBalance, player.getBalance(), getRound());
            if (player.getBalance() == 0) {
                visualizer.playerLeaves(player);
                toRemove.add(player);
            } else if (cashOut(player)) {
                visualizer.playerLeaves(player);
                toRemove.add(player);
            }
        }
        for (TriantaEnaPlayer player : toRemove) {
            playerList.remove(player);
        }
    }

    /**
     * Reset hands of players and dealer.
     */
    private void resetHands() {
        dealer.clearHands();
        dealer.addHand(new TriantaEnaHand());
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
        System.out.println("Current balance of player " + player.getId() + " is: " + player.getBalance());
        System.out.println("Player " + player.getId() + ", please enter an integer between 1 and " + player.getBalance() + " as bet: ");

        while (!isValid) {
            input = getInteger(sc.nextLine());
            if (input >= 0 && input <= player.getBalance()) {
                isValid = true;
                player.getHandAt(0).setBet(input);
                player.setBalance(-input);
            } else {
                System.out.println("Invalid input. Please enter an integer between 0 and " + player.getBalance() + " as bet: ");
            }
        }
    }

    /**
     * Ask the player if he/she would like to cash out.
     *
     * @param player - a player instance
     * @return true if player cash out, false otherwise
     */
    @Override
    public boolean cashOut(TriantaEnaPlayer player) {

        Scanner scanner = new Scanner(System.in);
        boolean isCashOut = false;
        System.out.println("Player " + player.getId() + ", do you want to cash out? Please enter Y/y for yes. All other input means no.");
        String choice = scanner.nextLine();
        if (choice.equals("y") || choice.equals("Y")) {
            isCashOut = true;
        }
        return isCashOut;
    }
}