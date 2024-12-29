package Model;

import DAO.EmployeDAOimpl;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class EmployeModel {
    private EmployeDAOimpl dao;

    // Constructeur
    public EmployeModel(EmployeDAOimpl dao) {
        this.dao = dao;
    }

    // Fonction pour ajouter un employé
    public boolean addEmploye(int id , String nom, String prenom, String email, String telephone, double salaire, Role role, Post post, int solde) {
        if (salaire < 0) {
            System.out.println("Erreur : le salaire doit être positif.");
            return false;
        }
        if (id < 0) {
            System.out.println("Erreur : l'id doit être positif.");
            return false;
        }
        if (telephone.length() != 10) {
            System.out.println("Erreur : le téléphone doit être de 10 chiffres.");
            return false;
        }
        if (!email.contains("@")) {
            System.out.println("Erreur : le mail doit contenir un @.");
            return false;
        }
        Employe e = new Employe(id, nom, prenom, email, telephone, salaire, role, post, solde);
        dao.add(e);
        return true;
    }

    // Fonction pour supprimer un employé
    public boolean deleteEmploye(int id) {
        dao.delete(id);
        return true;
    }

    // Fonction pour mettre à jour un employé
    public boolean updateEmploye(int id, String nom, String prenom, String email, String telephone, double salaire, Role role, Post post, int solde) {
        Employe e = new Employe(id, nom, prenom, email, telephone, salaire, role, post, solde);
        dao.update(e);
        return true;
    }

    // Fonction pour mettre à jour le solde d'un employé
    public boolean updateSolde(int id, int solde) {
        dao.updateSolde(id, solde);
        return true;
    }

    // Fonction pour afficher la liste des employés
    public List<Employe> displayEmploye() {
        return dao.display();
    }

    // Fonction pour importer les employés depuis un fichier CSV
    public void importData(String filePath) {
        File file = new File(filePath);

        // Vérification de la validité du fichier
        checkFileExists(file);
        checkIsFile(file);
        checkIsReadable(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Supposons que le format CSV soit : id, nom, prenom, email, telephone, salaire, role, poste, solde
                String[] data = line.split(",");
                if (data.length == 9) { // Vérification du nombre correct de champs
                    try {
                        int id = Integer.parseInt(data[0].trim());
                        String nom = data[1].trim();
                        String prenom = data[2].trim();
                        String email = data[3].trim();
                        String telephone = data[4].trim();
                        double salaire = Double.parseDouble(data[5].trim());
                        String roleString = data[6].trim();
                        String posteString = data[7].trim();
                        int solde = Integer.parseInt(data[8].trim());

                        // Conversion de role et poste en enums
                        Role role = Role.valueOf(roleString.toUpperCase());
                        Post poste = Post.valueOf(posteString.toUpperCase());

                        // Création et ajout de l'employé
                        Employe employe = new Employe(id, nom, prenom, email, telephone, salaire, role, poste, solde);
                        dao.add(employe);
                    } catch (NumberFormatException e) {
                        System.out.println("Erreur de format dans les données du fichier.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Erreur de valeur d'enum dans le fichier: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier: " + e.getMessage());
        }
    }

    // Fonction de validation du fichier : vérifie qu'il existe
    private boolean checkFileExists(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("Le fichier n'existe pas : " + file.getPath());
        }
        return true;
    }

    // Vérifie que le chemin correspond bien à un fichier
    private boolean checkIsFile(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("Le chemin spécifié n'est pas un fichier : " + file.getPath());
        }
        return true;
    }

    // Vérifie que le fichier est lisible
    private boolean checkIsReadable(File file) {
        if (!file.canRead()) {
            throw new IllegalArgumentException("Le fichier n'est pas lisible : " + file.getPath());
        }
        return true;
    }
    public void exportData(String FileName , List<Employe> data) throws IOException {
        File file = new File(FileName);
        checkIsReadable(file);
        checkIsFile(file);
        dao.exportData(FileName,data);
}
}

