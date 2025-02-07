package com.github.wizard;

import com.github.wizard.api.Card;
import com.github.wizard.api.Response;
import com.github.wizard.game.Player;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.tinylog.Logger;

public record Updater(StreamObserver<Response> responseStreamObserver) {

    public void update(Response response) {
        responseStreamObserver.onNext(response);
    }

    public static Response newOnTrickTakenResponse(Player player, int value) {
        return Response.newBuilder()
                .setType("1")
                .setData(
                        String.format(
                                "Player %s has made this trick with value %s",
                                player.getName(), value))
                .build();
    }

    public static Response newCardPlayRequestResponse() {
        return Response.newBuilder().setType("2").setData("Please play a card").build();
    }

    public static Response newOnGameBoardUpdate(List<Card> hand, List<Card> table) {
        if (hand == null) hand = new ArrayList<>();
        if (table == null) table = new ArrayList<>();

        String handCards =
                hand.stream()
                        .map(
                                card -> {
                                    if (card.getValue() == Card.Value.WIZARD
                                            || card.getValue() == Card.Value.JESTER) {
                                        return card.getValue().name();
                                    }
                                    return String.format(
                                            "%s(%s)",
                                            card.getColor().name(), card.getValue().getNumber());
                                })
                        .collect(Collectors.joining("/"));

        String tableCards =
                hand.stream()
                        .map(
                                card -> {
                                    if (card.getValue() == Card.Value.WIZARD
                                            || card.getValue() == Card.Value.JESTER) {
                                        return card.getValue().name();
                                    }
                                    return String.format(
                                            "%s(%s)",
                                            card.getColor().name(), card.getValue().getNumber());
                                })
                        .collect(Collectors.joining("/"));

        String cardsString = String.format("/%s//%s/", handCards, tableCards);

        Logger.info("sending out cards: {}", cardsString);

        return Response.newBuilder().setType("3").setData(cardsString).build();
    }

    public static Response newOnTrumpSelectedResponse(Card c) {
        return Response.newBuilder().setType("4").setData(c.getColor().name()).build();
    }

    public static Response newGetEstimateResponse() {
        return Response.newBuilder().setType("5").build();
    }

    public static Response newOnRoundFinishedResponse(int points, int round) {
        return Response.newBuilder().setType("6").setData(points + "/" + round).build();
    }
}
