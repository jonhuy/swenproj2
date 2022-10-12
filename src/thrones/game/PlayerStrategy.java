package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.List;
import java.util.Optional;

public interface PlayerStrategy {
    Optional<Card> selectACard(List<Card> shortListCards, boolean isCharacter);
    int selectPile();
    int selectPile(Card card,int playerIndex);
}
