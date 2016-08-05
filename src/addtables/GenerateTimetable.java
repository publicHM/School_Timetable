/*
 * Helper class that generates new timetable from data in class, course and professor tabels
 *
 * @author Petar Deveric
 */

package addtables;

import general.SetNewStage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GenerateTimetable extends SetNewStage{
    
    private final String timetableName;
    private int lowesYear;
    
    public GenerateTimetable(String timetableName){
        this.timetableName = timetableName;
    }
    
    public void generateNewTimetable(){
        try(Connection conn = DriverManager.getConnection("jdbc:derby:TableDB");){
            
            // Delete timetable if already exist
            deleteOldTimetableIfExists(conn);

            // Copy original Course, Class and Professor tables to temporary tables so they could be 
            // modified during creation of new timetable
            createTemporaryTables(conn);

            // Create new empty tables for professors and classes
            createHoursForProfessorsAndClasses(conn);

            // Create timetables for all classes and professor except first year
            getAllClasses(conn);

        }
        catch(SQLException e){
            System.err.println("SQLException in class GenerateTimetable, method generateNewTimetable()");
            e.printStackTrace(System.out);
        }
    }
    
    // Delete timetable if already exist
    private void deleteOldTimetableIfExists(Connection conn){
        try(PreparedStatement psPT = conn.prepareStatement("DELETE FROM ProfessorsTimetables WHERE Timetable = ?");
            PreparedStatement psCT = conn.prepareStatement("DELETE FROM ClassesTimetables WHERE Timetable = ?");){
            psPT.setString(1, timetableName);
            psPT.executeUpdate();
            psCT.setString(1, timetableName);
            psCT.executeUpdate();
        }
        catch(SQLException e){
            System.err.println("SQLException in class GenerateTimetable, method deleteOldTimetableIfExists()");
            e.printStackTrace(System.out);
        }
    }
    
    // Copy original courses, classes and professors data to temporary tables so they could be 
    // modified during creation of new timetable
    private void createTemporaryTables(Connection conn){
        try(Statement stmt = conn.createStatement()){
            stmt.executeUpdate("DELETE FROM Temporary_AllCourses");
            stmt.executeUpdate("INSERT INTO Temporary_AllCourses SELECT * FROM AllCourses WHERE Timetable = '" + 
                    timetableName + "'");
            stmt.executeUpdate("DELETE FROM Temporary_AllProfessors");
            stmt.executeUpdate("INSERT INTO Temporary_AllProfessors SELECT * FROM AllProfessors WHERE Timetable = '" + 
                    timetableName + "'");
            stmt.executeUpdate("DELETE FROM Temporary_AllClasses");
            stmt.executeUpdate("INSERT INTO Temporary_AllClasses SELECT * FROM AllClasses WHERE Timetable = '" + 
                    timetableName + "'");
        }
        catch(SQLException e){
            System.err.println("SQLException in class GenerateTimetable, method createTemporaryTables()");
            e.printStackTrace(System.out);
        }
    }
    
    // Create hours for for all professors and classes
    private void createHoursForProfessorsAndClasses(Connection conn){
        
        String queryGetProf = "SELECT Professor_Name FROM Temporary_AllProfessors WHERE Timetable = ?";
        String queryInsertHoursProf = "INSERT INTO ProfessorsTimetables (tHour, Professor, Timetable) VALUES(?, ?, ?)";
        String queryGetClasses = "SELECT Class_Label FROM Temporary_AllClasses WHERE Timetable = ?";
        String queryInsertHoursClass = "INSERT INTO ClassesTimetables (tHour, Class_Label, Timetable) VALUES(?, ?, ?)";
        
        try(PreparedStatement psAllProfessors = conn.prepareStatement(queryGetProf);
            PreparedStatement psInsertHoursProf = conn.prepareStatement(queryInsertHoursProf);
            PreparedStatement psAllClasses = conn.prepareStatement(queryGetClasses);
            PreparedStatement psInsertHoursClass = conn.prepareStatement(queryInsertHoursClass);){           
            
            psAllProfessors.setString(1, timetableName);
            // Create hours for all professors
            try(ResultSet rsProf = psAllProfessors.executeQuery();){
                while(rsProf.next()){
                    String professorName = rsProf.getString(1);
                    for(int k=1; k <=7;k++){
                        psInsertHoursProf.setInt(1, k);
                        psInsertHoursProf.setString(2, professorName);
                        psInsertHoursProf.setString(3, timetableName);
                        psInsertHoursProf.executeUpdate();
                    }
                }
            }
            
            psAllClasses.setString(1, timetableName);
            // Create hours for all classes
            try(ResultSet rsClasses = psAllClasses.executeQuery();){
                while(rsClasses.next()){
                    String classLabel = rsClasses.getString(1);
                    for(int k=1; k <=7;k++){
                        psInsertHoursClass.setInt(1, k);
                        psInsertHoursClass.setString(2, classLabel);
                        psInsertHoursClass.setString(3, timetableName);
                        psInsertHoursClass.executeUpdate();
                    }
                }
            }
        }
        catch(SQLException e){
            System.err.println("SQLException in class GenerateTimetable, method createHoursForProfessorsAndClasses()");
            e.printStackTrace(System.out);
        }
    }
    
    // Create timetables for all classes and professor
    // Step 1. Get all classes and iterate over ResultSet of classes
    private void getAllClasses(Connection conn){
         try(Statement stmt = conn.createStatement();
            ResultSet rsClasses = stmt.executeQuery("SELECT Class_Label, Shift FROM Temporary_AllClasses " +
                    "WHERE Timetable = '" + timetableName + "' ORDER BY Class_Label DESC");){
             
            while(rsClasses.next()){
                String classLabel = rsClasses.getString(1);
                int shift = rsClasses.getInt(2);
                
                // Extract year of education from CLASS_LABEL (class name)
                String yearOnly = classLabel.replaceAll("[^0-9]", "");
                int classYear = Integer.parseInt(yearOnly);
                System.out.println("1. getAllClasses() - classLabel: " + classLabel + ", classYear: "
                        + classYear + ", shift: " + shift);
                // Get one-by-one course for appending year of education and asign to current class
                coursesForTheYear(conn, classLabel, classYear, shift);
            }
        }
        catch(SQLException e){
            System.err.println("SQLException in class GenerateTimetable, method getAllClasses()");
            e.printStackTrace(System.out);
        }
    }
    
    // Create timetables for all classes and professor except first year
    // Step 2. Get all courses for appending class and iterate over them
    private void coursesForTheYear(Connection conn, String classLabel, int classYear, int shift){
        try (Statement stmt = conn.createStatement();
            //Get all courses for appanding year of education
            ResultSet rsCourse = stmt.executeQuery("SELECT Course_List, Lessons_per_Week FROM Temporary_AllCourses "
                    + "WHERE Year_of_Eucation = " + classYear + " AND Timetable = '" + timetableName + 
                    "' ORDER BY Lessons_per_Week DESC");){
                while(rsCourse.next()){
                    String currentCourse = rsCourse.getString(1);
                    int currentLessonsPerWeek = rsCourse.getInt(2);
                    System.out.println("2. coursesForTheYear() - currentCourse: " + currentCourse + ", currentLessonsPerWeek: "
                            + currentLessonsPerWeek);
                    // Get professor for appending course and class
                    professorForCourse(conn, classLabel, currentCourse, currentLessonsPerWeek, shift);
                }
            }
        catch(SQLException e){
            System.err.println("SQLException in class GenerateTimetable, method coursesForTheYear()");
            e.printStackTrace(System.out);
        }
    }
    
    // Create timetables for all classes and professor
    // Step 3. Get professor for appending course
    private void professorForCourse(Connection conn, String classLabel, String currentCourse, int currentLessonsPerWeek,
            int shift){
        try(Statement stmt = conn.createStatement();
            // Get professor for appanding course and class
            ResultSet rsProfessor = stmt.executeQuery("SELECT Professor_Name, Max_Hours FROM "
                    + "Temporary_AllProfessors WHERE Course LIKE '%" + currentCourse + "%' AND "
                    + "Class_Label LIKE '%" + classLabel + "%' AND Timetable = '" + timetableName + "'");){

            // 3.1. Check if any professor is already assigned to the class and course. Do appropirate logic.
            if(!rsProfessor.next()){
                rsProfessor.close();
                System.out.println("3.1. There is no assigned professor");
            //  Call method for case when there is no assigned proffesor
                randomProfessorForCourse(conn, classLabel, currentCourse, currentLessonsPerWeek, shift);
            }
            // Procede if there is assigned professor
            else{
                String currentProfessor = rsProfessor.getString(1);
                int currentMaxHours = rsProfessor.getInt(2);
                rsProfessor.close();
                fillTimetable(conn, classLabel, currentCourse, currentLessonsPerWeek, shift, currentProfessor, 
                        currentMaxHours);
            }
        }
        catch(SQLException e){
            System.err.println("SQLException in class GenerateTimetable, method professorForCourse()");
            e.printStackTrace(System.out);
        }
    }
                
    // 4. Find matching empty term for professors and classes and assign them to eachother
    private void fillTimetable(Connection conn, String classLabel, String currentCourse, int currentLessonsPerWeek,
            int shift, String currentProfessor,int currentMaxHours ){        
                
        System.out.println("3.1. Assigned Professor: " + currentProfessor + ", currentMaxHours: " + currentMaxHours);
        List<String> daysInWeek = new ArrayList(Arrays.asList("Monday", "Tuesday", "Wednesday", 
                "Thursday", "Friday"));

        int daysForChecking = 5;

        String availableDays = "SELECT Available_Days FROM Temporary_AllProfessors WHERE Professor_Name = '" 
                + currentProfessor + "' AND Available_Days LIKE ? AND Timetable = '" + timetableName + "'";
        String updateTempAllProfessors = "UPDATE Temporary_AllProfessors SET Max_Hours = ? WHERE Professor_Name = '" 
                + currentProfessor + "' AND Timetable = '" + timetableName + "'";
        try(PreparedStatement psDefaultAvailability = conn.prepareStatement(availableDays);
            PreparedStatement psUpdateTempAllProfessors = conn.prepareStatement(updateTempAllProfessors)){
            // Find empty cells in timetable and write in current professor and class
            outer: while(true){
                // 3.2. Get random randomDay
                String randomDay = daysInWeek.get(new Random().nextInt(daysForChecking));
                System.out.println("3.2 new randomDay: " + randomDay);

                // 3.3. Chech if in Professors table is defined that the professor can work at random randomDay
                psDefaultAvailability.setString(1, "%" + randomDay + "%");
                try(ResultSet rsDefaultAvailability = psDefaultAvailability.executeQuery();){

                    // 3.3. Do if in Professors table is defined that the professor can work at random randomDay
                    if(rsDefaultAvailability.next()){
                        System.out.println("3.3. Professor can work on random day");

                        String profAndClassTermEmpty = "SELECT pt.tHour FROM ProfessorsTimetables AS pt "
                        + "JOIN ClassesTimetables AS ct ON pt.tHour = ct.tHour AND pt.Timetable = ct.Timetable"
                        + " WHERE pt." + randomDay + " IS NULL AND ct." + randomDay + " IS NULL AND pt.Professor = '"
                        + currentProfessor + "' AND ct.Class_Label = '" + classLabel + "' AND pt.Timetable = '" 
                        + timetableName +"'";
                        try(PreparedStatement psProfAndClassTermEmpty = conn.prepareStatement(profAndClassTermEmpty);
                            ResultSet rsProfAndClassTermEmpty = psProfAndClassTermEmpty.executeQuery();){
                            // Insert values in professor and class table if class cell is also empty
                            if(rsProfAndClassTermEmpty.next()){
                                String updateProfessors = "UPDATE ProfessorsTimetables SET " + randomDay + " = '" 
                                        + classLabel + "' WHERE  tHour = ? AND Professor = '" + currentProfessor
                                        + "' AND  Timetable = '" + timetableName + "'";
                                String updateClasses = "UPDATE ClassesTimetables SET " + randomDay + " = '"
                                        + currentCourse + "' WHERE  tHour = ? AND Class_Label = '" + classLabel 
                                        + "' AND  Timetable = '" + timetableName + "'";
                                try(PreparedStatement psUpdateProfessors = conn.prepareStatement(updateProfessors);
                                    PreparedStatement psUpdateClasses = conn.prepareStatement(updateClasses);){
                                    int k = rsProfAndClassTermEmpty.getInt(1);
                                    System.out.println("3.5 Cell is empty for class at k = " + k + " hour.");
                                    psUpdateProfessors.setInt(1, k);
                                    psUpdateProfessors.executeUpdate();
                                    psUpdateClasses.setInt(1, k);
                                    psUpdateClasses.executeUpdate();
                                    System.out.println("3.6. Values are inserted in Professors and Classes timetables");}
                                // Decrease by one numbur of lessons per week that should be implemented and
                                // and professors maximum working hours
                                currentLessonsPerWeek--;
                                currentMaxHours--;

                                psUpdateTempAllProfessors.setInt(1, currentMaxHours);
                                psUpdateTempAllProfessors.executeUpdate();
                                System.out.println("3.7. currentLessonsPerWeek: " + currentLessonsPerWeek 
                                        + ", currentMaxHours: " + currentMaxHours);
                            }
                        }
                    }
                }

                System.out.println("3.8. currentLessonsPerWeek = " + currentLessonsPerWeek);
                // Break if timetable is populated with right amount of weekly lessons
                if(currentLessonsPerWeek == 0) break;

                daysForChecking--;
                daysInWeek.remove(randomDay);
                System.out.println("3.9. Remove daysForChecking = " + daysForChecking + " and randomDay: " + randomDay);

                // Call shiftClasses() if there was not enough empty matching terms
                if(daysForChecking==0){
                    for(int k= 1; k <= currentLessonsPerWeek; k++){
                        System.out.println("3.10. shiftClasses() is called");
                        shiftClasses(conn, classLabel, currentProfessor, currentCourse, currentMaxHours);
                        if( k == currentLessonsPerWeek) break outer;
                    }
                }
            }
        }
        catch(SQLException e){
            System.err.println("SQLException in class GenerateTimetable, method fillTimetable()");
            e.printStackTrace(System.out);
        }
    }
    
    // Get professor for appending course, on a random base
    private void randomProfessorForCourse(Connection conn, String classLabel, String currentCourse, int currentLessonsPerWeek,
            int shift){
        System.out.println("4. randomProfessorForCourse() started");
        String getProfessors = "SELECT Professor_Name, Max_Hours FROM Temporary_AllProfessors WHERE Course LIKE '%" 
                + currentCourse + "%' AND Max_Hours >= " + currentLessonsPerWeek +  " AND Shift = " + shift
                + " AND Timetable = '" + timetableName + "'";
        try(PreparedStatement psGetProfessors = conn.prepareStatement(getProfessors);
            ResultSet rsProfessor = psGetProfessors.executeQuery();){
            if(rsProfessor.next()){
                String currentProfessor = rsProfessor.getString(1);
                int currentMaxHours = rsProfessor.getInt(2);
                rsProfessor.close();
                fillTimetable(conn, classLabel, currentCourse, currentLessonsPerWeek, shift, currentProfessor, 
                        currentMaxHours);
            }
        }
        catch(SQLException e){
            System.err.println("SQLException in class GenerateTimetable, method randomProfessorForCourse()");
            e.printStackTrace(System.out);
        }
    }
    
    // 5. If there is no empty term for matching professor and class, switch terms with some another professor
    private void shiftClasses(Connection conn, String classLabel, String currentProfessor, String currentCourse, 
            int currentMaxHours){
        List<String> daysInWeek = new ArrayList(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", 
                        "Friday"));
        int daysLeft = 5;

        String availableDays = "SELECT Available_Days FROM Temporary_AllProfessors WHERE Professor_Name = '" 
            + currentProfessor + "' AND Available_Days LIKE ? AND Timetable = '" + timetableName + "'";            
        try(PreparedStatement psDefaultAvailability = conn.prepareStatement(availableDays);){
        // 5.1. Iterate over random days until condition is met
        outer : while(true){
            String randomDay = daysInWeek.get(new Random().nextInt(daysLeft));
            System.out.println("5.1. Outer random day: " + randomDay);

            // 5.2. Chech if in AllProfessors table is defined professor can work at random randomDay
            psDefaultAvailability.setString(1, "%" + randomDay + "%");
            try(ResultSet rsDefaultAvailability = psDefaultAvailability.executeQuery();){

                if(rsDefaultAvailability.next()){
                    System.out.println("5.2. Professor can work on day: " + randomDay);

                    String emptyTerms = "SELECT tHour FROM ProfessorsTimetables WHERE Professor = '" + currentProfessor
                            + "' AND " + randomDay + " IS NULL AND Timetable = '" + timetableName + "'";
                    // 5.3. Get all empty terms on the randomDay and iterate until maching is done
                    try(PreparedStatement psEmtyTerms = conn.prepareStatement(emptyTerms);
                        ResultSet rsEmtyTerms = psEmtyTerms.executeQuery();){

                        while(rsEmtyTerms.next()){
                            int availableHour = rsEmtyTerms.getInt(1);
                            System.out.println("5.3. Founded available hour: " + availableHour);

                            // 5.4. Get professor and lesson to switch it to another term
                            String lessonToSwitch = "SELECT " + randomDay + " FROM ClassesTimetables WHERE tHour = "
                                    + availableHour + " AND Class_Label = '" + classLabel + "' AND Timetable = '" 
                                    + timetableName + "'";

                            try(PreparedStatement psLessonToSwitch = conn.prepareStatement(lessonToSwitch);
                                ResultSet rsLessonToSwitch = psLessonToSwitch.executeQuery();){
                                rsLessonToSwitch.next();
                                String switchingLesson = rsLessonToSwitch.getString(1);

                                String professorToSwitch = "SELECT Professor FROM ProfessorsTimetables WHERE tHour = "
                                    + availableHour + " AND " + randomDay + " = '" + classLabel + "' AND Timetable = '" 
                                    + timetableName + "'";

                                try(PreparedStatement psProfessorToSwitch = conn.prepareStatement(professorToSwitch);
                                ResultSet rsProfessorToSwitch = psProfessorToSwitch.executeQuery();){
                                    rsProfessorToSwitch.next();
                                    String switchingProfessor = rsProfessorToSwitch.getString(1);

                                    System.out.println("5.4. switchingLesson: " + switchingLesson + ", "
                                            + "switchingProfessor: " + switchingProfessor);

                                    // 5.5. Get random days to switch lessons
                                    List<String> daysInWeekForSwitching = new ArrayList(Arrays.asList("Monday", 
                                            "Tuesday", "Wednesday", "Thursday", "Friday"));
                                    int daysLeftForSwitching = 5;

                                    // Iterate over random days to find out empty term for professor and class
                                    while(true){
                                        String randomDayForSwitching = daysInWeekForSwitching.get(new Random()
                                                .nextInt(daysLeftForSwitching));
                                        System.out.println("5.5. Random day for switching: " + randomDayForSwitching);
                                        String profAndClassTermEmpty = "SELECT pt.tHour FROM ProfessorsTimetables AS pt "
                                                + "JOIN ClassesTimetables AS ct ON pt.tHour = ct.tHour AND "
                                                + "pt.Timetable = ct.Timetable WHERE pt." + randomDayForSwitching
                                                + " IS NULL AND ct." + randomDayForSwitching + " IS NULL AND "
                                                + "pt.Professor = '" + switchingProfessor + "' AND ct.Class_Label = '" 
                                                + classLabel + "' AND pt.Timetable = '" + timetableName + "'";

                                        try(PreparedStatement psProfAndClassTermEmpty = 
                                                conn.prepareStatement(profAndClassTermEmpty);
                                            ResultSet rsProfAndClassTermEmpty = psProfAndClassTermEmpty.executeQuery();){

                                            if(rsProfAndClassTermEmpty.next()){
                                                int switchAtHour = rsProfAndClassTermEmpty.getInt(1);
                                                System.out.println("5.6. There is empty term");
                                                
                                                try(Statement stmt = conn.createStatement();){
                                                stmt.executeUpdate("UPDATE ClassesTimetables SET " + randomDayForSwitching 
                                                        + " = '" + switchingLesson + "' WHERE tHour = " + switchAtHour
                                                        + " AND Class_Label = '" + classLabel + "' AND " + "Timetable = '" 
                                                        + timetableName + "'");

                                                stmt.executeUpdate("UPDATE ProfessorsTimetables SET " + randomDayForSwitching
                                                        + " = '" + classLabel + "' WHERE tHour = " + switchAtHour
                                                        + " AND Professor = '" + switchingProfessor + "' AND Timetable = '" 
                                                        + timetableName + "'");

                                                stmt.executeUpdate("UPDATE ClassesTimetables SET " + randomDay + " = '"
                                                        + currentCourse + "' WHERE tHour = " + availableHour 
                                                        + " AND Class_Label = '" + classLabel + "' AND " + "Timetable = '"
                                                        + timetableName + "'");

                                                stmt.executeUpdate("UPDATE ProfessorsTimetables SET " + randomDay + " = '" 
                                                        + classLabel + "' WHERE tHour = " + availableHour
                                                        + " AND Professor = '" + currentProfessor + "' AND Timetable = '" 
                                                        + timetableName + "'");
                                                    System.out.println("5.7. Tabels updated");
                                                currentMaxHours--;
                                                stmt.executeUpdate("UPDATE Temporary_AllProfessors SET Max_Hours = " + 
                                                        currentMaxHours + " WHERE Professor_Name = '" + currentProfessor 
                                                        + "' AND Timetable = '" + timetableName + "'");
                                                //  5.8. Break all loops and finish this method
                                                System.out.println("5.8. Break all and finish method");
                                                break outer;
                                                }
                                            }
                                        }

                                    // 5.9. Remove checked day for switching, break if all days are checked
                                    daysLeftForSwitching--;
                                    System.out.println("5.9. remove day: " + randomDayForSwitching 
                                            + ", daysLeftForSwitching: " + daysLeftForSwitching);
                                    if(daysLeftForSwitching == 0){
                                        System.out.println("5.9. break");
                                        break;
                                    }
                                    daysInWeekForSwitching.remove(randomDayForSwitching);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("5.10. day removed");
            // 5.10. Remove checked randomDay from the list
            daysLeft--;
            // 5.10. Break if there is no days left
            if(daysLeft == 0){
                System.out.println("5.10 Break");
                break;
            }
            daysInWeek.remove(randomDay);
        }
        }
        catch(SQLException e){
            System.err.println("SQLException in class GenerateTimetable, method shiftClasses()");
            e.printStackTrace(System.out);
        }
    }
}
