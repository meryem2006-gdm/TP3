 package DAO;
 import Model.Employe;
 import java.util.List;
 public interface EmployeDAOI {
     public boolean add(Employe e);
     public void deleteEmploye(int id);
     public void updateEmploye(Employe e);
     public List<Employe> displayEmploye();
 }
