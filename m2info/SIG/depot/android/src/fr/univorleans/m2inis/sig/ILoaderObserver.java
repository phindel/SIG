package fr.univorleans.m2inis.sig;
/*
Pour afficher la progression (avec un petit message)
*/
public interface ILoaderObserver{
	void onStateChanged(Pourcent pc,String s);
}
