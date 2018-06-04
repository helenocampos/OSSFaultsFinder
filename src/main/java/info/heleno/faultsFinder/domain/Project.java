/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.heleno.faultsFinder.domain;

import fr.inria.jtravis.JTravis;
import fr.inria.jtravis.entities.Build;
import fr.inria.jtravis.entities.Builds;
import fr.inria.jtravis.entities.Commit;
import fr.inria.jtravis.entities.Job;
import fr.inria.jtravis.entities.Log;
import fr.inria.jtravis.entities.Repository;
import fr.inria.jtravis.entities.StateType;
import fr.inria.jtravis.entities.TestsInformation;
import info.heleno.faultsFinder.dao.FailedBuildDAO;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Heleno
 */
@Entity
@Table(name = "project")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Project.findAll", query = "SELECT p FROM Project p")
    , @NamedQuery(name = "Project.findByIdProject", query = "SELECT p FROM Project p WHERE p.idProject = :idProject")
    , @NamedQuery(name = "Project.findByOrganization", query = "SELECT p FROM Project p WHERE p.organization = :organization")
    , @NamedQuery(name = "Project.findByProjectName", query = "SELECT p FROM Project p WHERE p.projectName = :projectName")})
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idProject")
    private Integer idProject;
    @Column(name = "organization")
    private String organization;
    @Column(name = "projectName")
    private String projectName;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "projectidProject")
    private List<FailedBuild> failedbuildList;

    public Project() {
    }

    public Project(String organization, String projectName) {
        this.organization = organization;
        this.projectName = projectName;
        this.failedbuildList = new LinkedList<>();
    }

    public Project(Integer idProject) {
        this.idProject = idProject;
    }

    public Integer getIdProject() {
        return idProject;
    }

    public void setIdProject(Integer idProject) {
        this.idProject = idProject;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @XmlTransient
    public List<FailedBuild> getFailedBuildList() {
        return failedbuildList;
    }

    public void setFailedBuildList(List<FailedBuild> failedbuildList) {
        this.failedbuildList = failedbuildList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idProject != null ? idProject.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Project)) {
            return false;
        }
        Project other = (Project) object;
        if ((this.idProject == null && other.idProject != null) || (this.idProject != null && !this.idProject.equals(other.idProject))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "info.heleno.faultsFinder.domain.Project[ idProject=" + idProject + " ]";
    }

    public void proccessFailedBuilds() {
        JTravis jTravis = new JTravis.Builder().build();
        Optional<Repository> repository = jTravis.repository().fromSlug(getProjectSlug());

        if (repository.isPresent()) {
            Optional<Builds> optionalBuilds = jTravis.build().fromRepository(repository.get());
            while (optionalBuilds.isPresent()) {
                for (Build build : optionalBuilds.get().getBuilds()) {
                    if (build.getState() == StateType.FAILED) {
                        for (Job job : build.getJobs()) {
                            Optional<Log> logOptional = jTravis.log().from(job);
                            if (logOptional.isPresent()) {
                                Commit commit = build.getCommit();
                                Log log = logOptional.get();
                                TestsInformation testsInformation = log.getTestsInformation();
                                if (testsInformation != null) {
                                    FailedBuildDAO.insertFailedBuild(new FailedBuild(Integer.parseInt(build.getNumber()), testsInformation.getFailing(), testsInformation.getErrored(),
                                            commit.getCompareUrl(), this,commit.getSha()));
                                    System.out.println("Inserting failed build #"+build.getNumber());
                                }
                            }
                        }
                    }
                }
                optionalBuilds = jTravis.build().next(optionalBuilds.get());
            }
            System.out.println("Finished processing failed builds.");
        } else {
            System.out.println("Project not found");
        }
    }

    private String getProjectSlug() {
        return this.organization + "/" + this.projectName;
    }

}
