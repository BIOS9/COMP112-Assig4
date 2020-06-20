// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP112 - 2018T1, Assignment 4
* Name: Matthew Corfiatis
* Username: CorfiaMatt
* ID: 300447277
*/
import ecs100.*;

import java.awt.*;
import java.util.*;
import java.io.*;

public class ClassTimes {
    private static FontMetrics fMetrics;

    static final double TIMETABLE_WIDTH = 700;
    static final double TIMETABLE_HEIGHT = 600;
    static final double TIMETABLE_TOP = 50;
    static final double TIMETABLE_LEFT = 50;

    /**
     * Class to parse and store information about a single vic class represented by one line in the classdata file
     */
    public class uniClass
    {
        public String Course;
        public String Type;
        public String Day;
        public int StartTime;
        public int EndTime;
        public String Room;

        public uniClass(String entry) //Parses line from classdata
        {
            Scanner sc = new Scanner(entry);
            Course = sc.next();
            Type = sc.next();
            Day = sc.next();
            StartTime = Integer.parseInt(sc.next());
            EndTime = Integer.parseInt(sc.next());
            Room = sc.next();
        }

        /**
         * prints all data for the course except data specified to be skipped
         * @param skip Any data name in this string will not be printed
         */
        public void print(String skip)
        {
            skip = skip.toLowerCase();
            if(!skip.contains("course")) UI.print(Course + "\t");
            if(!skip.contains("type")) UI.print(String.format("%-15s", Type));
            if(!skip.contains("day")) UI.print(Day + "\t");
            if(!skip.contains("start")) UI.print(StartTime + "\t");
            if(!skip.contains("end")) UI.print(EndTime + "\t");
            if(!skip.contains("room")) UI.print(Room + "\t");
            UI.println();
        }
    }

    public ClassTimes()
    {
        UI.println("Generating hash table...");
        HashManager.GenerateHashTable(FILE_NAME); //Generate lookup hash table when the program starts
        UI.println("Hash table generated!");
    }

    static final String FILE_NAME = "classdata.txt";

    private static  Scanner openData() //Quick helper method to open a scanner and handle any errors
    {
        try
        {
            File file = new File(FILE_NAME);
            return new Scanner(file);
        }
        catch (IOException ex)
        {
            UI.printf("There was an error opening the file '%s' - %s%n", FILE_NAME, ex);
            return new Scanner("");
        }
    }

    private static  FileInputStream openFileStream()  //Quick helper method to open a file stream and handle any errors
    {
        try
        {
            File file = new File(FILE_NAME);
            return new FileInputStream(file);
        }
        catch (IOException ex)
        {
            UI.printf("There was an error opening the file '%s' - %s%n", FILE_NAME, ex);
            return null;
        }
    }

    /**
     * Reads the class timetable file, printing out each line.
     * This method is very straightforward, and there are very similar
     * examples in the lecture notes.
     */
    public void printAll() {
        try {
            File myFile = new File("classdata.txt");
            Scanner scan = new Scanner(myFile);
            while(scan.hasNextLine()) {
                UI.println(scan.nextLine());
            }
        }

        catch(IOException e) {UI.printf("File Failure %s \n", e);}
        UI.println("=========================");
    }


    /** Core 1
     * Read the class timetable file, printing out (to the text pane)
     * the class type, day, start time, end time, and room
     * for each class with the target course.
     * Print a message if there are no classes for the course.
     */
    public void doPrintCourse(){
        String course = UI.askString("Enter course code (eg ACCY111):").toUpperCase();
        UI.clearText();
        this.printCourse(course);
    }

    public void printCourse(String targetCourse){
        UI.clearText();
        printCourseFast(targetCourse);
        printCourseSlow(targetCourse);
    }

    /**
     * Finds all entries in the file that match the specified room.
     * Uses fast hash table based search.
     * @param targetCourse course to find entries for
     */
    public void printCourseFast(String targetCourse)
    {
        FileInputStream fs = openFileStream(); //Get file stream for classdata
        UI.println("\nClasses for course (Fast search) " + targetCourse);
        UI.println("=========================");
        long t1 = System.nanoTime(); //Start a timer to measure the time the action takes
        ArrayList<String> lines = HashManager.GetByIndex(targetCourse, 0, fs); //Get all lines matching search query
        long time = System.nanoTime() - t1; //Measure time for operation to take
        if(lines.size() == 0)
            UI.println("This course has no class times!");
        else
            for(String ln : lines)
                new uniClass(ln).print("course");
        UI.println(lines.size() + " results returned.");
        UI.printf("Query completed in: %.2fms%n", time / 1000000f); //Print nano time in Milliseconds
        UI.println("=========================");
        try { fs.close(); } catch(Exception ex){} //Attempt to close the file stream.
    }

