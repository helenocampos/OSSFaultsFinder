/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.heleno.faultsFinder.dao;

import info.heleno.faultsFinder.domain.FailedBuild;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

public class FailedBuildDAO {

    public static List<FailedBuild> getAllFailedBuilds() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.createQuery("from FailedBuild");
        return query.list(); 
    }
    
    
    public static List<FailedBuild> getFailedBuild(int buildNumber, int projectId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery("from FailedBuild where buildNumber= :buildNumber and project_idProject = :projectId");
        query.setParameter("buildNumber", buildNumber);
        query.setParameter("projectId", projectId);
        List<FailedBuild> result = query.list();
        session.getTransaction().commit();
        return result;
    }
    
//    public static List<FailedBuild> getFailedBuilds(String organization, String projectName) {
//        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//        session.beginTransaction();
//        Query query = session.createQuery("from FailedBuild where buildNumber= :buildNumber and project_idProject = :projectId");
//        query.setParameter("buildNumber", buildNumber);
//        query.setParameter("projectId", projectId);
//        List<FailedBuild> result = query.list();
//        session.getTransaction().commit();
//        return result;
//    }

    public static FailedBuild insertFailedBuild(FailedBuild build){
        
        List<FailedBuild> builds = getFailedBuild(build.getBuildNumber(), build.getProjectidProject().getIdProject());
        if (builds.isEmpty()) {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            session.save(build);
            session.getTransaction().commit();
        }else{
            build = builds.get(0);
        }
        
        return build;
    }

}
