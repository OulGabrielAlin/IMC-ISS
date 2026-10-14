package org.example;

import org.example.service.PenaltyService;

import java.time.LocalDate;
import java.util.prefs.Preferences;

public class SystemStartupManager {
    private PenaltyService penaltyService;

    public SystemStartupManager(PenaltyService penaltyService) {
        this.penaltyService = penaltyService;
    }

    public void runDailyTasks()
    {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());

        String lastRunStr = prefs.get("LAST_PENALTY_RUN_DATE", "2000-01-01");
        LocalDate lastRunDate = LocalDate.parse(lastRunStr);
        LocalDate currentDate = LocalDate.now();

        if (lastRunDate.isBefore(currentDate)) {
            System.out.println("Automatic penalty generation for today's date is running in background.");
            penaltyService.generateDailyPenalties();

            prefs.put("LAST_PENALTY_RUN_DATE", currentDate.toString());
            System.out.println("The penalties have been successfully generated.");
        } else {
            System.out.println("Penalties have already been processed today.");
        }
    }
}