    /**
     * Finds all entries in the file that match the specified room.
     * Uses slower "line by line" search
     * @param targetCourse course to find entries for
     */
    public void printCourseSlow(String targetCourse)
    {
        UI.println("\nClasses for course (Slow search) " + targetCourse);
        UI.println("=========================");
        long t1 = System.nanoTime();
        ArrayList<String> lines = new ArrayList<>();
        Scanner sc = openData();
        while(sc.hasNextLine()) { //Read through every line
            String line = sc.nextLine(); //Grab the line
            Scanner sc1 = new Scanner(line);
            if(sc1.next().equalsIgnoreCase(targetCourse)) //Check if the first token in the line matches the target course
                lines.add(line);
        }
        long time = System.nanoTime() - t1;
        if(lines.size() == 0)
            UI.println("This course has no class times!");
        else
            for(String ln : lines)
                new uniClass(ln).print("course");
        UI.println(lines.size() + " results returned.");
        UI.printf("Query completed in: %.2fms%n", time / 1000000f);
        UI.println("=========================");
    }

    /** Core 2
     * Print out the name of the target room, and underline it.
     * Then read the class timetable file, printing out (to the text pane)
     *  the course code, class type, day, start time, end time
     *  for each class in the target room.
     * It will be best to read the six tokens on each line individually.
     */
    public void doPrintRoom() {
        String room = UI.askString("Enter room code (eg AM102):").toUpperCase();
        UI.clearText();
        this.printRoom(room);
    }

    public void printRoom(String targetRoom) {
        FileInputStream fs = openFileStream(); //Get file stream for classdata
        UI.println("Classes in " + targetRoom);
        UI.println("=======================");
        long t1 = System.nanoTime();
        ArrayList<String> lines = HashManager.GetByIndex(targetRoom, 5, fs); //Get lines in classdata that match the search query
        long time = System.nanoTime() - t1;
        if(lines.size() == 0)
            UI.println("No courses are booked in " + targetRoom);
        else {
            for (String ln : lines)
                new uniClass(ln).print("room"); //Parse the line and print everything except the room
            UI.println("There are " + lines.size() + " classes in " + targetRoom);
        }
        UI.printf("Query completed in: %.2fms%n", time / 1000000f);
        UI.println("=========================");
        try { fs.close(); } catch(Exception ex){}
    }

    /** Core 3 
     * Prints out the start time and underlines it, then
     * Reads the class timetable file, printing out (to the text pane)
     * the course code, class type, room, day and end time for
     * each class that starts at the target time.
     * It will be best to read the six tokens on each line individually.
     */
    public void doPrintAtStartTime() {
        int time = UI.askInt("Enter start time (eg 900):");
        UI.clearText();
        this.printAtStartTime(time);
    }

    public void printAtStartTime(int startTime) {
        FileInputStream fs = openFileStream();
        String formattedTime = String.format("%4s", startTime).replace(' ', '0');
        UI.println("\nClasses starting at " + formattedTime);
        UI.println("============================");
        long t1 = System.nanoTime();
        ArrayList<String> lines = HashManager.GetByIndex(formattedTime, 3, fs);
        long time = System.nanoTime() - t1;
        if(lines.size() == 0)
            UI.println("No courses start at " + String.valueOf(startTime));
        else
            for(String ln : lines)
                new uniClass(ln).print("start");
        UI.println(lines.size() + " results returned.");
        UI.printf("Query completed in: %.2fms%n", time / 1000000f);
        UI.println("=========================");
        try { fs.close(); } catch(Exception ex){}
    }

    /** Core 4
     * Prints a title containing its arguments, and then
     * Reads the class timetable file, printing out (to the text pane)
     * the course code, class type, day, start and end time
     * for each class that is in targetRoom1 or targetRoom2 and is on targetDay
     * It will be best to read the six tokens on each line individually.
     */
    public void doPrintInRoomsOnDay(){
        String room1 = UI.askString("Enter first room code (eg AM102):").toUpperCase();
        String room2 = UI.askString("Enter second room code (eg AM104):").toUpperCase();
        String day = this.askDay();
        UI.clearText();
        this.printInRoomsOnDay(room1, room2, day);
    }

