package thrones.game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.util.List;
import java.util.*;

public class Dealer {
    public Card randomCard(Hand hand) {
        assert !hand.isEmpty() : " random card from empty hand.";
        int x = random.nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }
    static Random random;
    public void dealingOut(Deck deck,Hand[] hands, int nbPlayers, int nbCardsPerPlayer, int seed) {
        random = new Random(seed);
        //System.out.println(seed);
        Hand pack = deck.toHand(false);
        //System.out.println(pack.getCardsWithRank(GameOfThrones.Rank.ACE));
        assert pack.getNumberOfCards() == 52 : " Starting pack is not 52 cards.";
        // Remove 4 Aces
        List<Card> aceCards = pack.getCardsWithRank(Rank.ACE);
        //System.out.println(aceCards);
        for (Card card : aceCards) {
            //System.out.println(card);
            card.removeFromHand(false);
        }
        //System.out.println(pack);
        assert pack.getNumberOfCards() == 48 : " Pack without aces is not 48 cards.";
        // Give each player 3 heart cards
        for (int i = 0; i < nbPlayers; i++) {
            for (int j = 0; j < 3; j++) {
                List<Card> heartCards = pack.getCardsWithSuit(Suit.HEARTS);
                int x = random.nextInt(heartCards.size());
                Card randomCard = heartCards.get(x);
                randomCard.removeFromHand(false);
                hands[i].insert(randomCard, false);
            }
        }
        assert pack.getNumberOfCards() == 36 : " Pack without aces and hearts is not 36 cards.";
        // Give each player 9 of the remaining cards
        for (int i = 0; i < nbCardsPerPlayer; i++) {
            for (int j = 0; j < nbPlayers; j++) {
                assert !pack.isEmpty() : " Pack has prematurely run out of cards.";
                Card dealt = randomCard(pack);
                dealt.removeFromHand(false);
                hands[j].insert(dealt, false);
            }
        }
        for (int j = 0; j < nbPlayers; j++) {
            assert hands[j].getNumberOfCards() == 12 : " Hand does not have twelve cards.";
        }
    }
}
