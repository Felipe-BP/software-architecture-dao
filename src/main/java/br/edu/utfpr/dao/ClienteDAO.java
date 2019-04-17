package br.edu.utfpr.dao;

import java.sql.Connection;
import java.sql.DriverManager;

import br.edu.utfpr.dto.ClienteDTO;
import lombok.extern.java.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Log
public class ClienteDAO {

    //facilitar o acesso
    private Connection conn;

    //constants string SQL
    private final String INSERT = "INSERT INTO cliente (id, nome, telefone, idade, limiteCredito, id_pais) VALUES (?, ?, ?, ?, ?, ?)";
    private final String UPDATE = "UPDATE cliente SET name=?, telefone=?, idade=?, limiteCredito=?, id_pais=? WHERE id=?";
    private final String DELETE = "DELETE FROM cliente WHERE id=?";
    private final String SELECT_ALL = "SELECT * FROM cliente JOIN pais USING (id_pais)";

    // Responsável por criar a tabela Cliente no banco.
    public ClienteDAO() {

        try (this.conn = DriverManager.getConnection("jdbc:derby:memory:database;create=true")) {

            log.info("Criando tabela cliente ...");
            this.conn.createStatement().executeUpdate(
            "CREATE TABLE cliente (" +
						"id int NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT id_cliente_pk PRIMARY KEY," +
						"nome varchar(255)," +
						"telefone varchar(30)," + 
						"idade int," + 
                        "limiteCredito double," +
                        "id_pais int)");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void create(ClientDTO cliente) {
        try {
            PreparedStatement statement = this.conn.prepareStatement(this.INSERT);
            statement.setInt(1, cliente.getId());
            statement.setString(2, cliente.getName());
            statement.setString(3, cliente.getTelefone());
            statement.setInt(4, cliente.getIdade());
            statement.setDouble(5, cliente.getLimiteCredito());
            statement.setInt(6, cliente.getPais().getId());
            
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new Client was inserted successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(ClientDTO cliente) {
        try {
            PreparedStatement statement = this.conn.prepareStatement(this.UPDATE);
            statement.setString(2, cliente.getName());
            statement.setString(3, cliente.getTelefone());
            statement.setInt(4, cliente.getIdade());
            statement.setDouble(5, cliente.getLimiteCredito());
            statement.setInt(6, cliente.getPais().getId());
            statement.setInt(1, cliente.getId());
            
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("An existing Client was updated successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        try {
            PreparedStatement statement = this.conn.prepareStatement(this.DELETE);
            statement.setInt(1, id);
                
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("A Client was deleted successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ClientDTO> selectAll() {
        try {
            Statement statement = this.conn.createStatement();
            ResultSet result = statement.executeQuery(this.SELECT_ALL);
            List<ClientDTO> clients = new ArrayList<>();
             
            int count = 0;
             
            while (result.next()){
                PaisDTO pais = new PaisDTO();
                ClientDTO client = new ClientDTO();

                pais.setId(result.getInt("pais.id"));
                pais.setName(result.getString("pais.name"));
                pais.setSigla(result.getString("sigla"));
                pais.setcodigoTelefone(result.getInt("codigoTelefone"));

                client.setId(result.getInt("client.id"));
                client.setName(result.getString("client.name"));
                client.setTelefone(result.getString("telefone"));
                client.setIdade(result.getInt(4));
                client.setLimiteCredito(result.getDouble(5));
                client.setPais(pais);

                clients.add(client);
            }
            return clients;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}