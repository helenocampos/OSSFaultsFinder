/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.heleno.faultsFinder.domain;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Heleno
 */
@Entity
@Table(name = "failedbuild")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FailedBuild.findAll", query = "SELECT f FROM FailedBuild f")
    , @NamedQuery(name = "FailedBuild.findByIdFailedBuild", query = "SELECT f FROM FailedBuild f WHERE f.idFailedBuild = :idFailedBuild")
    , @NamedQuery(name = "FailedBuild.findByBuildNumber", query = "SELECT f FROM FailedBuild f WHERE f.buildNumber = :buildNumber")
    , @NamedQuery(name = "FailedBuild.findByFailedAmount", query = "SELECT f FROM FailedBuild f WHERE f.failedAmount = :failedAmount")
    , @NamedQuery(name = "FailedBuild.findByErroredAmount", query = "SELECT f FROM FailedBuild f WHERE f.erroredAmount = :erroredAmount")
    , @NamedQuery(name = "FailedBuild.findByPullRequestURL", query = "SELECT f FROM FailedBuild f WHERE f.pullRequestURL = :pullRequestURL")})
public class FailedBuild implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idFailedBuild")
    private Integer idFailedBuild;
    @Column(name = "buildNumber")
    private Integer buildNumber;
    @Column(name = "failedAmount")
    private Integer failedAmount;
    @Column(name = "erroredAmount")
    private Integer erroredAmount;
    @Column(name = "pullRequestURL")
    private String pullRequestURL;
    @Column(name = "sha")
    private String sha;
    @Column(name="faillingModule")
    private String faillingModule;
    @Column(name="jobNumber")
    private Integer jobNumber;
    @Column(name="jobId")
    private BigInteger jobId;
    /**
     * Get the value of sha
     *
     * @return the value of sha
     */
    public String getSha() {
        return sha;
    }

    /**
     * Set the value of sha
     *
     * @param sha new value of sha
     */
    public void setSha(String sha) {
        this.sha = sha;
    }

    @JoinColumn(name = "project_idProject", referencedColumnName = "idProject")
    @ManyToOne(optional = false)
    private Project projectidProject;

    public FailedBuild() {
    }

    public FailedBuild(Integer buildNumber, Integer failedAmount, Integer erroredAmount, String pullRequestURL, Project projectidProject, String sha, String faillingModule, Integer jobNumber, BigInteger jobId) {
        this.buildNumber = buildNumber;
        this.failedAmount = failedAmount;
        this.erroredAmount = erroredAmount;
        this.pullRequestURL = pullRequestURL;
        this.projectidProject = projectidProject;
        this.sha = sha;
        this.faillingModule = faillingModule;
        this.jobNumber = jobNumber;
        this.jobId = jobId;
    }

    public FailedBuild(Integer idFailedBuild) {
        this.idFailedBuild = idFailedBuild;
    }

    public Integer getIdFailedBuild() {
        return idFailedBuild;
    }

    public void setIdFailedBuild(Integer idFailedBuild) {
        this.idFailedBuild = idFailedBuild;
    }

    public Integer getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(Integer buildNumber) {
        this.buildNumber = buildNumber;
    }

    public Integer getFailedAmount() {
        return failedAmount;
    }

    public void setFailedAmount(Integer failedAmount) {
        this.failedAmount = failedAmount;
    }

    public Integer getErroredAmount() {
        return erroredAmount;
    }

    public void setErroredAmount(Integer erroredAmount) {
        this.erroredAmount = erroredAmount;
    }

    public String getPullRequestURL() {
        return pullRequestURL;
    }

    public void setPullRequestURL(String pullRequestURL) {
        this.pullRequestURL = pullRequestURL;
    }

    public Project getProjectidProject() {
        return projectidProject;
    }

    public void setProjectidProject(Project projectidProject) {
        this.projectidProject = projectidProject;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idFailedBuild != null ? idFailedBuild.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FailedBuild)) {
            return false;
        }
        FailedBuild other = (FailedBuild) object;
        if ((this.idFailedBuild == null && other.idFailedBuild != null) || (this.idFailedBuild != null && !this.idFailedBuild.equals(other.idFailedBuild))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "info.heleno.faultsFinder.domain.FailedBuild[ idFailedBuild=" + idFailedBuild + " ]";
    }

    public void downloadProjectFolder(String organization, String projectName) {
        StringBuilder urlBuilder = new StringBuilder("https://github.com/");
        urlBuilder.append(organization);
        urlBuilder.append("/");
        urlBuilder.append(projectName);
        urlBuilder.append("/archive/");
        urlBuilder.append(this.getSha());
        urlBuilder.append(".zip");

        try {
//            URL url = new URL(urlBuilder.toString());

            System.out.println("Requesting download for build number" + this.getBuildNumber());
//            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            File currentFolder = new File(System.getProperty("user.dir"));
            Path zippedFolderPath = Paths.get(currentFolder.getAbsolutePath(), "-"+this.buildNumber + ".zip");
            boolean downloaded = download(urlBuilder.toString(), zippedFolderPath.toString(), 5);
//            FileOutputStream fos = new FileOutputStream(zippedFolderPath.toString());
//            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
//            fos.close();
//            rbc.close();
            if (downloaded) {
                System.out.println("Download completed for build number" + this.getBuildNumber());
                File projectFolder = createProjectFolder(currentFolder.getAbsolutePath(), projectName);
                File buildFolder = createBuildDir(projectFolder.getAbsolutePath(), projectName);
                extractSubDir(zippedFolderPath, buildFolder.toPath(), projectName);
                Files.deleteIfExists(zippedFolderPath);
            } else {
                System.out.println("****************Download NOT completed for build number" + this.getBuildNumber());
            }

        } catch (IOException e) {
            System.out.println("");
        }
    }

    private boolean download(String remotePath, String localPath, int tryAmount) {
        BufferedInputStream in = null;
        FileOutputStream out = null;
        boolean downloaded = false;
        try {
            int tryNumber = 1;

            do {
                URL url = new URL(remotePath);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
System.setProperty("sun.net.client.defaultReadTimeout", "10000");
                int size = conn.getContentLength();

                if (size < 0) {
                    System.out.println("Could not get the file size. Trying again...");
                    TimeUnit.SECONDS.sleep(5);
                    tryNumber++;
                    continue;
                } else {
                    System.out.println("File size: " + size);
                }

                in = new BufferedInputStream(url.openStream());
                out = new FileOutputStream(localPath);
                byte data[] = new byte[1024];
                int count;
                double sumCount = 0.0;
                int x=10;
                while ((count = in.read(data, 0, 1024)) != -1) {
                    out.write(data, 0, count);
                    sumCount += count;
                    double percentage = (sumCount / size) * 100.0;
                    
                    if (percentage >= x) {
                        System.out.println("Percentage: " + percentage + "%");
                        x+=10;
                    }
                }
                downloaded = true;
            } while (!downloaded && tryNumber <= tryAmount);

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch(SocketTimeoutException e){
            if(tryAmount>0){
                download(remotePath,localPath,tryAmount--);
            }
        }
        catch (IOException e2) {
            e2.printStackTrace();
        } catch (InterruptedException ex) {
            Logger.getLogger(FailedBuild.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            return downloaded;
        }
    }

    private File createBuildDir(String rootFolder, String projectName) {
        File buildDir = new File(rootFolder, projectName + this.buildNumber);
        if (!buildDir.exists()) {
            buildDir.mkdir();
        }
        return buildDir;
    }

    private File createProjectFolder(String rootFolder, String projectName) {
        File projectFolder = new File(rootFolder, projectName);
        if (!projectFolder.exists()) {
            projectFolder.mkdir();
        }
        return projectFolder;
    }

    private void extractSubDir(Path zipFileUri, Path targetDir, String projectName) {
        try {
            FileSystem zipFs = FileSystems.newFileSystem(zipFileUri, null);
            String extractFolder = "/" + projectName + "-" + this.sha;
            Path pathInZip = zipFs.getPath(extractFolder);

            Files.walkFileTree(pathInZip, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
                    // Make sure that we conserve the hierachy of files and folders inside the zip
                    Path relativePathInZip = pathInZip.relativize(filePath);
                    Path targetPath = targetDir.resolve(relativePathInZip.toString());
                    Files.createDirectories(targetPath.getParent());

                    // And extract the file
                    if (!targetPath.toFile().exists()) {
                        Files.copy(filePath, targetPath);
                    }

                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException ex) {
            Logger.getLogger(FailedBuild.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getFaillingModule()
    {
        return faillingModule;
    }

    public void setFaillingModule(String faillingModule)
    {
        this.faillingModule = faillingModule;
    }

    public Integer getJobNumber()
    {
        return jobNumber;
    }

    public void setJobNumber(Integer jobNumber)
    {
        this.jobNumber = jobNumber;
    }

    public BigInteger getJobId()
    {
        return jobId;
    }

    public void setJobId(BigInteger jobId)
    {
        this.jobId = jobId;
    }

}
