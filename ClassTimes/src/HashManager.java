/* Code for COMP112 - 2018T1, Assignment 4
 * Name: Matthew Corfiatis
 * Username: CorfiaMatt
 * ID: 300447277
 */

import ecs100.UI;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.zip.CRC32;


/**
 * Hash manager class to help with creating checksums for files and hashes for misc data.
 * Author: Matthew Corfiatis
 */
public class HashManager {

    static Map<String, ArrayList<Long>> table = new HashMap<>();

    /**
     * Searches file based on key and type of key specified
     * Uses hash table based searching
     * @param key Value to look for
     * @param index Type of key/index of the token to be checked on each line
     * @param fs The file stream for the file to be searched
     * @return The list of entries matching the search
     */
    public static ArrayList<String> GetByIndex(String key, int index, FileInputStream fs)
    {
        ArrayList<String> validLines = new ArrayList<>();
        if(!table.containsKey(key))
            return new ArrayList<>();
        ArrayList<Long> lines = table.get(key);
        for(long l : lines)
        {
            try {
                fs.getChannel().position(l);
                StringBuilder lineBuilder = new StringBuilder();
                boolean end = false;
                while(fs.available() > 0) //Read each byte as character from file. Note: Only encoding supported is ASCII
                {
                    char c = (char)fs.read();
                    if(c != '\n')
                        lineBuilder.append(c);
                    else {
                        if(MatchIndex(lineBuilder.toString(), key, index))
                            validLines.add(lineBuilder.toString());
                        end = true;
                        break;
                    }
                }

                if(!end && MatchIndex(lineBuilder.toString(), key, index))
                    validLines.add(lineBuilder.toString());
            }
            catch (Exception ex)
            {
                UI.printf("Hash table search error: %s%n", ex);
            }
        }
        return validLines;
    }

    /**
     * Gets file byte index from search query
     * @param key Key to search hash table for
     * @return List of bytes integers as longs
     */
    public static ArrayList<Long> GetIndex(String key)
    {
        return table.get(key);
    }

    /**
     *  Checks if the specified token in a line matches the specified value
     * @param line Line to check
     * @param key Value to match
     * @param index Token index to check
     * @return
     */
    public static boolean MatchIndex(String line, String key, int index)
    {
        Scanner sc = new Scanner(line);
        for(int i = 0; i < index; i++)
            sc.next(); //Skip over tokens until we reach the index
        return sc.next().equals(key);
    }

    /**
     * Adds a key and value to the hash table.
     * Ensures that if there are two values with the same key, they will be both added to an array.
     * @param key Key of value
     * @param lineOffset Value to store
     */
    private static void AddHash(String key, long lineOffset)
    {
        if(table.containsKey(key))
            table.get(key).add(lineOffset); //Add value to the array inside the table if there is already one or more values
        else {
            //Create new array if no values already exist
            ArrayList<Long> l = new ArrayList<>();
            l.add(lineOffset);
            table.put(key, l);
        }
    }

    /**
     * Generates an easily searchable hash table.
     * @param fileName File to generate table for
     */
    public static void GenerateHashTable(String fileName)
    {
        try
        {
            File file = new File(fileName);
            FileInputStream fs = new FileInputStream(file); //Open file stream

            StringBuilder lineBuilder = new StringBuilder(); //StringBuilder to store the current line
            long lastLinePos = 0; //Stores last line byte position
            while(fs.available() > 0) //Read each byte as character from file. Note: Only encoding supported is ASCII
            {
                char c = (char)fs.read(); //Read one character from file
                if(c != '\n')
                    lineBuilder.append(c); //Append to line if the character is not a new line
                else
                {
                    Scanner sc = new Scanner(lineBuilder.toString());
                    AddHash(sc.next(), lastLinePos); //Add index for course name
                    sc.next(); //Skip class type
                    AddHash(sc.next(), lastLinePos); //Add index for day
                    AddHash(sc.next(), lastLinePos); //Add index for start time
                    sc.next(); //Skip end time
                    AddHash(sc.next(), lastLinePos); //Add index for class
                    sc.close();

                    lastLinePos = fs.getChannel().position(); //Set the last line position to the current stream position
                    lineBuilder.setLength(0); //Clear line buffer/string builder
                }
            }
        }
        catch (Exception ex)
        {
            UI.printf("Hash table generation error for file '%s' - %s%n", fileName, ex);
        }
    }
}
