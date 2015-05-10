package com.martin.noten;

/**
 * Represents the user's current situation
 *
 * @author Martin
 */
public class PromoRes {
    public final String sMessage;
    public final int iColor;
    public final String sPP;
    public final String sSchnitt;

    /**
     * The constructor
     *
     * @param sMessage Whether user is 'promoviert' or not
     * @param iColor   The color matching the result
     * @param sPP      The number of Pluspunkte
     * @param sSchnitt The user's average marks
     */
    public PromoRes(String sMessage, int iColor, String sPP, String sSchnitt) {
        super();
        this.sMessage = sMessage;
        this.iColor = iColor;
        this.sPP = sPP;
        this.sSchnitt = sSchnitt;
    }
}