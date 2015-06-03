package com.martin.kantidroid.logic;

import java.util.List;
import java.util.Locale;

import android.content.Context;

import com.martin.kantidroid.R;

public class PromoCheck {
	private Context context;

	public PromoCheck(Context context) {
		super();
		this.context = context;
	}

	public PromoRes getGym(int iSemester) {
		Fach entry;
		double plus = 0;
		double minus = 0;
		int total_minus = 0;
		double schn = 0;
		double total = 0;
		int fcount = 0;
		int mppcount = 0;

		String sMessage = "Promoviert";
		int iColor = R.color.promo_white;
		String sPP = "0.0";
		String sSchnitt = "5.0000";

		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getFachCount();
		List<Fach> faecher = db.getAllFaecher(context, iSemester);
		for (int i = 0; i < count; i++) {
			entry = faecher.get(i);
			if (entry.getPromotionsrelevant().contentEquals("true")) {

				switch (iSemester) {
				case 1:
					if (!entry.getRealAverage1().contentEquals("")) {
						if (Double.parseDouble(entry.getRealAverage1()) > 4) {
							plus += (Double.parseDouble(entry.getRealAverage1()) - 4);
						}
						if (Double.parseDouble(entry.getRealAverage1()) < 4) {
							minus += (4 - Double.parseDouble(entry.getRealAverage1()));
							total_minus++;
						}
						total += Double.parseDouble(entry.getMathAverage1());
						fcount++;
					}
					break;
				case 2:
					if (!entry.getRealAverage2().contentEquals("")) {
						if (Double.parseDouble(entry.getRealAverage2()) > 4) {
							plus += Double.parseDouble(entry.getRealAverage2()) - 4;
						}
						if (Double.parseDouble(entry.getRealAverage2()) < 4) {
							minus += 4 - Double.parseDouble(entry.getRealAverage2());
							total_minus++;
						}
						total += Double.parseDouble(entry.getMathAverage2());
						fcount++;
					}
					break;
				case 3:
					// Falls beide Semester ausgefüllt
					if (!entry.getRealAverage1().contentEquals("") && !entry.getRealAverage2().contentEquals("")) {
						schn = (Double.parseDouble(entry.getRealAverage1()) + Double.parseDouble(entry.getRealAverage2())) / 2;
						if (schn > 4) {
							plus += schn - 4;
						}
						if (schn < 4) {
							minus += 4 - schn;
							total_minus++;
						}
						total += (Double.parseDouble(entry.getMathAverage1()) + Double.parseDouble(entry.getMathAverage2())) / 2;
						fcount++;
					}

					// Falls nur erstes Semester ausgefüllt
					if (entry.getRealAverage2().contentEquals("") && !entry.getRealAverage1().contentEquals("")) {
						if (Double.parseDouble(entry.getRealAverage1()) > 4) {
							plus += (Double.parseDouble(entry.getRealAverage1()) - 4);
						}
						if (Double.parseDouble(entry.getRealAverage1()) < 4) {
							minus += (4 - Double.parseDouble(entry.getRealAverage1()));
							total_minus++;
						}
						total += Double.parseDouble(entry.getMathAverage1());
						fcount++;
					}

					// Falls nur zweites Semester ausgefüllt
					if (!entry.getRealAverage2().contentEquals("") && entry.getRealAverage1().contentEquals("")) {
						if (Double.parseDouble(entry.getRealAverage2()) > 4) {
							plus += Double.parseDouble(entry.getRealAverage2()) - 4;
						}
						if (Double.parseDouble(entry.getRealAverage2()) < 4) {
							minus += 4 - Double.parseDouble(entry.getRealAverage2());
							total_minus++;
						}
						total += Double.parseDouble(entry.getMathAverage2());
						fcount++;
					}
					break;
				}

				mppcount++;
			}
		}
		if (!((minus * 2) > plus)) {
			if (total_minus <= 3) {
				sMessage = "Promoviert";
				iColor = R.color.promo_white;
			}
			if (total_minus == 4) {
				sMessage = "Promoviert falls in OG\n4 ungenügende Noten";
				iColor = R.color.promo_white;
			}

			switch (iSemester) {
			case 1:
				if (total_minus > 4) {
					sMessage = "Mehr als 4 ungenügende Noten\nIm 2. Semester darf dies nicht der Fall sein";
					iColor = R.color.promo_orange;
				}
				break;
			case 2:
				if (total_minus > 4) {
					sMessage = "Nicht promoviert\nMehr als 4 ungenügende Noten";
					iColor = R.color.promo_red;
				}
				break;
			case 3:
				if (getGym(1).sMessage.contains("Nicht promoviert") || getGym(2).sMessage.contains("Nicht promoviert")) {
					sMessage = "Nicht promoviert";
					iColor = R.color.promo_red;
				}
				break;
			}
		} else {
			sMessage = "Nicht promoviert\nZu wenig Pluspunkte";
			iColor = R.color.promo_red;
		}

		double PP_result = plus - (2 * minus);
		sPP = PP_result + "/" + (mppcount * 2);

		if (fcount > 0) {
			sSchnitt = (String.format(Locale.getDefault(), "%.4f", total / fcount));
		} else {
			sSchnitt = ("-");
		}

		PromoRes prResult = new PromoRes(sMessage, iColor, sPP, sSchnitt, getKont(faecher, iSemester));
		return prResult;
	}

