/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prac1;

import marvin.io.MarvinImageIO;
import plugins.Invert;
import tools.BFrame;

/**
 * History sample
 *
 * @author Gabriel Ambrosio Archanjo
 *
 */
public class History extends BFrame {

    public History(String filename) {
        super(filename);
    }

    /**
     * przetwarzanie zdjęcia
     */
    //@Override
    protected void process() {
        resultImage = originalImage.clone();
        history.addEntry("Original", resultImage, null);

        //
        // inwersja koloru zdjęcia
        //

        // wczytanie pluginu
        tempPlugin = new Invert();
        // przetworzenie zdjęcia
        tempPlugin.process(resultImage, resultImage);
        //zaktualizowanie obrazka
        resultImage.update();
        //dodanie do historii
        //addToHistory("Invert");


        // wyświetlenie wyników działania programu na wykresie
        imagePanelNew.setImage(resultImage);
        
        if(resultImage != null){
            MarvinImageIO.saveImage(resultImage, "./res/out.png");
	}
        
    }

    public static void main(String args[]) {
        // nazwa wyświetlanego zdjęcia
        String filename = "./res/graph.png"; //"./res/01.jpg";

        new History(filename);
    }
}


/*
  
// wczytanie pluginu
tempPlugin = new ColorHistogram();
// przetworzenie zdjęcia
tempPlugin.process(resultImage, resultImage);
  
 */