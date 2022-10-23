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



    public Optional<Card> pickACorrectSuit(int playerIndex, boolean isCharacter, Hand currentHand, String playerType, Hand[] piles, int remainingTurn, List<Optional<Card>> diamondsPlayed) {
        Optional<Card> selected;
        //Hand currentHand = hands[playerIndex];

        List<Card> shortListCards = new ArrayList<>();
        for (int i = 0; i < currentHand.getCardList().size(); i++) {
            Card card = currentHand.getCardList().get(i);
            Suit suit = (Suit) card.getSuit();
            if (suit.isCharacter() == isCharacter) {
                //System.out.println("card added");
                shortListCards.add(card);
            }
        }
        if (shortListCards.isEmpty() || !isCharacter && random.nextInt(3) == 0){
            selected = Optional.empty();
        } else {
//            selected = Optional.of(shortListCards.get(random.nextInt(shortListCards.size())));
            if(playerType.equals("random")){
                System.out.println("PROBLEM WITH RANDOM PLAYER");
                RandomAI randomAI = new RandomAI();
                selected = randomAI.selectACard(shortListCards,isCharacter);
            }
            else if(playerType.equals("simple")){
                System.out.println("PROBLEM WITH SIMPLE PLAYER");
                SimpleAI simpleAI = new SimpleAI();
                selected = simpleAI.selectACard(shortListCards,isCharacter);
            }
            else{
                System.out.println("PROBLEM WITH SMART PLAYER");
                SmartAI smartAI = new SmartAI();
                selected = smartAI.selectACard(shortListCards,  isCharacter, piles, remainingTurn, playerIndex,diamondsPlayed);
            }
            //selected = select based on players' type
            //if pile == heart and selected == diamond : isPresent = false
            //else : not null

        }
        System.out.println("in player class");
        System.out.println(selected);
        return selected;
    }

    public int selectPile(int playerIndex,Optional<Card> card,String playerType){
        int pileNum;
        if(((Suit) card.get().getSuit()).isCharacter()){
            pileNum = playerIndex % 2;
        }
        else{
            if(playerType.equals("random")){
                RandomAI randomAI = new RandomAI();
                pileNum = randomAI.selectPile();
            }
            else if(playerType.equals("simple")){
                SimpleAI simpleAI = new SimpleAI();
                pileNum = simpleAI.selectPile(card,playerIndex);
            }
            else{
                SmartAI smartAI = new SmartAI();
                pileNum = smartAI.selectPile(card,playerIndex);
            }
        }
        return pileNum;
    }

}