    public void printInRoomsOnDay(String targetRoom1, String targetRoom2, String targetDay){
        UI.printf("Classes in %s or %s on %s%n", targetRoom1, targetRoom2, targetDay);
        UI.println("==========================================");
        long t1 = System.nanoTime();
        ArrayList<Long> dayClasses = HashManager.GetIndex(targetDay); //Get the position of the lines of the classdata file that match the search query
        ArrayList<Long> room1Classes = HashManager.GetIndex(targetRoom1);
        ArrayList<Long> room2Classes = HashManager.GetIndex(targetRoom2);
        ArrayList<Long> conditionClasses = new ArrayList<>();

        for(long l : dayClasses) //Add all class indexes that are in either room on the target day
            if(room1Classes.contains(l) || room2Classes.contains(l)) //Check if two file positions are the same indicating the same entry
                conditionClasses.add(l);

        FileInputStream fs = openFileStream();

        int count = 0;

        for(long l : conditionClasses)
        {
            try {
                fs.getChannel().position(l); //Jump to the line position in the file
                StringBuilder lineBuilder = new StringBuilder(); //Build line char by char using a string builder
                boolean end = false;
                while(fs.available() > 0) //Read each byte as character from file. Note: Only encoding supported is ASCII
                {
                    char c = (char)fs.read(); //read byte as character
                    if(c != '\n')
                        lineBuilder.append(c);
                    else { //Line has ended
                        if(HashManager.MatchIndex(lineBuilder.toString(), targetDay, 2) && //Check if the token at index 2 matches the target day
                                (HashManager.MatchIndex(lineBuilder.toString(), targetRoom1, 5) || //Check if the token at index 5 matches room 1
                                        HashManager.MatchIndex(lineBuilder.toString(), targetRoom2, 5))) {
                            new uniClass(lineBuilder.toString()).print("type end day room"); //parse and print data except type, end time, day and room
                            count++;
                        }
                        end = true;
                        break;
                    }
                }

                //If there was not a newline at the end of the file, check if the last line matches the query
                if(!end && HashManager.MatchIndex(lineBuilder.toString(), targetDay, 2) &&
                        (HashManager.MatchIndex(lineBuilder.toString(), targetRoom1, 5) ||
                                HashManager.MatchIndex(lineBuilder.toString(), targetRoom2, 5))) {
                    new uniClass(lineBuilder.toString()).print("type end day room");
                    count++;
                }
            }
            catch (Exception ex)
            {
                UI.printf("Hash table search error: %s%n", ex);
            }
        }

        long time = System.nanoTime() - t1; //Stop timing

        try { fs.close(); } catch(Exception ex){}
        if(count == 0)
            UI.printf("There are no classes in %s or %s on %s%n", targetRoom1, targetRoom2, targetDay);
        UI.println(count + " results returned.");
        UI.printf("Query completed in: %.2fms%n", time / 1000000f);
        UI.println("=========================");
    }


    /** Core 5
     * Writes a new file listing all the class bookings that are in a given room.
     *  The name of the new file should be the room, followed by "_Bookings.txt"
     *  The first line of the file should specify what room the bookings are for:
     *  "Bookings for room <room name>"
     *  
     *  Each class booking should be formatted in three lines, with a blank line after.
     *  Course: <Course Code>
     *  Time: <Day> <Start Time>-<End Time>
     *  Session: <Type>
     *
     *  For example, if the targetRoom is VZ515, then the start of the file would be as follows
     * 
     *  Bookings for room VZ515
     *  ----------------------------------
     *  Course: ACCY111
     *  Time: Tue 1000-1050
     *  Session: Tutorial
     *  
     *  Course: ACCY130
     *  Time: Thu 1310-1400
     *  Session: Tutorial
     *  
     *  Course: ACCY130
     *  Time: Tue 1310-1400
     *  Session: Tutorial
     *  
     */
    public void doBookingsFileForRoom() {
        String room = UI.askString("Enter room code (eg AM102):").toUpperCase();;
        UI.clearText();
        this.bookingsFileForRoom(room);
    }

