package com.gradeapp.util; /**
package com.gradeapp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradeapp.model.Course;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoSave {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void scheduleAutoSave(Course course, String filePath, long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                objectMapper.writeValue(new File(filePath), course);
                System.out.println("Auto-save completed successfully.");
            } catch (IOException e) {
                System.err.println("Auto-save failed: " + e.getMessage());
            }
        }, 0, period, unit);
    }

    public static Course loadFromFile(String filePath) throws IOException {
        return objectMapper.readValue(new File(filePath), Course.class);
    }
}

 */
