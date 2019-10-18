package gg.manny.brawl.util;

import java.util.HashMap;
import java.util.UUID;

public class EloRating {

    public EloRating() {
    }

    /**
     * Calculate ELO rating for multiplayer
     * 
     * Formula used to calculate rating for Player1 
     * NewRatingP1 = RatingP1 + K * (S - EP1)
     * 
     * Where: 
     * RatingP1 = current rating for Player1 
     * K = K-factor 
     * S = actualScore (1 win, 0 lose) 
     * EP1 = Q1 / Q1 + Q2 + Q3 
     * Q(i) = 10 ^ (RatingP(i)/400)
     * 
     * @param usersRating
     *            A HashMap<UniqueId, Rating> keeping users id and current rating
     * @param uniqueIdWinner
     *            The userId of the winner
     * @return A HashMap<UserId, Rating> with new rating
     */
    public HashMap<UUID, Integer> calculateMultiplayer(HashMap<UUID, Integer> usersRating, UUID uniqueIdWinner) {
        if (usersRating.size() == 0) return usersRating;

        HashMap<UUID, Integer> newUsersPoints = new HashMap<UUID, Integer>();

        // K-factor
        int K = 32;

        // Calculate total Q
        double Q = 0.0;
        for (UUID uniqueId : usersRating.keySet()) {
            Q += Math.pow(10.0, ((double) usersRating.get(uniqueId) / 400));
        }

        // Calculate new rating
        for (UUID uniqueId : usersRating.keySet()) {

            /**
             * Expected rating for an user
             * E = Q(i) / Q(total)
             * Q(i) = 10 ^ (R(i)/400)
             */
            double expected = (double) Math.pow(10.0, ((double) usersRating.get(uniqueId) / 400)) / Q;
                        
            /**
             * Actual score is
             * 1 - if player is winner
             * 0 - if player losses
             * (another option is to give fractions of 1/number-of-players instead of 0)
             */
            int actualScore;
            if (uniqueIdWinner.equals(uniqueId)) {
                actualScore = 1;
            } else {
                actualScore = 0;
            }

            // new rating = R1 + K * (S - E);
            int newRating = (int) Math.round(usersRating.get(uniqueId) + K * (actualScore - expected));

            // Add to HashMap
            newUsersPoints.put(uniqueId, newRating);
            
        }

        return newUsersPoints;
    }

    /**
     * Calculate rating for 2 players
     * 
     * @param player1Rating
     *            The rating of Player1
     * @param player2Rating
     *            The rating of Player2
     * @param outcome
     *            A string representing the game result for Player1 
     *            "+" winner
     *            "=" draw 
     *            "-" lose
     * @return New player rating
     */
    public int calculate2PlayersRating(int player1Rating, int player2Rating, EloOutcomeType outcome) {

        double actualScore;

        switch(outcome) {
		case DRAW:
			actualScore = 0.5;
			break;
		case LOSER:
			actualScore = 0;
			break;
		case WINNER:
            actualScore = 1.0;
			break;
		default:
			return player1Rating;
        }

        // calculate expected outcome
        double exponent = (double) (player2Rating - player1Rating) / 400;
        double expectedOutcome = (1 / (1 + (Math.pow(10, exponent))));

        // K-factor
        int K = determineK(player1Rating);

        // calculate new rating
        int newRating = (int) Math.round(player1Rating + K * (actualScore - expectedOutcome));

        return newRating;
    }

    /**
     * Determine the rating constant K-factor based on current rating
     * 
     * @param rating
     *            Player rating
     * @return K-factor
     */
    public int determineK(int rating) {
        int K;
        if (rating < 2000) {
            K = 32;
        } else if (rating >= 2000 && rating < 2400) {
            K = 24;
        } else {
            K = 16;
        }
        return K;
    }

    public enum EloOutcomeType {
    	
    	WINNER,
    	DRAW,
    	LOSER;
    	
    }
}