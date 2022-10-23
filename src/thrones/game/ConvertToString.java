package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.stream.Collectors;

public class ConvertToString {
    String ToString(Suit s) { return s.toString().substring(0, 1); }

    String ToString(Rank r) {
        switch (r) {
            case ACE: case KING: case QUEEN: case JACK: case TEN:
                return r.toString().substring(0, 1);
            default:
                return String.valueOf(r.getRankValue());
        }
    }

    String ToString(Card c) { return ToString((Rank) c.getRank()) + ToString((Suit) c.getSuit()); }

    String ToString(Hand h) {
        return "[" + h.getCardList().stream().map(this::ToString).collect(Collectors.joining(",")) + "]";
    }
}
