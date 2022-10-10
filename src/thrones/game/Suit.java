package thrones.game;


public enum Suit {
    SPADES(GameOfThrones.GoTSuit.DEFENCE),
    HEARTS(GameOfThrones.GoTSuit.CHARACTER),
    DIAMONDS(GameOfThrones.GoTSuit.MAGIC),
    CLUBS(GameOfThrones.GoTSuit.ATTACK);
    Suit(GameOfThrones.GoTSuit gotsuit) {
        this.gotsuit = gotsuit;
    }
    private final GameOfThrones.GoTSuit gotsuit;

    public boolean isDefence(){ return gotsuit == GameOfThrones.GoTSuit.DEFENCE; }

    public boolean isAttack(){ return gotsuit == GameOfThrones.GoTSuit.ATTACK; }

    public boolean isCharacter(){ return gotsuit == GameOfThrones.GoTSuit.CHARACTER; }

    public boolean isMagic(){ return gotsuit == GameOfThrones.GoTSuit.MAGIC; }
}
