/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.io;

import java.io.*;
import java.util.logging.*;

/**
 *
 * @author noe
 */
public class Log
{
    private final static Logger LOGGER = Logger.getLogger("Central Logger");

    static
    {
        try
        {
            LOGGER.addHandler(new FileHandler("_stallone.log"));
        }
        catch (IOException e)
        {
            LOGGER.addHandler(new ConsoleHandler());
        }
    }
    
    public static void logError(String message)
    {
        LOGGER.log(Level.SEVERE, message);
    }
    
}
