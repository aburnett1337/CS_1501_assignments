/**
 * Auto Completer engine
 * Written by Austin Burnett
 * Last Edited 10/19/2021
 */
package cs1501_p2;
import java.io.*;
import java.util.ArrayList;


public class AutoCompleter implements AutoComplete_Inter{

    /**
     * Global Variables:
     * currString:  the string saved in memory that represents the user string inputted
     * dlb:         the dictionary saved inside of a DLB
     * uh:          the userHistory saved in a DLB
     */

    private String currString = "";
    private DLB dlb;
    private UserHistory uh;

    /**
     * Constructor that feeds in only 1 input dict, and creates a DLB and feeds in all strings
     * in dict
     * @param dict file/filepath pointing to a dictionary text file
     */
    public AutoCompleter(String dict){
        dlb = new DLB();
        uh = new UserHistory();
        try{
        BufferedReader infile = new BufferedReader( new FileReader (dict) );
        while(infile.ready()){
            String str = infile.readLine();
            dlb.add(str);
        }
        infile.close();
        }
        catch (Exception e){
            System.out.println("AN ERROR OCCURED");
        }
    }

    /**
     * Constructor that takes in dict and userHist and populates both dlb and uh
     * @param dict file/filepath pointing to a dictionary text file
     * @param userHist file/filepath pointing to a user history text file
     */
    public AutoCompleter(String dict, String userHist){
        dlb = new DLB();
        uh = new UserHistory();
        try{
        BufferedReader dictFile = new BufferedReader( new FileReader (dict) );
        BufferedReader uhFile = new BufferedReader( new FileReader (userHist) );
        while(dictFile.ready()){
            String str = dictFile.readLine();
            dlb.add(str);
        }
        while(uhFile.ready()){
            String str = uhFile.readLine();
            uh.add(str);
        }
        dictFile.close();
        uhFile.close();
    }
    catch(Exception e){
        System.out.println("AN ERROR OCCURED");
    }
    }

    /**
     * the backbone for creating the suggested arrayList. If the user Suggestion is less than 5,
     * pull from the dictionary until the size is 5
     * @param next char the next character the user inputted
     * @return the suggested dictionary or usersuggestion arraylist
     */
    public ArrayList<String> nextChar(char next){
        uh.searchByChar(next);
        dlb.searchByChar(next);
        String uhCurr = uh.currString;
        String dlbCurr = dlb.currString;
        ArrayList<String> userSugg = uh.suggest();
        ArrayList<String> dictSugg = dlb.suggest();
        if(userSugg != null){
            for(int i = 0; i < userSugg.size(); i++){
                if(uhCurr.length() > 0){
                    String str = uhCurr.substring(0, uhCurr.length() - 1) + userSugg.get(i);
                    userSugg.set(i, str);
                } 
            }
        }
        for(int i = 0; i < dictSugg.size(); i++){
            if(dlbCurr.length() > 0){
                String str = dlbCurr.substring(0, dlbCurr.length() - 1) + dictSugg.get(i);
                dictSugg.set(i, str);
            } 
        }

        if(userSugg == null) return dictSugg;
        int i = 0;
        while(userSugg.size() < 5){
            if( !(userSugg.contains(dictSugg.get(i))) ) userSugg.add(dictSugg.get(i));
            i++;
        }
        return userSugg;
    }

    /**
     * states that the user selected a finished word. Resets the currString in uh and dlb
     * and adds that new word into the uh DLB
     * @param cur
     */
    public void finishWord(String cur){
        uh.resetByChar();
        dlb.resetByChar();
        uh.add(cur);
        return;
    }

    /**
     * writes out the uh DLB into a file 
     * @param fname filename of the file you want to write to
     */
    public void saveUserHistory(String fname){
        try{
            File uhFile = new File(fname);
            FileWriter writer = new FileWriter(fname);

            ArrayList<String> userHistory = new ArrayList<String>();
            userHistory = uh.traverse();

            for(int i = 0; i < userHistory.size(); i++){
                writer.write(userHistory.get(i) + "\n");
            }
            writer.close();
        }
        catch (Exception e){
            System.out.println("AN ERROR OCCURED");
        }
        


    }

}