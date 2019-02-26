package state;


public class StateHandler {

    public static StateProto.State TestState() {
        return StateProto.State.newBuilder().setDeck(
                StateProto.Deck.newBuilder().setSize(10).build())
                .setNumRelics(20).build();

    }

}
