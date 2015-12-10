package fr.univorleans.m2inis.sig;
import java.util.Collection;
import java.util.List;
public class BatimentUniv extends Zone{
	public BatimentUniv(String nom,List<Point>polygoneExterieur,Collection<Service> services_){
		super(nom,polygoneExterieur,batimentUniv);
		services=services_;
	}
	public Collection<Service> getService(){
		return services;
	}
	private Collection<Service> services;
}