    public void bookingsFileForRoom(String targetRoom){
        UI.println("Generating room booking file for " + targetRoom);
        FileInputStream fs = openFileStream();
        File outputF = new File(targetRoom + "_Bookings.txt");
        long t1 = System.nanoTime();
        ArrayList<String> lines = HashManager.GetByIndex(targetRoom, 5, fs);
        long time = System.nanoTime() - t1;

        try {
            outputF.createNewFile();
            PrintWriter fw = new PrintWriter(outputF);

            if (lines.size() == 0)
                UI.println("No courses are booked in " + targetRoom);
            else
                for (String ln : lines) {
                    uniClass c = new uniClass(ln);
                    fw.write("Course: "  + c.Course + "\r\n");
                    fw.write("Time: "  + c.Day + " " + c.StartTime + "-" + c.EndTime + "\r\n");
                    fw.write("Session: "  + c.Type + "\r\n\r\n");
                }
            fw.close();
        }
        catch (Exception ex)
        {
            UI.println("File write error: " + ex);
        }
        UI.printf("Query completed in: %.2fms%n", time / 1000000f);
        try { fs.close(); } catch(Exception ex){}
        UI.println("Printed to "+targetRoom+"_Bookings.txt");
        UI.println("=========================");
    }

    /** Core 6
     * Lists all the classes (just list the course code, type, and room) that are
     *  in the specified building (given by its abbreviation)
     *  on the specified day, and start at or after the specified start time
     * Note, the first part of every room name is an uppercase abbreviation of the building.
     */
    public void doInBuildingAfterTime() {
        String building = UI.askString("Enter building (eg HM):").toUpperCase();
        String day = this.askDay();
        int time = UI.askInt("Enter start time (eg 1315):");
        UI.clearText();
        this.inBuildingAfterTime(building, day, time);
    }

    public void inBuildingAfterTime(String targetBuilding, String targetDay, int targetStart){
        UI.printf("Classes in %s on %s after %d\n", targetBuilding, targetDay, targetStart);
        UI.println("============================");
        FileInputStream fs = openFileStream();
        long t1 = System.nanoTime();
        ArrayList<String> lines = HashManager.GetByIndex(targetDay, 2, fs);
        long time = System.nanoTime() - t1;

        if (lines.size() == 0)
            UI.println("No courses are booked on " + targetDay);
        else
            for (String ln : lines) {
                uniClass c = new uniClass(ln);

                if(c.Room.substring(0,2).equalsIgnoreCase(targetBuilding) &&
                        c.StartTime >= targetStart)
                {
                    c.print("start end day");
                }
            }

        UI.printf("Query completed in: %.2fms%n", time / 1000000f);
        try { fs.close(); } catch(Exception ex){}
        UI.println("=========================");
    }

    /** Completion 1
     * Computes and returns the average (mean) duration in minutes of all classes scheduled
     * in a specified room.
     * Hint: be careful with the times
     * Hint: if there are no classes in the room, do not cause an error.
     */
    public void doMeanClassLength() {
        String room = UI.askString("Enter room code (eg AM102):").toUpperCase();
        UI.clearText();
        double mean = this.meanClassLength(room);
        if (mean == 0) {
            UI.printf("There were no classes in  %s%n", room);
        }
        else {
            UI.printf("Average duration in %s = %4.2f mins%n",
                room,  mean);
        }
        UI.println("=========================");
    }

    public double meanClassLength(String targetRoom){
        FileInputStream fs = openFileStream();
        long t1 = System.nanoTime();
        ArrayList<String> lines = HashManager.GetByIndex(targetRoom, 5, fs);
        long time = System.nanoTime() - t1;

        double total = 0;

        if(lines.size() == 0)
            return 0;
        else
        {
            for(String ln : lines)
            {
                uniClass c = new uniClass(ln); //Parse
                int start = c.StartTime;
                int end  = c.EndTime;

                int difference = (end % 100 + (end / 100) * 60) - (start % 100 + (start / 100) * 60); //Convert time to minutes then subtract start from end to get difference.
                total += difference;
            }
            total /= lines.size();
        }
        UI.printf("Query completed in: %.2fms%n", time / 1000000f);
        try { fs.close(); } catch(Exception ex){}
        return total;
    }

    /** Completion 2
     * Lists all the courses (just the course code) that have classes in a given building
     * on a given day such that any part of the class is between the given times.
     * Each course involved should only be listed once, even if it has several classes
     * in the building in the time period.  Note, the data file is ordered by the course code.
     * Note that this is similar, but not the same as, one of the completion questions.
     */
    public void doPotentialDisruptions(){
        UI.clearText();
        String building = UI.askString("Enter a building code(eg AM):").toUpperCase();
        String day = this.askDay();
        int start = UI.askInt("What is the start time?");
        int end = UI.askInt("What is the end time?");
        this.potentialDisruptions(building, day, start, end);
    }

