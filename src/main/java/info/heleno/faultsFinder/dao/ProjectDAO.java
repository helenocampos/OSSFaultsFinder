/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.heleno.faultsFinder.dao;

import info.heleno.faultsFinder.domain.Project;
import java.sql.SQLException;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

public class ProjectDAO {

    public static List<Project> getAllProjects() throws ClassNotFoundException, SQLException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.createQuery("from Project");
        return query.list();
    }

    public static List<Project> getProject(String organization, String projectName) throws ClassNotFoundException, SQLException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery("from Project where organization= :organization and projectName = :projectName");
        query.setParameter("organization", organization);
        query.setParameter("projectName", projectName);
        List<Project> result = query.list();
        session.getTransaction().commit();
        return result;
    }

    public static Project insertProject(Project project) throws ClassNotFoundException, SQLException {
        List<Project> projects = getProject(project.getOrganization(), project.getProjectName());
        if (projects.isEmpty()) {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            session.save(project);
            session.getTransaction().commit();
        }else{
            project = projects.get(0);
        }
        
        return project;
    }

}
