package it.polito.tdp.metroparis.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	private Graph<Fermata, DefaultEdge> grafo;
	
	public void creaGrafo() {
		this.grafo = new SimpleDirectedGraph<Fermata, DefaultEdge>(DefaultEdge.class);
		MetroDAO dao = new MetroDAO();
		
		List<Fermata> fermate = dao.getAllFermate();
		Map<Integer, Fermata> fermateIdMap = new HashMap<Integer, Fermata>();		
		for(Fermata f : fermate)
			fermateIdMap.put(f.getIdFermata(), f);
		
		Graphs.addAllVertices(this.grafo, fermate);
		
		/**
		 * METODO 1
		 * Soluzione più semplice
		 * Itero su ogni coppia di vertici
		 */
/*		for(Fermata partenza : fermate) {
			for(Fermata arrivo : fermate) {
				if(dao.isFermateConnesse(partenza, arrivo)) { //se esiste almeno una connessione tra partenza e arrivo
					this.grafo.addEdge(partenza, arrivo);
				}
			}
		}
*/
		
		/**
		 * METODO 2
		 * Dato ciascun vertice, trova i vertici ad esso adiacenti
		 */
/*		for(Fermata partenza : fermate) {
			List<Integer> idConnesse = dao.getIdFermateConnesse(partenza);
			for(Integer id : idConnesse) {
//				Fermata arrivo = (Fermata che possiede questo id); 
				Fermata arrivo = null;
				for(Fermata f : fermate) {
					if(f.getIdFermata() == id) {
						arrivo = f;
						break;
					}
				}
				this.grafo.addEdge(partenza, arrivo);
			}
		}
*/
		
		/**
		 * VARIANTE METODO 2b
		 * Il DAO mi restituisce un elenco di oggetti fermata
		 */
/*		for(Fermata partenza : fermate) {
			List<Fermata> arrivi = dao.getFermateConnesse(partenza);
			for(Fermata arrivo : arrivi) {
				this.grafo.addEdge(partenza, arrivo);
			}
		}
*/
		
		/**
		 * VARIANTE METODO 2c
		 * Il DAO mi restituisce un elenco di ID numerici, che converto in oggetti
		 * tramite una Map<Integer, Fermata> - "Identity Map"
		 */
/*		for(Fermata partenza : fermate) {
			List<Integer> idConnesse = dao.getIdFermateConnesse(partenza);
			for(int id : idConnesse) {
				Fermata arrivo = fermateIdMap.get(id);
				this.grafo.addEdge(partenza, arrivo);
			}
		}
*/
		
		/**
		 * METODO 3
		 * Faccio una sola query che mi restituisce le coppie di fermate da collegare 
		 * (Variante preferita: 3C: Usare Identity Map)
		 */
		List<CoppiaId> fermateDaCollegare = dao.getAllFermateConnesse();			
		for(CoppiaId coppia : fermateDaCollegare) {
			this.grafo.addEdge(fermateIdMap.get(coppia.getIdPartenza()), fermateIdMap.get(coppia.getIdArrivo()));
		}
		
		
		System.out.println(this.grafo);
		System.out.println("Vertici = " + this.grafo.vertexSet().size());
		System.out.println("Archi = " + this.grafo.edgeSet().size());
		
		visitaGrafo(fermate.get(0));
	}
	
	public void visitaGrafo(Fermata partenza) {
		GraphIterator<Fermata, DefaultEdge> visita = 
//				new BreadthFirstIterator<>(this.grafo, partenza); // per Livelli
				new DepthFirstIterator<>(this.grafo, partenza); // per Profondità
		
		while(visita.hasNext()) {
			Fermata f = visita.next();
			System.out.println(f);
		}
		
		
	}

}
