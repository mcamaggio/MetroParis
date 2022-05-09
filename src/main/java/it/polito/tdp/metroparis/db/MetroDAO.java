package it.polito.tdp.metroparis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.javadocmd.simplelatlng.LatLng;

import it.polito.tdp.metroparis.model.Connessione;
import it.polito.tdp.metroparis.model.CoppiaId;
import it.polito.tdp.metroparis.model.Fermata;
import it.polito.tdp.metroparis.model.Linea;

public class MetroDAO {

	public List<Fermata> getAllFermate() {

		final String sql = "SELECT id_fermata, nome, coordx, coordy FROM fermata ORDER BY nome ASC";
		List<Fermata> fermate = new ArrayList<Fermata>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Fermata f = new Fermata(rs.getInt("id_Fermata"), rs.getString("nome"),
						new LatLng(rs.getDouble("coordx"), rs.getDouble("coordy")));
				fermate.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return fermate;
	}

	public List<Linea> getAllLinee() {
		final String sql = "SELECT id_linea, nome, velocita, intervallo FROM linea ORDER BY nome ASC";

		List<Linea> linee = new ArrayList<Linea>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Linea f = new Linea(rs.getInt("id_linea"), rs.getString("nome"), rs.getDouble("velocita"),
						rs.getDouble("intervallo"));
				linee.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return linee;
	}

	/**
	 * Se esiste almeno una connessione tra partenza e arrivo
	 * @param partenza
	 * @param arrivo
	 * @return
	 */
	public boolean isFermateConnesse(Fermata partenza, Fermata arrivo) {
		
		final String sql = "SELECT COUNT(*) AS cnt "
				+ "FROM connessione "
				+ "WHERE id_stazP = ? "
				+ "AND id_stazA = ?";
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, partenza.getIdFermata());
			st.setInt(2, arrivo.getIdFermata());
			ResultSet rs = st.executeQuery();
			
			rs.first();
			int count = rs.getInt("cnt");
			st.close();
			conn.close();
			
			return count>0;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.", e);
		}
		
		
	}

	public List<Integer> getIdFermateConnesse(Fermata partenza) {
		
		final String sql = "SELECT id_stazA "
				+ "FROM connessione "
				+ "WHERE id_stazP = ? "
				+ "GROUP BY id_stazA";
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, partenza.getIdFermata());
			ResultSet rs = st.executeQuery();
			
			List<Integer> idFermate = new ArrayList<Integer>();
			
			while (rs.next()) {
				idFermate.add(rs.getInt("id_stazA"));
			}
			
			st.close();
			conn.close();
			
			return idFermate;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.", e);
		}
		
	}
	
	public List<Fermata> getFermateConnesse(Fermata partenza){
		
		final String sql = "SELECT DISTINCT f.id_fermata, f.nome, f.coordx, f.coordy "
				+ "FROM fermata f, connessione c "
				+ "WHERE c.id_stazP = ? AND C.id_stazA = f.id_fermata "
				+ "ORDER BY f.nome ASC ";
		
		final String sql2 = "SELECT id_fermata, nome, coordx, coordy "
				+ "FROM fermata "
				+ "WHERE id_fermata IN ( "
				+ "SELECT id_stazA "
				+ "FROM connessione "
				+ "WHERE id_stazP = ? "
				+ "GROUP BY id_stazA "
				+ ") "
				+ "ORDER BY nome ASC ";
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql2);
			st.setInt(1, partenza.getIdFermata());
			ResultSet rs = st.executeQuery();
			
			List<Fermata> fermate = new ArrayList<Fermata>();
			
			while (rs.next()) {
				Fermata f = new Fermata(rs.getInt("id_Fermata"), rs.getString("nome"),
						new LatLng(rs.getDouble("coordx"), rs.getDouble("coordy")));
				fermate.add(f);
			}
			
			st.close();
			conn.close();
			
			return fermate;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.", e);
		}
	}
	
	public List<CoppiaId> getAllFermateConnesse(){
		
		final String sql = "SELECT DISTINCT id_stazP, id_stazA FROM connessione";
		

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			List<CoppiaId> result = new ArrayList<CoppiaId>();
			
			while (rs.next()) {
				CoppiaId c = new CoppiaId(rs.getInt("id_stazP"), rs.getInt("id_stazA"));
				result.add(c);
			}

			st.close();
			conn.close();
			
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		
		
	}
	
	
	

}
