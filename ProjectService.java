package projects.exception;

public interface ProjectService {

	Project fetchProjectById(Integer projectId);

	List<Project> fetchAllProjects();

	Project addProject(Project project);


package projects.exception;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

import java.util.List;
import java.util.NoSuchElementException;

public class ProjectsService {
    
    private ProjectDao projectDao = new ProjectDao();
    
    public Project addProject(Project project) {
        return projectDao.insertProject(project); 
    }

    public Project fetchProjectById(Integer projectId) {
        return projectDao.fetchProjectById(projectId) 
                .orElseThrow(() -> new NoSuchElementException("Project with project ID=" + projectId + " does not exist.")); 
    }

    public void modifyProjectDetails(Project project) {
        if (!projectDao.modifyProjectDetails(project)) { 
            throw new DbException("Project with ID=" + project.getProjectName() + " does not exist.");
        }
    }

    public void deleteProject(Integer projectId) { 
        if (!projectDao.deleteProject(projectId)) { 
            throw new DbException("Project with ID=" + projectId + " does not exist.");
        }
    }
}