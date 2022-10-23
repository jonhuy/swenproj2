package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;


public class SmartAI {

    private final int ATTACK_RANK_INDEX = 0;
    private final int DEFENCE_RANK_INDEX = 1;
    private final int PILE_NUM = 2;
    Random random = GameOfThrones.random;
    Card card ;
        Optional<Card> selectACard(List<Card> shortListCards, boolean isCharacter, Hand[] piles, int remainingTurn, int playerIndex, List<Optional<Card>> diamondsPlayed){
        int maxRank = 0;
        Optional<Card> returnedCard = Optional.empty();
        List<Card> availableCards = new ArrayList<>();
        int diamondsInHand = getDiamondsNum(shortListCards);
        if(isCharacter){
            //System.out.println("Case1");
            for(int i = 0; i< shortListCards.size();i++){
                int curRank = ((Rank) shortListCards.get(i).getRank()).getRankValue();
                if(curRank > maxRank){
                    maxRank = curRank;
                    returnedCard = Optional.ofNullable(shortListCards.get(i));
                }
            }
        }
        else if(remainingTurn == 1){
            //System.out.println("Case2");
            // smart Strategy condition 1
            int[] pile0Rank = calculatePileRanks(piles[0]);
            int[] pile1Rank = calculatePileRanks(piles[1]);


            int rankDifference0 = pile0Rank[ATTACK_RANK_INDEX] - pile1Rank[DEFENCE_RANK_INDEX];
            int rankDifference1 = pile1Rank[ATTACK_RANK_INDEX] - pile0Rank[DEFENCE_RANK_INDEX];
            if(playerIndex % 2 == 0 ){
                if(rankDifference0 <= 0){
                    returnedCard = findAttackCard(rankDifference0,shortListCards);
                }
                else if(rankDifference1> 0){
                    returnedCard = findDefenceCard(rankDifference1,shortListCards);
                }
            // check If player's team will lose a fight
                else{
                    returnedCard = isChangeResult(piles[(playerIndex % 2 + 1) % 2], shortListCards,piles[playerIndex % 2]);
                }
            }else if(playerIndex % 2 == 1){
                if(rankDifference1 <= 0){
                    returnedCard = findAttackCard(rankDifference1,shortListCards);
                }
                else if(rankDifference0 > 0){
                    returnedCard = findDefenceCard(rankDifference0,shortListCards);
                }
                else{
                    returnedCard = isChangeResult(piles[(playerIndex % 2 + 1) % 2],shortListCards, piles[playerIndex % 2]);
                }
            }
        }
        else if(diamondsPlayed == null || diamondsPlayed.size() != (12 - diamondsInHand)){
            //System.out.println("Case3");
            for(int i = 0;i < shortListCards.size();i++){
                if(diamondsPlayed == null){
                    availableCards = collectCards(shortListCards);
                }
                else{
                    for(int j = 0; j < diamondsPlayed.size();j++){
                        if (((Rank)shortListCards.get(i).getRank()).getRankValue() != ((Rank) diamondsPlayed.get(j).get().getRank()).getRankValue()){
                            availableCards.add(shortListCards.get(i));
                        }
                    }
                }

            }
            if(availableCards.size() != 0){
                returnedCard = Optional.ofNullable(availableCards.get(random.nextInt(availableCards.size())));
            }
        }
        return returnedCard;
    }

    int selectPile(Optional<Card> card,int playerIndex){
        int pileNum;
        if(card.get().getSuit() == Suit.DIAMONDS){
            pileNum = (playerIndex % 2 + 1) % 2;
        }
        else{
            pileNum = (playerIndex % 2);
        }
        return pileNum;
    }

    Optional<Card> findAttackCard(int rankDifference, List<Card> hand){
        for(int i = 0; i < hand.size();i++){
            if(((Rank) hand.get(i).getRank()).getRankValue() > rankDifference && (hand.get(i).getSuit() == Suit.CLUBS)){
                return Optional.ofNullable(hand.get(i));
            }
            else{
                return Optional.empty();
            }
        }
        return  Optional.empty();
    }

