package thrones.game;

import ch.aplu.jcardgame.Card;

import java.util.List;
import java.util.Optional;

public interface AIPlayers {
    Optional<Card> selectACard(List<Card> shortListCards, boolean isCharacter);
    int selectPile();
    int selectPile(Card card,int playerIndex);
    int selectPile(Card card,int playerIndex,int remainingTurn,int[] pile0, int[] pile1);
}
