package thrones.game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static ch.aplu.jgamegrid.GameGrid.delay;

public class Player {
    static Random random;
    private Optional<Card> selected;

    private int nbPlayers;

    public Player(int seed, int num){
        random = new Random(seed);
        nbPlayers = num;
    }
    public int getPlayerIndex(int index) {
        return index % nbPlayers;
    }
    public Optional<Card> waitForCorrectSuit(int playerIndex, boolean isCharacter, Hand[] hands) {
        if (hands[playerIndex].isEmpty()) {
            selected = Optional.empty();
        } else {
            selected = null;
            hands[playerIndex].setTouchEnabled(true);
            do {
                if (selected == null) {
                    delay(100);
                    continue;
                }
                Suit suit = selected.isPresent() ? (Suit) selected.get().getSuit() : null;
                if (isCharacter && suit != null && suit.isCharacter() ||         // If we want character, can't pass and suit must be right
                        !isCharacter && (suit == null || !suit.isCharacter())) { // If we don't want character, can pass or suit must not be character
                    // if (suit != null && suit.isCharacter() == isCharacter) {
                    break;
                } else {
                    selected = null;
                    hands[playerIndex].setTouchEnabled(true);
                }
                delay(100);
            } while (true);
        }
        return selected;
    }
    public Optional<Card> pickACorrectSuit(int playerIndex, boolean isCharacter, Hand[] hands) {
        Hand currentHand = hands[playerIndex];
        List<Card> shortListCards = new ArrayList<>();
        for (int i = 0; i < currentHand.getCardList().size(); i++) {
            Card card = currentHand.getCardList().get(i);
            Suit suit = (Suit) card.getSuit();
            if (suit.isCharacter() == isCharacter) {
                shortListCards.add(card);
            }
        }
        if (shortListCards.isEmpty() || !isCharacter && random.nextInt(3) == 0) {
            selected = Optional.empty();
        } else {
            selected = Optional.of(shortListCards.get(random.nextInt(shortListCards.size())));
        }
        return selected;
    }

}
