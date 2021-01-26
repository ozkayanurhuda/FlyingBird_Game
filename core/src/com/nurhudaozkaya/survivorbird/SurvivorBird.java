package com.nurhudaozkaya.survivorbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;

import java.util.Random;

public class SurvivorBird extends ApplicationAdapter {

	SpriteBatch batch;
	Texture background;
	Texture bird;
	Texture bee1;
	Texture bee2;
	Texture bee3;
	float birdX = 0 ; // kuşların x ve y ekseninde başlangıçları
	float birdY = 0 ;
	int gameState = 0 ; // başlamamışken 0 olsun dedim başlayınca 1 yapıcam
	float velocity = 0 ; // kuşun düşerkenki hızı
	float gravity = 0.4f ; // yerçekimi ekledim
	float enemyVelocity = 4 ; // arıların hızı
	Random random ; // y eksenindeki konumu random atamak için
	int score = 0;  //scorlama işlemi için bi int oluştuyorum
	int scoredEnemy = 0 ; // enemy benim kuşumu geçtiyse ve hala kuş yaşıyorsa
	BitmapFont font; // skoru göstermek için
	BitmapFont font2; // game over yazdırmak için kullandığım

	Circle birdCircle ; // bird circle ımı oluşturdum

	ShapeRenderer shapeRenderer;

	int numberOfEnemies = 4 ; // 4 tane 3lü arı seti tanımladım
	float [] enemyX = new float[numberOfEnemies] ; // enemylere x eksenini atadım
	float [] enemyOffSet = new float[numberOfEnemies] ; // y eksenini rastgele atamak için oluşturduğum değişkenler
	float [] enemyOffSet2 = new float[numberOfEnemies] ;
	float [] enemyOffSet3 = new float[numberOfEnemies] ;
	float distance = 0 ;

	Circle[] enemyCircles; // enemy circle larımı oluşturdum
	Circle[] enemyCircles2;
	Circle[] enemyCircles3;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("background.png");
		bird = new Texture("bird.png"); // resmi bird.png den al
		bee1 = new Texture("bee.png");
		bee2 = new Texture("bee.png");
		bee3 = new Texture("bee.png");

		distance = Gdx.graphics.getWidth() / 2 ; // iki arı arası fark ekran yarısı
		random = new Random(); //offsetleri oluştururken kullanıcam

		//istediğim zaman birdx ve y ı değiştirebileceğim bir setup ayarladım
		birdX = Gdx.graphics.getWidth() / 2 - bird.getHeight() / 2;
		birdY = Gdx.graphics.getHeight() / 3; // y ekseni her halukarda değişicek çünkü kuşu zıplatıcam

		shapeRenderer = new ShapeRenderer();

		birdCircle = new Circle(); //circle ları initialize ediyorum
		enemyCircles = new Circle[numberOfEnemies];
		enemyCircles2 = new Circle[numberOfEnemies];
		enemyCircles3 = new Circle[numberOfEnemies];

		font = new BitmapFont(); //skor için boyut ve renk
		font.setColor(Color.PURPLE);
		font.getData().setScale(4);

		font2 = new BitmapFont(); // game over yazdırmak için
		font2.setColor(Color.PURPLE);
		font2.getData().setScale(6);



