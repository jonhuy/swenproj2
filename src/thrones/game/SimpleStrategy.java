package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
public class SimpleStrategy {
    static Random random;
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

    int selectPile(Card card,int playerIndex){
        if(((Suit) card.getSuit()).isAttack() || ((Suit) card.getSuit()).isDefence()){
            return playerIndex % 2;
        }
        else{
            return (playerIndex % 2 + 1) % 2;
        }
    }
}
