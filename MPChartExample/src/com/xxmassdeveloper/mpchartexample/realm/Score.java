package com.xxmassdeveloper.mpchartexample.realm;


import io.realm.RealmObject;

/**
 * our data object
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Score extends RealmObject {

    public float totalScore;

    public float scoreNr;

    public String playerName;

    public Score() {}

    public Score(float totalScore, float scoreNr, String playerName) {
        this.scoreNr = scoreNr;
        this.playerName = playerName;
        this.totalScore = totalScore;
    }

}
