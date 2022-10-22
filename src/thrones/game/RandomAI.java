package thrones.game;

import ch.aplu.jcardgame.Card;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RandomAI {
    Random random = GameOfThrones.random;
    Optional<Card> selectACard(List<Card> shortListCards, boolean isCharacter){
        return Optional.of(shortListCards.get(random.nextInt(shortListCards.size())));
    }

    int selectPile(){
        return random.nextInt(2);
    };
}
