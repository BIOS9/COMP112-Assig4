// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP112 - 2018T1, Assignment 4
* Name: Matthew Corfiatis
* Username: CorfiaMatt
* ID: 300447277
*/

import ecs100.*;

public class RunClassTimes {

    public static void main(String[] args){
        ClassTimes ct = new ClassTimes();
        UI.addButton("Print All", ct::printAll);

        // Methods for the core
        UI.addButton("Course", ct::doPrintCourse );
        UI.addButton("Room", ct::doPrintRoom );
        UI.addButton("StartTime", ct::doPrintAtStartTime) ;
        UI.addButton("Rooms On Day", ct::doPrintInRoomsOnDay );
        UI.addButton("Room Booking File", ct::doBookingsFileForRoom );
        UI.addButton("In Building After", ct::doInBuildingAfterTime );

        //Methods for the completion
        UI.addButton("Mean Class Length", ct::doMeanClassLength );
        UI.addButton("Potential Disruptions", ct::doPotentialDisruptions );

        //Methods for the challenge
        UI.addButton("All students On Day", ct::doAllStudentsOnDay );
        UI.addButton("Student Timetable", ct::doStudentTimetable );

        UI.setDivider(1.0);
        UI.addButton("Quit", UI::quit);
    }
}
