package com.example.tyriangame;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class Personagens {
	private NaveJogador _nave;
	private ArrayList<Tiro> _tiro = new ArrayList<Tiro>();
	private ArrayList<Inimigo> _inimigo = new ArrayList<Inimigo>();
	
	public NaveJogador getNaveJogador() {
		return _nave;
	}
	public void setNaveJogador(NaveJogador nave) {
		_nave = nave;
	}
	
	public ArrayList<Tiro> getTiros() {
		return _tiro;
	}
	
	public synchronized ArrayList<Inimigo> getInimigo() {
		return _inimigo;
	}
	
	public void addInimigos(Bitmap bitmap, int x) {
		Inimigo inimigo = new Inimigo(bitmap);
		
		inimigo.getCoordinates().setX(x);
		inimigo.getCoordinates().setY(0);
		
		_inimigo.add(inimigo);
	}
}
