package state;


public class StateHandler {

    public static StateProtos.State TestState() {
        return StateProtos.State.newBuilder().setDeck(
                StateProtos.Deck.newBuilder().setSize(10).build())
                .setNumRelics(20).build();

    }

}
