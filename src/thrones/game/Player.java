package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static ch.aplu.jgamegrid.GameGrid.delay;


public class Player {
    static public int seed = 130006;
    static Random random = new Random(seed);;


    public Optional<Card> pickACorrectSuit(int playerIndex, boolean isCharacter, Hand[] hands) {
        Optional<Card> selected;
        Hand currentHand = hands[playerIndex];
        List<Card> shortListCards = new ArrayList<>();
        for (int i = 0; i < currentHand.getCardList().size(); i++) {
            Card card = currentHand.getCardList().get(i);
            GameOfThrones.Suit suit = (GameOfThrones.Suit) card.getSuit();
            if (suit.isCharacter() == isCharacter) {
                System.out.println("card added");
                shortListCards.add(card);
            }
        }
        if (shortListCards.isEmpty() || !isCharacter && random.nextInt(3) == 0){
            selected = Optional.empty();
        } else {
            selected = Optional.of(shortListCards.get(random.nextInt(shortListCards.size())));
        }
        System.out.println("in player class");
        System.out.println(selected);
        return selected;
    }

    //im thinking is too hard to refactor the code for human players because we need to port over all the code for the card listeners as well
    //would probably be easier to just make a class for the AI players
    public void waitForCorrectSuit(int playerIndex, boolean isCharacter, Hand[] hands, Optional<Card> selected) {
        if (hands[playerIndex].isEmpty()) {
            selected = Optional.empty();
        } else {
            System.out.println("inside else loop");
            selected = null;
            hands[playerIndex].setTouchEnabled(true);
            do {
                System.out.println("inside do loop");
                if (selected == null) {
                    delay(100);
                    System.out.println("where u break0");
                    continue;
                }
                System.out.println("where u break1");
                GameOfThrones.Suit suit = selected.isPresent() ? (GameOfThrones.Suit) selected.get().getSuit() : null;
                if (isCharacter && suit != null && suit.isCharacter() ||         // If we want character, can't pass and suit must be right
                        !isCharacter && (suit == null || !suit.isCharacter())) { // If we don't want character, can pass or suit must not be character
                    // if (suit != null && suit.isCharacter() == isCharacter) {
                    System.out.println(selected);
                    break;

                } else {
                    selected = null;
                    hands[playerIndex].setTouchEnabled(true);
                }
                System.out.println("where u break2");
                delay(100);
                System.out.println("finish do loop");
            } while (true);
        }
    }

}