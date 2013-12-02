package com.example.tyriangame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SurfacePanel extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceThread _thread;
	private Personagens personagens = new Personagens();
	
	private float _movimentoX = 115;
	private float _movimentoY = 250;
	
	private Bitmap _tiroBitmap, _inimigoBitmap, _campo, _gameOver;
	
	private Colisao colisao = new Colisao();
	
	static Context contexto;
	
	public SurfacePanel(Context context) {
		super(context);
		contexto = context;
		getHolder().addCallback(this);
		_thread = new SurfaceThread(getHolder(), this);
		setFocusable(true);
		
		personagens.setNaveJogador(new NaveJogador(BitmapFactory.decodeResource(
				getResources(), R.drawable.nave)));
		
		_tiroBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.tiro);
		
		_inimigoBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.inimigo1);
		
		_campo = BitmapFactory.decodeResource(getResources(),
				R.drawable.agua);
		
		_gameOver = BitmapFactory.decodeResource(getResources(),
				R.drawable.gameover);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		
		//Desenha o campo
		canvas.drawBitmap(_campo, 0, 0, null);
		
		//Desenha os Tiros
		Bitmap bitmap;
		Coordinates coordinates;
		
		for (Inimigo inimigo : personagens.getInimigo()) {
			bitmap = inimigo.getGraphic();
			coordinates = inimigo.getCoordinates();
			canvas.drawBitmap(bitmap, coordinates.getX(), coordinates.getY(), null);
		}
		
		for (Tiro tiro : personagens.getTiros()) {
			bitmap = tiro.getGraphic();
			coordinates = tiro.getCoordinates();
			canvas.drawBitmap(bitmap, coordinates.getX(), coordinates.getY(), null);
		}
		
		canvas.drawBitmap(personagens.getNaveJogador().getGraphic(),
				personagens.getNaveJogador().getCoordinates().getX(),
				personagens.getNaveJogador().getCoordinates().getY(),
				null);
		
		_gameOver = Bitmap.createScaledBitmap(_gameOver, getWidth(), getHeight(), true);
		
		if(personagens.getNaveJogador().getVida() <= 0) {
			canvas.drawBitmap(_gameOver, 0, 0, null);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_UP) {
			
			synchronized (_thread.getSurfaceHolder()) {
				Tiro tiro = new Tiro(_tiroBitmap, personagens.getNaveJogador());
				tiro.getCoordinates().setX(personagens.getNaveJogador().getCoordinates().getX()
						+ (personagens.getNaveJogador().getCoordinates().getX()/2) );
				tiro.getCoordinates().setY(personagens.getNaveJogador().getCoordinates().getY());
				tiro.getSpeed().setY(5);
				personagens.getTiros().add(tiro);
			}
		}
		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		_thread.setRunning(true);
		_thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		_thread.setRunning(false);
		while (retry) {
			try {
				_thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// ignoramos a exce��o e tentamos de novo
			}
		}
	}
	
	public void updatePhysics() {
		
		if(personagens.getInimigo().size() <= 0) {
			for(int i = 0; i < 10; i++) {
				personagens.addInimigos(_inimigoBitmap, i * 50);
			}
		}
		
		personagens.getNaveJogador().getCoordinates().setX(_movimentoX);
		personagens.getNaveJogador().getCoordinates().setY(_movimentoY);
		
		for (Tiro tiro : personagens.getTiros()) {
			tiro.getCoordinates().setY(tiro.getCoordinates().getY() - tiro.getSpeed().getY());
		}
		for(int i = 0; i < personagens.getTiros().size(); i++) {
			if (personagens.getTiros().get(i).getCoordinates().getY() < 0) {
				personagens.getTiros().remove(i);
			}
		}
		
		Coordinates coord;
		Speed speed;
		for(Inimigo inimigos : personagens.getInimigo()) {
			coord = inimigos.getCoordinates();
			speed = inimigos.getSpeed();
			
			// Direction
			if (speed.getYDirection() == Speed.Y_DIRECTION_DOWN) {
				coord.setY((int) (coord.getY() + speed.getY() * MainMenu.dificuldade));
			}
		}
		for(int i = 0; i < personagens.getInimigo().size(); i++) {
			if (personagens.getInimigo().get(i).getCoordinates().getY() > getHeight()) {
				personagens.getInimigo().remove(i);
			}
		}
		colisao.checaColisaoDoPlayerComInimigos(personagens.getNaveJogador(), personagens.getInimigo());
		colisao.checaColisaoDosTirosComInimigos(personagens.getInimigo(), personagens.getTiros());
	}
	public float setMovimentoX(float movimentoX){
		return _movimentoX += movimentoX;
	}
	
	public float setMovimentoY(float movimentoY){
		return _movimentoY += movimentoY;
	}
}
