package stores;

import models.ProjectFilter;
import models.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Store class for project filters
 * This class stores filter settings for each user so they persist between menu pages
 */
public class FilterStore {
    private static final Map<String, ProjectFilter> userFilters = new HashMap<>();
    
    /**
     * Get the project filter for a user
     * @param user The user to get the filter for
     * @return The user's project filter
     */
    public static ProjectFilter getProjectFilter(User user) {
        String userId = user.getNric();
        if (!userFilters.containsKey(userId)) {
            userFilters.put(userId, new ProjectFilter()); // Create default filter
        }
        return userFilters.get(userId);
    }
    
    /**
     * Set the project filter for a user
     * @param user The user to set the filter for
     * @param filter The filter to set
     */
    public static void setProjectFilter(User user, ProjectFilter filter) {
        userFilters.put(user.getNric(), filter);
    }
    
    /**
     * Reset the project filter for a user
     * @param user The user to reset the filter for
     */
    public static void resetProjectFilter(User user) {
        ProjectFilter filter = new ProjectFilter();
        userFilters.put(user.getNric(), filter);
    }
}