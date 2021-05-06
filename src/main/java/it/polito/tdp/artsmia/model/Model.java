package it.polito.tdp.artsmia.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.Adiacenza;
import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {

	private Graph<ArtObject, DefaultWeightedEdge> grafo;
	private ArtsmiaDAO dao;
	private Map<Integer, ArtObject> idMap; //identity map per evitare di chiamare il dao più volte inutilmente
	
	public Model() {
		dao = new ArtsmiaDAO();
		idMap = new HashMap<Integer, ArtObject>();
	}
	
	public void creaGrafo() {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//Aggiunta vertici
		dao.listObjects(idMap);
		Graphs.addAllVertices(grafo, idMap.values());
		
		//Aggiunta archi
		//Approccio 1
		//doppio for per confrontare ogni coppia di vertici se va collegata
		//ci mette circa 600 giorni
		/*for(ArtObject a1 : this.grafo.vertexSet()) {
			for(ArtObject a2 : this.grafo.vertexSet()) {
				//Verifico che i vertifi siano diversi e non vi sia già un arco tra loro
				if(!a1.equals(a2) && !this.grafo.containsEdge(a1,a2)) {
					//devo collegare a1 ad a2?
					 int peso = dao.getPeso(a1, a2);
					 if(peso > 0) {
						 Graphs.addEdge(this.grafo, a1, a2, peso);
					 }
				}
			}
		}*/
		
		//Approccio 2
		//per tutti i vertici (uno alla volta) chiedo al db di dirmi quali sono i vertici a quello collegati
		//è ancora troppo lento (circa 30 min)
		
		//Approccio 3
		//chiedo al db tutte le coppie di vertici collegati con relativo peso (unica query)
		List<Adiacenza> adiacenze = this.dao.getAdiacenze();
		for(Adiacenza a : adiacenze) {
			Graphs.addEdge(this.grafo, idMap.get(a.getId1()), idMap.get(a.getId2()), a.getPeso());
		}
		
		System.out.println("GRAFO CREATO!");
		System.out.println("N. di vertici: "+grafo.vertexSet().size());
		System.out.println("N. di archi: "+grafo.edgeSet().size());
	}
	
}
