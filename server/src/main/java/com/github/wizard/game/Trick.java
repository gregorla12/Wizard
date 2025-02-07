package com.github.wizard.game;

import com.github.wizard.Server;
import com.github.wizard.api.Card;
import java.util.ArrayList;
import java.util.List;

public class Trick {

    private final List<Card> cards = new ArrayList<>(Server.MAX_PLAYERS);
    private final List<Player> players = new ArrayList<>(Server.MAX_PLAYERS);

    Card trump;

    public void reset() {
        getCards().clear();
        players.clear();
    }

    public Trick(Card trump) {
        this.trump = trump;
    }

    public void playCard(Card card, Player player) {
        if (getCards().size() >= Server.MAX_PLAYERS)
            throw new IllegalArgumentException("cannot play more cards than there are players");

        getCards().add(card);
        players.add(player);
    }

    public int getCardsPlayed() {
        return getCards().size();
    }
    /**
     * should return the value for a given Trick
     *
     * @return
     */
    public int getValue() {
        Card.Color firstColor =
                getCards().get(0).getColor(); // as only cards of similar color and wizards count

        return getCards().stream()
                .map(
                        card -> {
                            if (card.getColor() != firstColor
                                    || card.getValue() == Card.Value.WIZARD
                                    || card.getValue() == Card.Value.JESTER) {
                                return 0;
                            }
                            return card.getValue().getNumber();
                        })
                .reduce(Integer::sum)
                .orElse(-1);
    }

    public Player getWinningPlayer() { // todo do not ignore trump
        Card.Color firstColor =
                getCards().get(0).getColor(); // as only cards of similar color and wizards count

        int highestValueIndex = 0;

        for (int i = 0; i < getCards().size(); i++) {
            Card card = getCards().get(i);

            // First wizard wins
            if (card.getValue() == Card.Value.WIZARD) return players.get(i);

            // Jester is always ignored
            if (card.getValue() == Card.Value.JESTER) continue;

            if (i >= 1) {
                // new color is trump, old was not
                if (card.getColor() == trump.getColor()
                        && getCards().get(highestValueIndex).getColor() != trump.getColor())
                    highestValueIndex = i;
                else if (card.getColor() == firstColor
                        && card.getValue().getNumber()
                                > getCards().get(highestValueIndex).getValue().getNumber())
                    highestValueIndex = i;
            }
        }

        return players.get(highestValueIndex);
    }

    public List<Card> getCards() {
        return cards;
    }
}