	public PromoRes getHMS(int iSemester) {
		Fach entry;
		double minus = 0;
		int total_minus = 0;
		double schn = 0;
		double total = 0;
		int fcount = 0;

		String sMessage = "Promoviert";
		int iColor = R.color.promo_white;
		String sPP = "0.0";
		String sSchnitt = "5.0000";

		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getFachCount();
		List<Fach> faecher = db.getAllFaecher(context, iSemester);
		for (int i = 0; i < count; i++) {
			entry = faecher.get(i);
			if (entry.getPromotionsrelevant().contentEquals("true")) {

				// Falls beide Semester ausgefüllt
				if (!entry.getRealAverage1().contentEquals("") && !entry.getRealAverage2().contentEquals("")) {
					schn = (Double.parseDouble(entry.getRealAverage1()) + Double.parseDouble(entry.getRealAverage2())) / 2;
					if (schn < 4) {
						minus += 4 - schn;
						total_minus++;
					}
					total += (Double.parseDouble(entry.getMathAverage1()) + Double.parseDouble(entry.getMathAverage2())) / 2;
					fcount++;
				}

				// Falls nur erstes Semester ausgefüllt
				if (entry.getRealAverage2().contentEquals("") && !entry.getRealAverage1().contentEquals("")) {
					if (Double.parseDouble(entry.getRealAverage1()) < 4) {
						minus += (4 - Double.parseDouble(entry.getRealAverage1()));
						total_minus++;
					}
					total += Double.parseDouble(entry.getMathAverage1());
					fcount++;
				}

				// Falls nur zweites Semester ausgefüllt
				if (!entry.getRealAverage2().contentEquals("") && entry.getRealAverage1().contentEquals("")) {
					if (Double.parseDouble(entry.getRealAverage2()) < 4) {
						minus += 4 - Double.parseDouble(entry.getRealAverage2());
						total_minus++;
					}
					total += Double.parseDouble(entry.getMathAverage2());
					fcount++;
				}
			}
		}
		if (!((total / fcount) < 4)) {
			if (total_minus <= 3) {
				sMessage = "Promoviert";
				iColor = R.color.promo_white;
			} else {
				sMessage = "Nicht promoviert\nMehr als 3 Ungenügende";
				iColor = R.color.promo_red;
			}
			if (minus > 2.5) {
				sMessage = "Nicht Promoviert\nMehr als 2.5 Minuspunkte";
				iColor = R.color.promo_red;
			}
		} else {
			sMessage = "Nicht promoviert\nGesamtschnitt unter 4";
			iColor = R.color.promo_red;
		}

		// HMS, also keine Pluspunkte anzeigen
		sPP = "-";

		if (fcount > 0) {
			sSchnitt = (String.format(Locale.getDefault(), "%.4f", total / fcount));
		} else {
			sSchnitt = ("-");
		}

		PromoRes prResult = new PromoRes(sMessage, iColor, sPP, sSchnitt, getKont(faecher, iSemester));
		return prResult;
	}

