package thrones.game;

import ch.aplu.jcardgame.Card;

import java.util.List;
import java.util.Optional;
import java.util.Random;
public class SimpleAI {
    Random random = GameOfThrones.random;
    Card card ;

    Optional<Card> selectACard(List<Card> shortListCards, boolean isCharacter){
        int maxRank = 0;
        Card maxCard = null;
        if(isCharacter){
            for(int i = 0; i< shortListCards.size();i++){
                int curRank = ((Rank) shortListCards.get(i).getRank()).getRankValue();
                if(curRank > maxRank){
                    maxRank = curRank;
                    maxCard = shortListCards.get(i);
                }
            }
            return Optional.ofNullable(maxCard);
        }
        else{
            return Optional.of(shortListCards.get(random.nextInt(shortListCards.size())));
        }
    }

    int selectPile(Optional<Card> card,int playerIndex){
        if(((Suit) card.get().getSuit()).isAttack() || ((Suit) card.get().getSuit()).isDefence()){
            return playerIndex % 2;
        }
        else{
            return (playerIndex % 2 + 1) % 2;
        }
    }
}