		// her oluşan arı setinin distance ı otomatik ayarlanıcak
		for (int i = 0 ; i < numberOfEnemies ; i++) { // beelerin arasında ne kadar mesafe olacağını initialize ettim


			// küçük sayılar çıkarıp daha gerçekçi sonuçlar elde ettim
			enemyOffSet[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() -200) ; //0 - 1 arası bir değer verir *
			enemyOffSet2[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() -200) ;
			enemyOffSet3[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() -200) ;

			enemyX[i] = Gdx.graphics.getWidth() - bee1.getWidth() / 2 + i * distance ; // her seferinde farklı uzaklık olucak aynı y ekseninde

			enemyCircles[i]= new Circle(); //enemynin içindeki circle ları da initialize ettim
			enemyCircles2[i]= new Circle();
			enemyCircles3[i]= new Circle();
		}
	}

	@Override
	public void render () {

		batch.begin();

		//backgroundu çizip konumlandırdım
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if (gameState == 1) { // oyun başladığında

			if (enemyX[scoredEnemy] < Gdx.graphics.getWidth() / 2 - bird.getHeight() / 2) {
				score ++;

				if(scoredEnemy<numberOfEnemies-1) { //number of enemies -1
					scoredEnemy++;
				} else {
					scoredEnemy=0;
				}
			}

			if ( Gdx.input.justTouched()) { // tekrar tıklarsa ne olucak
				velocity = -7 ; // kuşun zıplamasını sağlıyor (yükselmesini)
			}

			// dokunduğumda 4lü bee gruplarının devamlı gelmesini sağladım
			for (int i = 0 ; i < numberOfEnemies ; i++) {

				if (enemyX[i] < 0) { // ekrandan kaybolunca , ekranda kapladığı yer 0 noktasından küçükse
					enemyX[i] = enemyX[i] + numberOfEnemies * distance;

					//yukarıdaki aynı ifadeyi yazdım arılar için
					enemyOffSet[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() -200) ;
					enemyOffSet2[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() -200) ;
					enemyOffSet3[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() -200) ;

				} else {

					enemyX[i] = enemyX[i] - enemyVelocity ; //her seferinde 2 çıkararak konumlandırdım
				}

				// oyun başladığında arılar gelmeye başlasın. boyutları kuş ile aynı olsun dedim.
				// arıların hepsi aynı x ekseni üzerinde olsun fakat y ler farklı
				batch.draw(bee1, enemyX[i], Gdx.graphics.getHeight()/2 + enemyOffSet[i], Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);
				batch.draw(bee2, enemyX[i], Gdx.graphics.getHeight()/2 + enemyOffSet2[i], Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);
				batch.draw(bee3, enemyX[i], Gdx.graphics.getHeight()/2 + enemyOffSet3[i], Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);

				enemyCircles[i] = new Circle(enemyX[i]+ Gdx.graphics.getWidth()/30,Gdx.graphics.getHeight()/2 + enemyOffSet[i]+Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth() / 30);
				enemyCircles2[i] = new Circle(enemyX[i]+ Gdx.graphics.getWidth()/30,Gdx.graphics.getHeight()/2 + enemyOffSet2[i]+Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth() / 30);
				enemyCircles3[i] = new Circle(enemyX[i]+ Gdx.graphics.getWidth()/30,Gdx.graphics.getHeight()/2 + enemyOffSet3[i]+Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth() / 30);
			}

			if ( birdY > 0 ) { // kuşun 0ın ekranın altına düşüp kaybolmaması için eksen koşullarını sağlattırdım
				velocity = velocity + gravity ; // yerçekimini ayarladım. her seferinde 0,4 piksel düşüyor
				birdY = birdY - velocity ;
			} else {
				gameState=2; // oyun bitmiş demektir
			}

		} else if (gameState==0) { // gameState 0 ise oyun başlmamışsa
			if (Gdx.input.justTouched()) { // dokunduğunda ne olucak
				gameState = 1; // başlayınca gameStateim 1 oluyor
			}
		} else if (gameState==2) { // oyun bittiğinde
			//game over yazısını draw
			font2.draw(batch,"Game Over! Tap To Play Again!",100,Gdx.graphics.getHeight()/2); // 100 e ekranın ortası konumu

			if(Gdx.input.justTouched()) { // tekrar tıkladıysa
				gameState=1; // oyunu tekrar başlat

				birdY = Gdx.graphics.getHeight() / 3; // kuş saçma bi yerde başlamasın diye başlangıçtaki konumu tekrar atadım

				// for looptaki initializationları tekrar yapmam gerkiyor
				// her oluşan arı setinin distance ı otomatik random ayarlanıcak
				for (int i = 0 ; i < numberOfEnemies ; i++) { // beelerin arasında ne kadar mesafe olacağını initialize ettim
					// küçük sayılar çıkarıp daha gerçekçi sonuçlar elde ettim
					enemyOffSet[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() -200) ; //0 - 1 arası bir değer verir *
					enemyOffSet2[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() -200) ;
					enemyOffSet3[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() -200) ;

					enemyX[i] = Gdx.graphics.getWidth() - bee1.getWidth() / 2 + i * distance ;

					enemyCircles[i]= new Circle(); //enemynin içindeki circle ları da initialize ettim
					enemyCircles2[i]= new Circle();
					enemyCircles3[i]= new Circle();
				}
				velocity = 0; // çok yukarılara çıktıysa baştan başlasın
				scoredEnemy =0; // oyun bitince skorlar sıfırlansın
				score =0;
			}
		}


		//kuşu çizdim
		batch.draw(bird, birdX, birdY, Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);

		font.draw(batch,String.valueOf(score),100,200) ;// skoru ekrana drawlıyorum



		batch.end();

		//birdCircle ı çizdim fakat görünmüyor
		// yüksekliğinin ve genişliğinin yarısı kadar ekleme yaptım .circle ın merkezini kuşun ortasına konumlandırmak için
		birdCircle.set(birdX + Gdx.graphics.getWidth()/30,birdY + Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30); // radius genişliğin yarısı olmalı

		for (int i=0;i<numberOfEnemies;i++) { //birdcircle ve enemycircle lar çarpışması

			if(Intersector.overlaps(birdCircle,enemyCircles[i]) || Intersector.overlaps(birdCircle,enemyCircles2[i]) || Intersector.overlaps(birdCircle,enemyCircles3[i])) {
				gameState = 2; // oyunu bitir
			}
		}
	}

	@Override
	public void dispose () {

	}
}
