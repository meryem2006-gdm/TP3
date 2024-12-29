package DAO;

import Model.Employe;
import Model.Post;
import Model.Role;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeDAOimpl implements GenericDAOI<Employe> {
    @Override
    public void add(Employe e) {
        String sql = "INSERT INTO employe (nom, prenom, email, telephone, salaire, role, poste, solde) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DBConnexion.getConnexion().prepareStatement(sql)) {
            stmt.setString(1, e.getNom());
            stmt.setString(2, e.getPrenom());
            stmt.setString(3, e.getEmail());
            stmt.setString(4, e.getTelephone());
            stmt.setDouble(5, e.getSalaire());
            stmt.setString(6, e.getRole().name());
            stmt.setString(7, e.getPost().name());
            stmt.setInt(8, e.getSolde());
            stmt.executeUpdate();
        } catch (SQLException exception) {
            System.err.println("Failed to add employee: " + exception.getMessage());
            exception.printStackTrace();
        } catch (ClassNotFoundException ex) {
            System.err.println("Failed to connect to the database: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM employe WHERE id = ?";
        try (PreparedStatement stmt = DBConnexion.getConnexion().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException exception) {
            System.err.println("Failed to delete employee: " + exception.getMessage());
        } catch (ClassNotFoundException ex) {
            System.err.println("Failed to connect to the database: " + ex.getMessage());
        }
    }

    @Override
    public void update(Employe e) {
        String sql = "UPDATE employe SET nom = ?, prenom = ?, email = ?, telephone = ?, salaire = ?, role = ?, poste = ?, solde = ? WHERE id = ?";
        try (PreparedStatement stmt = DBConnexion.getConnexion().prepareStatement(sql)) {
            stmt.setString(1, e.getNom());
            stmt.setString(2, e.getPrenom());
            stmt.setString(3, e.getEmail());
            stmt.setString(4, e.getTelephone());
            stmt.setDouble(5, e.getSalaire());
            stmt.setString(6, e.getRole().name());
            stmt.setString(7, e.getPost().name());
            stmt.setInt(8, e.getSolde());
            stmt.setInt(9, e.getId());
            stmt.executeUpdate();
        } catch (SQLException exception) {
            System.err.println("Failed to update employee: " + exception.getMessage());
        } catch (ClassNotFoundException ex) {
            System.err.println("Failed to connect to the database: " + ex.getMessage());
        }
    }

    @Override
    public List<Employe> display() {
        String sql = "SELECT * FROM employe";
        List<Employe> employes = new ArrayList<>();
        try (PreparedStatement stmt = DBConnexion.getConnexion().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String email = rs.getString("email");
                String telephone = rs.getString("telephone");
                double salaire = rs.getDouble("salaire");
                String role = rs.getString("role");
                String poste = rs.getString("poste");
                int solde = rs.getInt("solde");
                Employe e = new Employe(id, nom, prenom, email, telephone, salaire, Role.valueOf(role), Post.valueOf(poste), solde);
                employes.add(e);
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("Failed to connect to the database: " + ex.getMessage());
        } catch (SQLException ex) {
            System.err.println("Failed to retrieve employees: " + ex.getMessage());
        }
        return employes;
    }

    public void updateSolde(int id, int solde) {
        String sql = "UPDATE employe SET solde = ? WHERE id = ?";
        try (PreparedStatement stmt = DBConnexion.getConnexion().prepareStatement(sql)) {
            stmt.setInt(1, solde);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException exception) {
            System.err.println("Failed to update employee balance: " + exception.getMessage());
        } catch (ClassNotFoundException ex) {
            System.err.println("Failed to connect to the database: " + ex.getMessage());
        }
    }

    public void importData(String filePath) {
        String sql = "INSERT INTO employe (nom, prenom, email, telephone, salaire, role, poste, solde) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             PreparedStatement stmt = DBConnexion.getConnexion().prepareStatement(sql)) {

            String line = reader.readLine(); // Skip header line
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 8) {
                    stmt.setString(1, data[0].trim());
                    stmt.setString(2, data[1].trim());
                    stmt.setString(3, data[2].trim());
                    stmt.setString(4, data[3].trim());
                    stmt.setDouble(5, Double.parseDouble(data[4].trim()));
                    stmt.setString(6, data[5].trim());
                    stmt.setString(7, data[6].trim());
                    stmt.setInt(8, Integer.parseInt(data[7].trim()));
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
            System.out.println("Employees imported successfully!");

        } catch (SQLException | IOException e) {
            System.err.println("Failed to import data: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
        }
    }

    public void exportData(String fileName, List<Employe> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("nom,prenom,email,telephone,salaire,role,poste,solde");
            writer.newLine();
            for (Employe employe : data) {
                String line = String.format("%s,%s,%s,%s,%.2f,%s,%s,%d",
                        employe.getNom(),
                        employe.getPrenom(),
                        employe.getEmail(),
                        employe.getTelephone(),
                        employe.getSalaire(),
                        employe.getRole().name(),
                        employe.getPost().name(),
                        employe.getSolde());
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Data exported successfully to " + fileName);
        } catch (IOException exception) {
            System.err.println("Failed to export data: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
}
