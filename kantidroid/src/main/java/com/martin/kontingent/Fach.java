package com.martin.kontingent;

public class Fach {

	// private Variablen
	int _id;
	String _name;
	String _kont_av;
	String _kont_us;
	String _dates;

	// leerer constructor
	public Fach() {

	}

	// constructor mit id
	public Fach(int id, String name, String kont_av, String kont_us, String dates) {
		this._id = id;
		this._name = name;
		this._kont_av = kont_av;
		this._kont_us = kont_us;
		this._dates = dates;
	}

	// constructor für addFach
	public Fach(String name, String kont_av) {
		this._name = name;
		this._kont_av = kont_av;
	}

	// getting ID
	public int getID() {
		return this._id;
	}

	// setting id
	public void setID(int id) {
		this._id = id;
	}

	// getting name
	public String getName() {
		return this._name;
	}

	// setting name
	public void setName(String name) {
		this._name = name;
	}

	// getting kont_av
	public String getKont_av() {
		return this._kont_av;
	}

	// setting kont_av
	public void setKont_av(String kont_av) {
		this._kont_av = kont_av;
	}

	// getting kont_us
	public String getKont_us() {
		return this._kont_us;
	}

	// setting kont_us
	public void setKont_us(String kont_us) {
		this._kont_us = kont_us;
	}

	// getting dates
	public String getDates() {
		return this._dates;
	}

	// setting dates
	public void setDates(String dates) {
		this._dates = dates;
	}
}
