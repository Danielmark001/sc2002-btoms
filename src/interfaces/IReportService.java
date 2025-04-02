package interfaces;

import java.util.Map;

/**
 * Interface for the Report Service
 * Defines methods for generating reports
 */
public interface IReportService {
    String generateBookingReport(String filter, String value);
}