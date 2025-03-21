try(Connection conn = DbConnection.getConnection()){
	startTransaction(conn);
	
	try(PreparedStatement stmt = conn.prepareStatement(sql)){
		try(ResultSet rs = stmt.executeQuery()){
			List<Project> projects = new LinkedList<>();
			
			while(rs.next()) {
		    Project project = new Project();

			project.setActualHours(rs.getBigDecimal("actual_hours"));
			project.setDifficulty(rs.getObject("difficulty", Integer.class));
			project.setEstimatedHours(rs.getBigDecimal("estimated_hours"));
			project.setNotes(rs.getString("notes"));
			project.setProjectId(rs.getObject("project_id", Integer.class));
			project.setProjectName(rs.getString("project_name"));

			projects.add(project);
			}
			
			return projects;

		}
	}
	catch (Exception e) {
		rollbackTransaction(conn);
		throw new DbException(e);
	}
}
catch(SQLException e) {
	throw new DbException(e);




public Optional<Project> fetchProjectById(Integer projectId) {
    String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
    try (Connection conn = DbConnection.getConnection()) {
        startTransaction(conn);
        try {
            Project project = null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setParameter(stmt, 1, projectId, Integer.class);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        project = extract(rs, Project.class);
                    }
                }
            }
            commitTransaction(conn);
            return Optional.ofNullable(project);
        } catch (Exception e) {
            throw new DbException(e);
        }
    } catch (SQLException e) {
        throw new DbException(e);
    }
}

private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
    String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        setParameter(stmt, 1, projectId, Integer.class);
        try (ResultSet rs = stmt.executeQuery()) {
            List<Material> materials = new ArrayList<>();
            while (rs.next()) {
                materials.add(extract(rs, Material.class));
            }
            return materials;
        }
    }
}

private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
    String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        setParameter(stmt, 1, projectId, Integer.class);
        try (ResultSet rs = stmt.executeQuery()) {
            List<Step> steps = new LinkedList<>();
            while (rs.next()) {
                steps.add(extract(rs, Step.class));
            }
            return steps;
        }
    }
}
} 




package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import projects.entity.Project;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {
    private static final String CATEGORY_TABLE = "category";
    private static final String MATERIAL_TABLE = "material";
    private static final String PROJECT_TABLE = "project";
    private static final String PROJECT_CATEGORY_TABLE = "project_category";
    private static final String STEP_TABLE = "step";

    public Project insertProject(Project project) {
        // @formatter:off
        String sql = ""
            + "INSERT INTO " + PROJECT_TABLE + " (project_name, estimated_hours, actual_hours, difficulty, notes) "
            + "VALUES (?, ?, ?, ?, ?)";
        // @formatter:on

        try (Connection conn = DbConnection.getConnection()) {
            rollbackTransaction(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                setParameter(stmt, 1, project.getProjectName(), String.class);
                setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
                setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
                setParameter(stmt, 4, project.getDifficulty(), Integer.class);
                setParameter(stmt, 5, project.getNotes(), String.class);

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new DbException("Inserting project failed, no rows affected.");
                }

               
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        project.setProjectName(generatedKeys.getInt(1));
                    } else {
                        throw new DbException("Inserting project failed, no ID obtained.");
                    }
                }

                commitTransaction(conn);
                return project;
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public List<Project> fetchAllProjects() {
        String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";

        try (Connection conn = DbConnection.getConnection()) {
            rollbackTransaction(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    List<Project> projects = new ArrayList<>();

                    while (rs.next()) {
                        Project project = new Project();
                        project.setActualHours(rs.getBigDecimal("actual_hours"));
                        project.setDifficulty(rs.getInt("difficulty"));
                        project.setEstimatedHours(rs.getBigDecimal("estimated_hours"));
                        project.setNotes(rs.getString("notes"));
                        project.setProjectName(rs.getInt("project_id")); // Changed to getInt
                        project.setProjectName(rs.getString("project_name"));
                        projects.add(project);
                    }

                    return projects;
                }
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public Optional<Project> fetchProjectById(Integer projectId) {
        String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
        try (Connection conn = DbConnection.getConnection()) {
            rollbackTransaction(conn);
            try {
                Project project = null;
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    setParameter(stmt, 1, projectId, Integer.class);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            project = extract(rs, Project.class);
                        }
                    }
                }
                commitTransaction(conn);
                return Optional.ofNullable(project);
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    private void setParameter(PreparedStatement stmt, int i, String string, Class<Integer> class1) {
		
		
		
	}

	private void rollbackTransaction(Connection conn) {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException e) {
            throw new DbException("Error rolling back transaction", e);
        }
    }

	public boolean modifyProjectDetails(Project project) {
		
		return false;
	}
	 // @formatter:off
    String sql = ""
        + "UPDATE " + PROJECT_TABLE + " SET "
        + "project_name = ?, "
        + "estimated_hours = ?, "
        + "actual_hours = ?, "
        + "difficulty = ?, "
        + "notes = ? "
        + "WHERE project_id = ?";
    // @formatter:on

    try(Connection conn = DbConnection.getConnection()) {
      rollbackTransaction(conn);

      try(PreparedStatement stmt = conn.prepareStatement(sql)) {
        setParameter(stmt, 1, PROJECT_TABLE.getProjectName(), String.class);
        setParameter(stmt, 2, PROJECT_TABLE.getEstimatedHours(), BigDecimal.class);
        setParameter(stmt, 3, PROJECT_TABLE.getActualHours(), BigDecimal.class);
        setParameter(stmt, 4, PROJECT_TABLE.getDifficulty(), Integer.class);
        setParameter(stmt, 5, PROJECT_TABLE.getNotes(), String.class);
        setParameter(stmt, 6, PROJECT_TABLE.getProjectId(), Integer.class);

        boolean modified = stmt.executeUpdate() == 1;
        commitTransaction(conn);

        return modified;
      }
      catch(Exception e) {
        rollbackTransaction(conn);
        throw new DbException(e);
      }
    }
    catch(SQLException e) {
      throw new DbException(e);
    }
  }

  
  public boolean deleteProject(Integer projectId) {
    String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";

    try(Connection conn = DbConnection.getConnection()) {
      rollbackTransaction(conn);

      try(PreparedStatement stmt = conn.prepareStatement(sql)) {
        setParameter(stmt, 1, projectId, Integer.class);

       
        boolean deleted = stmt.executeUpdate() == 1;

        commitTransaction(conn);
        return deleted;
      }
      catch(Exception e) {
        rollbackTransaction(conn);
        throw new DbException(e);
      }
    }
    catch(SQLException e) {
      throw new DbException(e);
    }
  }

private void commitTransaction(Connection conn2) {
	
	
}

}