    public void potentialDisruptions(String building, String targetDay, int targetStart, int targetEnd){
        UI.printf("\nClasses in %s on %s between %d and %d%n",
            building, targetDay, targetStart, targetEnd);
        UI.println("=================================");
        FileInputStream fs = openFileStream();
        long t1 = System.nanoTime();
        ArrayList<String> lines = HashManager.GetByIndex(targetDay, 2, fs);
        long time = System.nanoTime() - t1;
        ArrayList<String> printed = new ArrayList<>();

        if(lines.size() == 0)
            UI.println("There are no classes on " + targetDay);
        else
        {
            for(String ln : lines)
            {
                uniClass c = new uniClass(ln); //Parse
                if(c.Room.substring(0, 2).equalsIgnoreCase(building) &&
                        ((c.StartTime < targetEnd && c.StartTime > targetStart) || (c.EndTime < targetEnd && c.EndTime > targetStart)) && //Check if tine falls within
                        !printed.contains(c.Course)){ //If the course has not already been printed
                    UI.println(c.Course);
                    printed.add(c.Course); //Add course to printed list
                }
            }
        }
        UI.printf("Query completed in: %.2fms%n", time / 1000000f);
        try { fs.close(); } catch(Exception ex){}
        UI.println("=========================");
    }

    /** Challenge 1
     * Lists all the students who have a course that has a lecture on the specified day.
     */
    public void doAllStudentsOnDay() {
        String day = this.askDay();
        UI.clearText();
        this.allStudentsOnDay(day);
        UI.println("=========================");
    }

    public void allStudentsOnDay(String targetDay){
        UI.printf("All students that have a lecture on %s%n", targetDay);
        UI.println("=================================");
        FileInputStream fs = openFileStream();
        long t1 = System.nanoTime();
        ArrayList<String> lines = HashManager.GetByIndex(targetDay, 2, fs);
        long time = System.nanoTime() - t1;
        ArrayList<String> classes = new ArrayList<>();

        if(lines.size() == 0)
            UI.println("There are no classes on " + targetDay);
        else
        {
            for(String ln : lines)
            {
                uniClass c = new uniClass(ln); //Parse
                if(c.Type.equalsIgnoreCase("lecture"))
                    classes.add(c.Course);
            }

            if(classes.size() == 0)
            {
                UI.println("There are no lectures on " + targetDay);
                return;
            }

            try {
                File studntFile = new File("studentcourses.txt");
                Scanner sc = new Scanner(studntFile);
                while(sc.hasNext())
                {
                    Scanner lnSc = new Scanner(sc.nextLine());
                    String id = lnSc.next(); //Get student ID
                    while(lnSc.hasNext()) //Loop through courses
                    {
                        String course = lnSc.next(); //Get course
                        if(classes.contains(course)) {
                            UI.println(id);
                            break; //Break so student doesnt get listed twice
                        }
                    }
                    lnSc.close();
                }
                sc.close();
            }
            catch(IOException ex)
            {
                UI.printf("Error reading student course file: %s", ex);
            }
        }
        UI.printf("Query completed in: %.2fms%n", time / 1000000f);
        try { fs.close(); } catch(Exception ex){}
    }

    /** Challenge 2
     * Display the weekly timetable of the target student in the graphics pane.
     * The timetable should have a column for each day, and a row for each half hour, from 8:00 to 19:30. 
     * The timetable should have the course code in every cell that the student has a lecture.
     */
    public void doStudentTimetable() {
        int studentID = UI.askInt("Enter the ID number of a student (eg 1004):");
        UI.clearPanes();
        this.studentTimetable(studentID);
        UI.println("=========================");
    }

    class graphicsString
    {
        public String str;
        public double x;
        public double y;

        public graphicsString(String _str, double _x, double _y)
        {
            str = _str;
            x = _x;
            y = _y;
        }
    }