    Optional<Card> findDefenceCard(int rankDifference, List<Card> hand){
        for(int i = 0; i < hand.size();i++){
            if((((Rank) hand.get(i).getRank()).getRankValue() > rankDifference) && (hand.get(i).getSuit() == Suit.SPADES)){
                return Optional.ofNullable(hand.get(i));
            }
            else{
                return  Optional.empty();
            }
        }
        return  Optional.empty();
    }
    Optional<Card> isChangeResult(Hand oppoPile,List<Card> hand,Hand teamPile){
        Optional<Card> returnedCard =  Optional.empty();
        for(int i = 0; i < hand.size();i++){
            if((hand.get(i).getSuit()) == Suit.DIAMONDS && oppoPile.getCard(oppoPile.getNumberOfCards()) != null){
                Card lastCard = oppoPile.getCard(oppoPile.getNumberOfCards());
                //System.out.println(lastCard);
                Suit lastSuit = (Suit) lastCard.getSuit();
                int lastRank = ((Rank) lastCard.getRank()).getRankValue();
                int[] teamPileRanks = calculatePileRanks(teamPile);
                int[] oppoPileRanks = calculatePileRanks(oppoPile);
                int curRankValue = ((Rank) hand.get(i).getRank()).getRankValue();
                if(oppoPileRanks[ATTACK_RANK_INDEX] > teamPileRanks[DEFENCE_RANK_INDEX] && lastSuit == Suit.CLUBS) {
                    if((lastRank != curRankValue) && ((oppoPileRanks[ATTACK_RANK_INDEX] - curRankValue) < teamPileRanks[DEFENCE_RANK_INDEX])){
                        returnedCard = Optional.ofNullable(hand.get(i));
                        break;
                    }
                    else if((lastRank == curRankValue) && ((oppoPileRanks[ATTACK_RANK_INDEX] - curRankValue * 2) < teamPileRanks[DEFENCE_RANK_INDEX])){
                        returnedCard = Optional.ofNullable(hand.get(i));
                        break;
                    }
                }
                else if(oppoPileRanks[DEFENCE_RANK_INDEX] > teamPileRanks[ATTACK_RANK_INDEX] && lastSuit == Suit.SPADES){
                    if((lastRank != curRankValue) && ((oppoPileRanks[DEFENCE_RANK_INDEX] - curRankValue) < teamPileRanks[ATTACK_RANK_INDEX])){
                        returnedCard = Optional.ofNullable(hand.get(i));
                        break;
                    }
                    else if((lastRank != curRankValue) && ((oppoPileRanks[DEFENCE_RANK_INDEX] - curRankValue) < teamPileRanks[ATTACK_RANK_INDEX])){
                        returnedCard = Optional.ofNullable(hand.get(i));
                        break;
                    }
                }
            }
        }
        return returnedCard;
    }


    private int[] calculatePileRanks(Hand pile) {
        Hand currentPile = pile;
        int i = currentPile.isEmpty() ? 0 : ((Rank) currentPile.get(0).getRank()).getRankValue();
        return new int[] { i, i };
    }

    private int getDiamondsNum(List<Card> hand){
        int numDiamonds = 0;
        for(int i = 0; i < hand.size();i++){
            if(((Suit)hand.get(i).getSuit()).isMagic()){
                numDiamonds ++;
            }
        }
        return numDiamonds;
    }

    private List<Card> collectCards(List<Card> cardsInHand){
        List<Card> diamondsInHand = new ArrayList<>();
        List<Card> cardsCanPlay = new ArrayList<>();
        if(cardsInHand.isEmpty() == false){
            for(int i = 0; i< cardsInHand.size();i++){
                if(((Suit)cardsInHand.get(i).getSuit()).isMagic()){
                    diamondsInHand.add(cardsInHand.get(i));
                }
            }
            for(int i = 0; i< cardsInHand.size();i++){
                if(((Suit)cardsInHand.get(i).getSuit()).isMagic() == false && ((Suit)cardsInHand.get(i).getSuit()).isCharacter() == false){
                    if(diamondsInHand != null){
                        for(int j = 0; j < diamondsInHand.size();j++){
                            if(((Rank)cardsInHand.get(i).getRank()).getRankValue() == ((Rank) diamondsInHand.get(j).getRank()).getRankValue()){
                                cardsCanPlay.add(cardsInHand.get(i));
                            }
                        }
                    }
                    else{
                        cardsCanPlay.add(cardsInHand.get(i));
                    }
                }
            }
        }
        return cardsCanPlay;
    }
}
