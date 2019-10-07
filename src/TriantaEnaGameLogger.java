/**
 * Created by Jiatong Hao, Xiankang Wu and Lijun Chen on 9/27/2019.
 */

public class TriantaEnaGameLogger extends GameLogger {

    public void welcomeMsg() {
        msg("Welcome to our game!");
        msg("########\n");
    }

    public void displaySetDefaultParams() {
        msg("Before we start, do you want to change the parameters of the game?");
        msg("Default banker stopping value is 27, and default balance for each player is 100.");
        msg("Enter Y/y to change. All other inputs means using default parameter.");
    }

    public void displaySetBankerParam() {
        msg("Please enter a number between 24 and 29 as banker's stopping value. ");
        msg("All other inputs means using default parameter.");
    }

    public void displaySetBalanceParam() {
        msg("Please enter a number larger than 1 as balance for each player.");
        msg("All other inputs means using default parameter.");
    }

    public void displaySetPlayerCountInfo(int max_player) {
        msg("Please tell us how many players will join the game.");
        msg("We do not allow new players to join after the game starts.");
        msg("The max number of players allowed is " + max_player + ".");
        msg("Please enter an integer between 2 and " + max_player + " (include banker): ");
    }

    public void displayInvalidMsgForRange(int min_number, int max_number) {
        msg("Invalid input. Please enter a number between " + min_number + " and " + max_number + ": ");
    }

    public void playHandInfo(int playerId, int playerBalance, int handIdx, int bet) {
        int displayedHandIdx = handIdx + 1;
        msg("Player " + playerId + ", Hand no." + displayedHandIdx + ", Bet for this hand = $" +
                bet + ", Balance = $" + playerBalance );
    }

    public void displayBankerCard(TriantaEnaCard card) {
        msg("Banker's face-up card: " + card);
    }

    public void displayBankerHand(TriantaEnaHand hand) {
        msg("Banker's current hand is: \n" + hand);
        msg("Banker's current hand has value: " + hand.getTotalValue() + "\n");
    }

    public void displayPlayerHand(int playerId, TriantaEnaHand hand) {
        msg("Player " + playerId + ", your current hand is: " + hand);
        msg("Your current hand has value: " + hand.getTotalValue());
    }


    public void displayActionChoices(String[] actions) {
        msg("Please select your next action with its corresponding number (e.g., 1 to hit):");
        int idx = 1;
        for (String action : actions) {
            System.out.print(action + ": " + idx++ + "\t");
        }
        msg("\n");
    }

    public void printPlayerBalance(int playerId, int roundBalance, int playerBalance, int roundNum) {
        if (roundBalance > 0)
            msg("At round " + roundNum + ", Player " + playerId + " wins $" + roundBalance + ".");
        else if (roundBalance == 0)
            msg("At round " + roundNum + ", Player " + playerId + " is tie.");
        else
            msg("At round " + roundNum + ", Player " + playerId + " loses $" + -roundBalance + ".");
        msg("Player " + playerId + " current balance is $" + playerBalance);
    }

    public void printBankerBalance(int playerId, int balance, int roundNum) {
            msg("At round " + roundNum + ", Banker " + playerId + " has balance $" + balance + ".\n");
    }

    public void playerLeaves(TriantaEnaPlayer player) {
        msg("Player " + player.getId() + " leaves the game with $" + player.getBalance());
    }
}