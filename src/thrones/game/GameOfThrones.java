package thrones.game;


// Oh_Heaven.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.awt.Color;
import java.awt.Font;
import java.util.*;
import java.util.stream.Collectors;


@SuppressWarnings("serial")
public class GameOfThrones extends CardGame {

    enum GoTSuit { CHARACTER, DEFENCE, ATTACK, MAGIC }

    //new class for string representation for the cards
    private ConvertToString cardToString = new ConvertToString();
    static public int seed;
    static Random random;
    private final String version = "1.0";
    public final int nbPlayers = 4;
    public final int nbStartCards = 9;
    public final int nbPlays = 6;
    public final int nbRounds = 3;
    private final int handWidth = 400;
    private final int pileWidth = 40;
    private Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private final Location[] handLocations = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };

    private final Location[] scoreLocations = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(25, 25),
            new Location(575, 125)
    };
    private final Location[] pileLocations = {
            new Location(350, 280),
            new Location(350, 430)
    };
    private final Location[] pileStatusLocations = {
            new Location(250, 200),
            new Location(250, 520)
    };

    private Actor[] pileTextActors = { null, null };
    private Actor[] scoreActors = {null, null, null, null};
    public static int watchingTime ;
    private Hand[] hands;
    private Hand[] piles;
    private List<Optional<Card>> playedDiamonds = new ArrayList<>();
    private final String[] playerTeams = { "[Players 0 & 2]", "[Players 1 & 3]"};
    private int nextStartingPlayer = random.nextInt(nbPlayers);

    private int[] scores = new int[nbPlayers];

    Font bigFont = new Font("Arial", Font.BOLD, 36);
    Font smallFont = new Font("Arial", Font.PLAIN, 10);




    private void initScore() {
        for (int i = 0; i < nbPlayers; i++) {
            scores[i] = 0;
            String text = "P" + i + "-0";
            scoreActors[i] = new TextActor(text, Color.WHITE, bgColor, bigFont);
            addActor(scoreActors[i], scoreLocations[i]);
        }

        String text = "Attack: 0 - Defence: 0";
        for (int i = 0; i < pileTextActors.length; i++) {
            pileTextActors[i] = new TextActor(text, Color.WHITE, bgColor, smallFont);
            addActor(pileTextActors[i], pileStatusLocations[i]);
        }
    }

    private void updateScore(int player) {
        removeActor(scoreActors[player]);
        String text = "P" + player + "-" + scores[player];
        scoreActors[player] = new TextActor(text, Color.WHITE, bgColor, bigFont);
        addActor(scoreActors[player], scoreLocations[player]);
    }

    private void updateScores() {
        for (int i = 0; i < nbPlayers; i++) {
            updateScore(i);
        }
        System.out.println(playerTeams[0] + " score = " + scores[0] + "; " + playerTeams[1] + " score = " + scores[1]);
    }

    private Optional<Card> selected;
    private final int NON_SELECTION_VALUE = -1;
    private int selectedPileIndex = NON_SELECTION_VALUE;
    private final int UNDEFINED_INDEX = -1;
    private final int ATTACK_RANK_INDEX = 0;
    private final int DEFENCE_RANK_INDEX = 1;
    private final int PILES_SIZE = 2;
    private static String[] playerTypes = new String[5];
    private Dealer dealer = new Dealer();
    private void setupGame() {
        hands = new Hand[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            hands[i] = new Hand(deck);
        }
        //dealingOut( hands, nbPlayers, nbStartCards);
        dealer.dealingOut(deck, hands, nbPlayers, nbStartCards,seed);


        for (int i = 0; i < nbPlayers; i++) {
            hands[i].sort(Hand.SortType.SUITPRIORITY, true);
            System.out.println("hands[" + i + "]: " + cardToString.ToString(hands[i]));
        }

        for (final Hand currentHand : hands) {
            // Set up human player for interaction
            currentHand.addCardListener(new CardAdapter() {
                public void leftDoubleClicked(Card card) {
                    selected = Optional.of(card);
                    currentHand.setTouchEnabled(false);
                }
                public void rightClicked(Card card) {
                    selected = Optional.empty(); // Don't care which card we right-clicked for player to pass
                    currentHand.setTouchEnabled(false);
                }
            });
        }
        // graphics
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            layouts[i] = new RowLayout(handLocations[i], handWidth);
            layouts[i].setRotationAngle(90 * i);
            hands[i].setView(this, layouts[i]);
            hands[i].draw();
        }
        // End graphics
    }

    private void resetPile() {
        if (piles != null) {
            for (Hand pile : piles) {
                pile.removeAll(true);
            }
        }
        piles = new Hand[PILES_SIZE];
        for (int i = 0; i < PILES_SIZE; i++) {
            piles[i] = new Hand(deck);
            piles[i].setView(this, new RowLayout(pileLocations[i], 8 * pileWidth));
            piles[i].draw();
            final Hand currentPile = piles[i];
            final int pileIndex = i;
            piles[i].addCardListener(new CardAdapter() {
                public void leftClicked(Card card) {
                    selectedPileIndex = pileIndex;
                    currentPile.setTouchEnabled(false);
                }
            });
        }

        updatePileRanks();
    }

    private void waitForCorrectSuit(int playerIndex, boolean isCharacter) {
        if (hands[playerIndex].isEmpty()) {
            selected = Optional.empty();
        } else {
            selected = null;
            hands[playerIndex].setTouchEnabled(true);
            do {
                //System.out.println("inside do loop");
                if (selected == null) {
                    delay(100);
                    continue;
                }
                Suit suit = selected.isPresent() ? (Suit) selected.get().getSuit() : null;
                if (isCharacter && suit != null && suit.isCharacter() ||         // If we want character, can't pass and suit must be right
                        !isCharacter && (suit == null || !suit.isCharacter())) { // If we don't want character, can pass or suit must not be character
                    // if (suit != null && suit.isCharacter() == isCharacter) {
                    System.out.println(selected);
                    break;
                } else {
                    selected = null;
                    hands[playerIndex].setTouchEnabled(true);
                }
                delay(100);
            } while (true);
        }
    }

    private int waitForPileSelection() {
        selectedPileIndex = NON_SELECTION_VALUE;
        for (Hand pile : piles) {
            pile.setTouchEnabled(true);
        }
        while(selectedPileIndex == NON_SELECTION_VALUE) {
            delay(100);
        }
        for (Hand pile : piles) {
            pile.setTouchEnabled(false);
        }
        return selectedPileIndex;
    }

    private int[] calculatePileRanks(int pileIndex) {
        Hand currentPile = piles[pileIndex];

        int attackPower = 0;
        int defencePower = 0;
        int baseLevel = 0;
        int i = 1;
        if(!currentPile.isEmpty()){
            baseLevel = ((Rank) currentPile.get(0).getRank()).getRankValue();
            attackPower = baseLevel;
            defencePower = baseLevel;
            for (; i < currentPile.getNumberOfCards(); i++) {
                Card previous = currentPile.get(i-1);
                Card card =  currentPile.get(i);
                Suit suit = (Suit) card.getSuit();
                if (suit.isDefence()) {
                    if(card.getRank() == previous.getRank()){
                        defencePower += 2*(((Rank) currentPile.get(i).getRank()).getRankValue());
                    }else{
                        defencePower += ((Rank) currentPile.get(i).getRank()).getRankValue();
                    }

                } else if (suit.isAttack()) {
                    if(card.getRank() == previous.getRank()){
                        attackPower += 2*(((Rank) currentPile.get(i).getRank()).getRankValue());
                    }else{
                        attackPower += ((Rank) currentPile.get(i).getRank()).getRankValue();
                    }

                } else if (suit.isMagic()) {
                    if(card.getRank() == previous.getRank()){
                        attackPower -= 2*(((Rank) currentPile.get(i).getRank()).getRankValue());
                        defencePower -= 2*(((Rank) currentPile.get(i).getRank()).getRankValue());
                    }else {
                        attackPower -= ((Rank) currentPile.get(i).getRank()).getRankValue();
                        defencePower -= ((Rank) currentPile.get(i).getRank()).getRankValue();
                    }
                    if(attackPower < 0){
                        attackPower = 0;
                    }
                    if(defencePower < 0){
                        defencePower = 0;
                    }
                }
            }
        }
        return new int[] { attackPower, defencePower };
    }

    protected void updatePileRankState(int pileIndex, int attackRank, int defenceRank) {
        TextActor currentPile = (TextActor) pileTextActors[pileIndex];
        removeActor(currentPile);
        String text = playerTeams[pileIndex] + " Attack: " + attackRank + " - Defence: " + defenceRank;
        pileTextActors[pileIndex] = new TextActor(text, Color.WHITE, bgColor, smallFont);
        addActor(pileTextActors[pileIndex], pileStatusLocations[pileIndex]);
    }

    private void updatePileRanks() {
        for (int j = 0; j < PILES_SIZE; j++) {
            int[] ranks = calculatePileRanks(j);
            updatePileRankState(j, ranks[ATTACK_RANK_INDEX], ranks[DEFENCE_RANK_INDEX]);
        }
    }

    private boolean isSelectedCharacterCard(Optional<Card> selected){
        return (((Suit)selected.get().getSuit()).isCharacter() || selected.isEmpty());
    }

    private int getPlayerIndex(int index) {
        return index % nbPlayers;
    }
    private Player player = new Player();
    private void executeAPlay() {
        resetPile();
        selected = Optional.empty();
        nextStartingPlayer = getPlayerIndex(nextStartingPlayer);
        if (hands[nextStartingPlayer].getNumberOfCardsWithSuit(Suit.HEARTS) == 0)
            nextStartingPlayer = getPlayerIndex(nextStartingPlayer + 1);
        assert hands[nextStartingPlayer].getNumberOfCardsWithSuit(Suit.HEARTS) != 0 : " Starting player has no hearts.";
        int remainingTurns = nbPlayers * nbRounds;
        int nextPlayer = nextStartingPlayer;
        for (int i=0;i<remainingTurns;i++){
            nextPlayer = getPlayerIndex(nextPlayer);
            int pileIndex = 0;
            boolean wantCharacterCard = false;

        // 1: play the first 2 hearts

            if (i == 0 || i == 1){
                setStatusText("Player " + nextPlayer + " select a Heart card to play");
                wantCharacterCard = true;
                pileIndex = nextPlayer % 2;
                // 2: play the remaining turns
            } else{
                setStatusText("Player" + nextPlayer + " select a non-Heart card to play.");

            }

            boolean RightCard = false;
            while (!RightCard){
                try{
                    selectSuit(nextPlayer,wantCharacterCard, remainingTurns - i);
                    if (selected.isPresent()){
                        Boolean isCharacterCard = isSelectedCharacterCard(selected);
                        if (isCharacterCard != wantCharacterCard){
                            if (wantCharacterCard){
                                throw new BrokeRuleException("Selected a non-Heart card to play.");
                            } else {
                                throw new BrokeRuleException("Selected a Heart card to play.");
                            }
                        } else {
                            RightCard = true;
                        }
                    } else {
                        RightCard = true;
                    }
                } catch (BrokeRuleException exception){
                    setStatusText(exception.getMessage());
                }
            }


            // Pick pile for selected card
            if (selected.isPresent()){
                if (!wantCharacterCard){
                    setStatusText("Selected: " + cardToString.ToString(selected.get()) + ". Player" + nextPlayer + " select a pile to play the card.");
                    //boolean validPile = false;
                    boolean isSelectedMagic = ((Suit) selected.get().getSuit()).isMagic();
                    //int pileTries = 0;
                    try {
                        selectPile(nextPlayer);
                        if (isSelectedMagic){
                            Suit lastCardSuit = (Suit) piles[selectedPileIndex].getLast().getSuit();
                                if (lastCardSuit.isCharacter()){
                                    throw new BrokeRuleException("You cannot play a Diamond card on a Heart card.");
                                }
                        }
                    } catch (BrokeRuleException exception){
                        setStatusText(exception.getMessage());
                        selected = Optional.empty();
                    }
                    pileIndex = selectedPileIndex;
                }
                if (selected.isPresent()){
                    if (((Suit) selected.get().getSuit()).isMagic()){
                        playedDiamonds.add(selected);
                    }
                    System.out.println("Player " + nextPlayer + " plays " + cardToString.ToString(selected.get()) + " on pile " + pileIndex);
                    selected.get().setVerso(false);
                    selected.get().transfer(piles[pileIndex], true); // transfer to pile (includes graphic effect)
                    updatePileRanks();
                }
            }

            if (!selected.isPresent()){
                if (wantCharacterCard){
                    System.out.println("Pass returned on selection of character.");
                } else{
                    System.out.println("Pass.");
                }
            }
            nextPlayer ++;
        }

        // 3: calculate winning & update scores for players
        updatePileRanks();
        int[][] pileRanks = new int[PILES_SIZE + 1][3];
        for (int j=0;j<PILES_SIZE;j++){
            calculatePileRanks(j);
            pileRanks[j] = printRanks(piles[j], j);
        }

        Rank pile0CharacterRank = (Rank) piles[0].getCardList().get(0).getRank();
        Rank pile1CharacterRank = (Rank) piles[1].getCardList().get(0).getRank();
        String character0Result;
        String character1Result;


        if (pileRanks[0][ATTACK_RANK_INDEX] > pileRanks[1][DEFENCE_RANK_INDEX]){

            scores[0] += pile1CharacterRank.getRankValue();
            scores[2] += pile1CharacterRank.getRankValue();
            character0Result = "Character 0 attack on character 1 succeeded.";
        } else {
            scores[1] += pile1CharacterRank.getRankValue();
            scores[3] += pile1CharacterRank.getRankValue();
            character0Result = "Character 0 attack on character 1 failed.";
        }

        if (pileRanks[1][ATTACK_RANK_INDEX] > pileRanks[0][DEFENCE_RANK_INDEX]) {
            scores[1] += pile0CharacterRank.getRankValue();
            scores[3] += pile0CharacterRank.getRankValue();
            character1Result = "Character 1 attack on character 0 succeeded.";
        } else {
            scores[0] += pile0CharacterRank.getRankValue();
            scores[2] += pile0CharacterRank.getRankValue();
            character1Result = "Character 1 attack character 0 failed.";
        }
        updateScores();
        System.out.println(character0Result);
        System.out.println(character1Result);
        setStatusText(character0Result + " " + character1Result);

        // 5: discarded all cards on the piles
        nextStartingPlayer += 1;
        delay(watchingTime);
    }

    public int[] printRanks(Hand pile, int i){
        System.out.println("piles["+ i + "]: " + cardToString.ToString(pile));
        int[] pileRanks = calculatePileRanks(i);
        System.out.println("piles["+ i + "] is " + "Attack: " + pileRanks[ATTACK_RANK_INDEX] + " - Defence: " + pileRanks[DEFENCE_RANK_INDEX]);
        return pileRanks;
    }

    private Boolean isSelectedCharacterCard(){
        return ((Suit) selected.get().getSuit()).isCharacter();
    }

    /* Added a separate function to reduce bloating in the function */
    // LOOK HERE PLAYER TYPES

    private void selectPile(int playerNo){
        if (playerTypes[playerNo].equals("human")) {
            waitForPileSelection();
        } else {
            selectedPileIndex = player.selectPile(playerNo, selected,playerTypes[playerNo]);
        }
    }



    /* CHANGED START */
    /* Added a separate function to reduce repeated code */
    // LOOK HERE PLAYER TYPES

    private void selectSuit(int playerNo, boolean characterCard, int remainingTurns){
        if(playerTypes[playerNo].equals("human")){
            waitForCorrectSuit(playerNo,characterCard);
        }
        else{
            Optional<Card> tempSelected = player.pickACorrectSuit(playerNo, characterCard, hands[playerNo],playerTypes[playerNo],piles,remainingTurns,playedDiamonds);
            if (tempSelected == selected){
                selected = Optional.empty();
            } else {
                selected = tempSelected;
            }
        }
    }


    public GameOfThrones() {
        super(700, 700, 30);

        setTitle("Game of Thrones (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        initScore();

        setupGame();
        for (int i = 0; i < nbPlays; i++) {
            executeAPlay();
            updateScores();
        }

        String text;
        if (scores[0] > scores[1]) {
            text = "Players 0 and 2 won.";
        } else if (scores[0] == scores[1]) {
            text = "All players drew.";
        } else {
            text = "Players 1 and 3 won.";
        }
        System.out.println("Result: " + text);
        setStatusText(text);

        refresh();
    }

    public static void main(String[] args) {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        final Properties properties;
        //properties.setProperty("watchingTime", "5000");
        //System.out.println(args.length);

        if (args == null || args.length == 0) {
            properties = PropertiesLoader.loadPropertiesFile("got.properties");
        } else {
            properties = PropertiesLoader.loadPropertiesFile(args[0]);
        }
        //System.out.println(properties);
        String seedProp = properties.getProperty("seed");  //Seed property
        if (seedProp != null) { // Use property seed
            GameOfThrones.seed = Integer.parseInt(seedProp);
        } else { // and no property
            GameOfThrones.seed = 130006; // so randomise
        }

        String timeProp = properties.getProperty("watchingTime");  //watchingTime property
        if (timeProp != null) { // Use property watchingTime
            watchingTime = Integer.parseInt(timeProp);
        } else { // and no property
            watchingTime = 5000; // so randomise
        }


        for (int i=0; i<4; i++){
            String playerKey = "players." + i;
            String playersProp = properties.getProperty(playerKey);
            if (playersProp != null){
                playerTypes[i] = playersProp;
            } else {
                playerTypes[i] = "random";
            }
        }



        System.out.println("Seed = " + seed);
        GameOfThrones.random = new Random(seed);
        new GameOfThrones();
    }

}