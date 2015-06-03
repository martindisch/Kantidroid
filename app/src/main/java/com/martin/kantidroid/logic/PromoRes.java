package com.martin.kantidroid.logic;

/**
 * Represents the user's current situation
 * 
 * @author Martin
 * 
 */
public class PromoRes {
	public String sMessage;
	public int iColor;
	public String sPP;
	public String sSchnitt;
	public String sKont;

	/**
	 * The constructor
	 * 
	 * @param sMessage
	 *            Whether user is 'promoviert' or not
	 * @param iColor
	 *            The color matching the result
	 * @param sPP
	 *            The number of Pluspunkte
	 * @param sSchnitt
	 *            The user's average marks
	 */
	public PromoRes(String sMessage, int iColor, String sPP, String sSchnitt, String sKont) {
		super();
		this.sMessage = sMessage;
		this.iColor = iColor;
		this.sPP = sPP;
		this.sSchnitt = sSchnitt;
		this.sKont = sKont;
	}
}