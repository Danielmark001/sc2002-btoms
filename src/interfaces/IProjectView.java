package interfaces;

import models.BTOProject;

/**
 * The {@link IProjectView} interface defines a contract for displaying project
 * information.
 */
public interface IProjectView {
	/**
	 * Displays information about the given project.
	 *
	 * @param project the {@link BTOProject} object whose information should be
	 *                displayed
	 */
	public void displayProjectInfo(BTOProject project);
}