    public void studentTimetable(int id){
        try
        {
            double minuteUnit = TIMETABLE_HEIGHT / 70.0; //Height of one 10 minute block in the timetable. 73 is the number of 10 minute blocks inbetween and including 800-1930
            double dayUnit = TIMETABLE_WIDTH / 5.0; //Width of one day block in the timetable
            String[] days = new String[] {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

            boolean foundStudent = false; //If student exists

            FileInputStream fs = openFileStream();
            File file = new File("studentcourses.txt");
            Scanner sc = new Scanner(file);

            ArrayList<graphicsString> courseNames = new ArrayList<>();

            while(sc.hasNext())
            {
                String line = sc.nextLine();
                Scanner lnsc = new Scanner(line);
                if(lnsc.nextInt() != id) //If the student ID does not match the requested ID, skip
                    continue;

                foundStudent = true;

                while(lnsc.hasNext()) //Loop through each course for this student
                {
                    //30 + 193
                    String course = lnsc.next();
                    ArrayList<String> lines = HashManager.GetByIndex(course, 0, fs);

                    for(String cLine : lines) {
                        uniClass c = new uniClass(cLine);
                        if(!c.Type.equalsIgnoreCase("lecture"))
                            continue;;
                        int startMins = ((c.StartTime % 100 + (c.StartTime / 100) * 60) - 480) / 10; //Convert the start time to minutes then subtract timetable starting offset 0800 in minutes = 480. Divide by 10 to split into 10 minute blocks for the timetable
                        int endMins = ((c.EndTime % 100 + (c.EndTime / 100) * 60) - 480) / 10;

                        UI.setColor(Color.red.darker().darker().darker());
                        UI.fillRect(TIMETABLE_LEFT + (dayUnit * dayToInt(c.Day)),
                                TIMETABLE_TOP + (minuteUnit * startMins),
                                dayUnit,
                                (endMins - startMins) * minuteUnit);

                        courseNames.add(new graphicsString(course, TIMETABLE_LEFT + (dayUnit * dayToInt(c.Day)) - (StrWidth(course) / 2) + (dayUnit / 2), //Add course name graphic to a list so it can be drawn above the grid lines.
                                TIMETABLE_TOP + (minuteUnit * startMins) + (minuteUnit * (endMins - startMins) / 2) + (fMetrics.getHeight() / 4)));
                    }
                }
            }
            sc.close();
            fs.close();


            UI.setColor(Color.black);

            for(int i = 0; i < 5; i++) {
                UI.drawString(days[i], TIMETABLE_LEFT + (dayUnit * i) + (TIMETABLE_WIDTH / 10) - (StrWidth(days[i]) / 2), TIMETABLE_TOP - 3); //Draw day headers
                UI.drawLine((dayUnit * i) + TIMETABLE_LEFT, TIMETABLE_TOP, (dayUnit * i) + TIMETABLE_LEFT, TIMETABLE_TOP + TIMETABLE_HEIGHT - minuteUnit); //Draw vertical day seperating lines
            }

            for(int i = 0; i < 24; i++) { //23 blocks of 30 mins between start and end time
                int mins = ((i * 30) + 480);
                String time = String.valueOf (mins / 60) + ":" + String.format("%2d", mins % 60).replace(" ", "0");
                UI.drawString(time, TIMETABLE_LEFT - StrWidth(time) - 3, TIMETABLE_TOP + (i * minuteUnit * 3) + (fMetrics.getHeight() / 4)); //Draw time headers
                UI.drawLine(TIMETABLE_LEFT, TIMETABLE_TOP + (i * minuteUnit * 3), TIMETABLE_LEFT + TIMETABLE_WIDTH, TIMETABLE_TOP + (i * minuteUnit * 3)); //Draw horizontal time seperating lines
            }

            UI.drawRect(TIMETABLE_LEFT, TIMETABLE_TOP, TIMETABLE_WIDTH, TIMETABLE_HEIGHT - minuteUnit);
            UI.setColor(Color.green);

            for(graphicsString g : courseNames)
                UI.drawString(g.str, g.x, g.y);

            if(foundStudent)
                UI.println("Timetable for: " + id);
            else
                UI.println("There is no timetable for: " + id);
        }
        catch (IOException ex)
        {
            UI.println("IO error during student timetable generation: " + ex);
        }
    }

    private static int StrWidth(String txt)
    {
        if(fMetrics == null)
            fMetrics =  UI.getGraphics().getFontMetrics();
        return fMetrics.stringWidth(txt);
    }

    public int dayToInt(String day)
    {
        switch(day)
        {
            case "Mon":
                return 0;
            case "Tue":
                return 1;
            case "Wed":
                return 2;
            case "Thu":
                return 3;
            case "Fri":
                return 4;
            default:
                return -1;
        }
    }

    /** Asks the user for a Day and returns as a capitalised three letter string */
    public String askDay(){
        String day;
        while (true) {
            day = UI.askString("Enter first 3 letters of day (eg Mon):");
            if (day.length()>=3){ break; }
            UI.println("You must enter at least three letters of the day.");
        }
        return day.substring(0,1).toUpperCase() + day.substring(1,3).toLowerCase();
    }


}