	public PromoRes getFMS(int iSemester) {
		Fach entry;
		double minus = 0;
		double schn = 0;
		double total = 0;
		double total_real = 0;
		int fcount = 0;

		String sMessage = "Promoviert";
		int iColor = R.color.promo_white;
		String sPP = "0.0";
		String sSchnitt = "5.0000";

		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getFachCount();
		List<Fach> faecher = db.getAllFaecher(context, iSemester);
		for (int i = 0; i < count; i++) {
			entry = faecher.get(i);
			if (entry.getPromotionsrelevant().contentEquals("true")) {

				// Falls beide Semester ausgefüllt
				if (!entry.getRealAverage1().contentEquals("") && !entry.getRealAverage2().contentEquals("")) {
					schn = (Double.parseDouble(entry.getRealAverage1()) + Double.parseDouble(entry.getRealAverage2())) / 2;
					if (schn < 4) {
						minus += 4 - schn;
					}
					total += (Double.parseDouble(entry.getMathAverage1()) + Double.parseDouble(entry.getMathAverage2())) / 2;
					total_real += (Double.parseDouble(entry.getRealAverage1()) + Double.parseDouble(entry.getRealAverage2())) / 2;
					fcount++;
				}

				// Falls nur erstes Semester ausgefüllt
				if (entry.getRealAverage2().contentEquals("") && !entry.getRealAverage1().contentEquals("")) {
					if (Double.parseDouble(entry.getRealAverage1()) < 4) {
						minus += (4 - Double.parseDouble(entry.getRealAverage1()));
					}
					total += Double.parseDouble(entry.getMathAverage1());
					total_real += Double.parseDouble(entry.getRealAverage1());
					fcount++;
				}

				// Falls nur zweites Semester ausgefüllt
				if (!entry.getRealAverage2().contentEquals("") && entry.getRealAverage1().contentEquals("")) {
					if (Double.parseDouble(entry.getRealAverage2()) < 4) {
						minus += 4 - Double.parseDouble(entry.getRealAverage2());
					}
					total += Double.parseDouble(entry.getMathAverage2());
					total_real += Double.parseDouble(entry.getRealAverage2());
					fcount++;
				}
			}
		}
		if (!(minus > 2.5)) {
			if (!((total_real / fcount) < 4)) {
				sMessage = "Promoviert";
				iColor = R.color.promo_white;
			} else {
				sMessage = "Nicht Promoviert\nPromotionsschnitt unter 4";
				iColor = R.color.promo_red;
			}
		} else {
			sMessage = "Nicht promoviert\nMehr als 2.5 Minuspunkte";
			iColor = R.color.promo_red;
		}

		// FMS, also keine Pluspunkte anzeigen
		sPP = "-";

		if (fcount > 0) {
			sSchnitt = (String.format(Locale.getDefault(), "%.4f", total / fcount));
		} else {
			sSchnitt = ("-");
		}

		PromoRes prResult = new PromoRes(sMessage, iColor, sPP, sSchnitt, getKont(faecher, iSemester));
		return prResult;
	}

	private String getKont(List<Fach> subjects, int semester) {
		int totalK = 0;
		int used = 0;
		double dPercentage = 0;

		Fach entry = null;
		String kontUsed = null;

		int überzogen = 0;

		for (int i = 0; i < subjects.size(); i++) {
			entry = subjects.get(i);
			if (semester == 1) {
				kontUsed = entry.getKont1();
			}
			else {
				kontUsed = entry.getKont2();
			}
			if (!entry.getKont().contentEquals("")) {
				totalK = totalK + Integer.parseInt(entry.getKont());
			}
            if (!kontUsed.contentEquals("-")) {
				used = used + Integer.parseInt(kontUsed);

				// Check für überzogen
				if (Integer.parseInt(kontUsed) > Integer.parseInt(kontUsed)) {
					überzogen++;
				}
			}
		}

		if (!(totalK == 0)) {
			dPercentage = (double) Math.round((double) used * 100 / totalK * 100) / 100;
		}
		return (used + "/" + totalK);
	}
}