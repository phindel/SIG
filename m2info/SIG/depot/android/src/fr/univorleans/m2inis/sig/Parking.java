package fr.univorleans.m2inis.sig;
import java.util.List;

public class Parking extends Zone{
	public Parking(String nom,List<Point>polygoneExterieur){
		super(nom,polygoneExterieur,parking);
	}
	
}
