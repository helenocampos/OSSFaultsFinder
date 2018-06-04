/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.heleno.faultsFinder;

import info.heleno.faultsFinder.dao.HibernateUtil;
import info.heleno.faultsFinder.dao.ProjectDAO;
import info.heleno.faultsFinder.domain.FailedBuild;
import info.heleno.faultsFinder.domain.Project;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Heleno
 */
public class Extractor
{

    public static void main(String[] args) throws Exception
    {
        turnLoggingOFF();
        if (args.length >= 3 && args.length <= 4)
        {
            String mode = args[0];
            String organization = args[1];
            String projectName = args[2];
            if (mode.contains("help"))
            {
                printHelp();
            } else
            {
                if (mode.equals("-save"))
                {
                    saveFailedBuilds(organization, projectName);
                } else
                {
                    if (mode.equals("-download"))
                    {
                        if (args.length == 4)
                        {
                            int startFrom = Integer.valueOf(args[3]);
                            downloadFailedBuilds(organization, projectName, startFrom);
                        } else
                        {
                            downloadFailedBuilds(organization, projectName);
                        }
                    } else
                    {
                        printHelp();
                    }
                }
            }
            HibernateUtil.closeSession();
        } else
        {
            printHelp();
        }

    }

    private static void turnLoggingOFF()
    {
       Logger log = Logger.getLogger("org.hibernate");
        log.setLevel(Level.WARNING);
    }

    public static void printHelp()
    {
        System.out.println("Available argument options:");
        System.out.println("Save faulty builds information on database: -save organization project");
        System.out.println("Download faulty builds: -download organization project");
        System.out.println("Download faulty builds starting from specific build: -download organization project startFrom");

        System.out.println("Examples:");
        System.out.println("-save alibaba fastjson");
        System.out.println("-download alibaba fastjson");
        System.out.println("-download alibaba fastjson 1534");
    }

    private static void downloadFailedBuilds(String organization, String projectName)
    {
        try
        {
            List<Project> projects = ProjectDAO.getProject(organization, projectName);
            if (!projects.isEmpty())
            {
                Project project = projects.get(0);
                for (FailedBuild failedBuild : project.getFailedBuildList())
                {
                    failedBuild.downloadProjectFolder(organization, projectName);
                }
            }
        } catch (ClassNotFoundException ex)
        {
//            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex)
        {
//            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void downloadFailedBuilds(String organization, String projectName, int startFrom)
    {
        try
        {
            List<Project> projects = ProjectDAO.getProject(organization, projectName);
            if (!projects.isEmpty())
            {
                Project project = projects.get(0);
                for (FailedBuild failedBuild : project.getFailedBuildList())
                {
                    if (failedBuild.getBuildNumber() <= startFrom)
                    {
                        failedBuild.downloadProjectFolder(organization, projectName);
                    }
                }
            }
        } catch (ClassNotFoundException ex)
        {
//            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex)
        {
//            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void saveFailedBuilds(String organization, String projectName)
    {
        Project project = new Project(organization, projectName);
        try
        {
            project = ProjectDAO.insertProject(project);
            project.proccessFailedBuilds();
        } catch (Exception e)
        {
            System.out.println("Could not save project");
        }
    }
}
