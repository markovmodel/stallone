/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.util;

/**
 *
 * @author noe
 */
import java.io.*;

public class JobTools
{

    public static void sleep(long millis)
    {
        try
        {
            Thread.currentThread().sleep(millis);
        }
        catch (InterruptedException e)
        {
        }
    }

    public static boolean isAlive(Process p)
    {
        try
        {
            p.exitValue();
            return (false);
        }
        catch (IllegalThreadStateException e)
        {
            return (true);
        }
    }

    public static void executePoor(String wdir, String command, String strInputFile, String strOutputFile)
            throws Exception
    {
        String strStarter = wdir + "/___starter" + MathTools.randomInt(0, Integer.MAX_VALUE) + ".exe";

        // writing the starter
        FileOutputStream starter = new FileOutputStream(strStarter);
        String cmdfull = "cd " + wdir + "\n" + command;
        if (strInputFile != null)
        {
            cmdfull = cmdfull + " < " + strInputFile;
        }
        if (strOutputFile != null)
        {
            cmdfull = cmdfull + " > " + strOutputFile;
        }
        cmdfull = cmdfull + " \n";
        starter.write(cmdfull.getBytes());

        // making the starter executable
        Process p = Runtime.getRuntime().exec("chmod 700 " + strStarter);
        p.waitFor();
        p.getErrorStream().close();
        p.getInputStream().close();
        p.getOutputStream().close();
        p.destroy();

        // execute and wait
        p = Runtime.getRuntime().exec("bash " + strStarter);
        p.waitFor();
        p.getErrorStream().close();
        p.getInputStream().close();
        p.getOutputStream().close();
        p.destroy();

        // delete starter
        starter.close();
        new File(strStarter).delete();
    }

    public static String writeStarter(String wdir, String content)
            throws Exception
    {
        String strStarter = wdir + "/___starter" + MathTools.randomInt(0, Integer.MAX_VALUE) + ".exe";
        FileOutputStream starter = new FileOutputStream(strStarter);
        starter.write(content.getBytes());
        starter.close();

        // making the starter executable
        Process p = Runtime.getRuntime().exec("chmod 700 " + strStarter);
        p.waitFor();

        return (strStarter);
    }

    /**
    Generates a temporary directory, copies the needed files into it.
    Returns the name of the directory. It is left to the user to remove the directory
    when it is no longer needed.
    @param neededFiles a list of file (full pathname) which are needed in the directory
    to execute the command
    @param wdir the directory in which the temporary subdirectory should be generated,
    e.g. "." or "tmp".
    @return the name of the generated directory.
     */
    public static String tempDir(String[] neededFiles, String wdir)
            throws Exception
    {
        int id = 0;
        File tmpdir = null;
        String dirname = null;

        // determine id and directory name.
        do
        {
            id = MathTools.randomInt(0, Integer.MAX_VALUE);
            dirname = wdir + "/___tmp_" + id;
            tmpdir = new File(dirname);
        } while (tmpdir.exists());

        // generate directory
        tmpdir.mkdir();

        // copy files into directory, if available
        if (neededFiles != null)
        {
            for (int i = 0; i < neededFiles.length; i++)
            {
                execute("cp " + neededFiles[i] + " " + dirname);
            }
        }

        return (dirname);
    }

    public static void execute(String command)
    {
        try
        {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            p.getErrorStream().close();
            p.getInputStream().close();
            p.getOutputStream().close();
            p.destroy();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void execute(String command, String strInputFile, String strOutputFile)
            throws IOException
    {
        Process p = startJob(command, strInputFile, strOutputFile);

        try
        {
            p.waitFor();
            p.getErrorStream().close();
            p.getInputStream().close();
            p.getOutputStream().close();
            p.destroy();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
    Starts a new job which reads the given input file from stdin
    and writes to the given output file from stdout.
     */
    public static Process startJob(String command, String strInputFile, String strOutputFile)
            throws IOException
    {
        Process p = Runtime.getRuntime().exec(command);
        FileInputStream inFile = new FileInputStream(strInputFile);
        byte[] byteIn = new byte[inFile.available()];
        inFile.read(byteIn);
        inFile.close();

        FileOutputStream outFile = new FileOutputStream(strOutputFile);

        // stream input file into Charmm process
        OutputStream pOut = p.getOutputStream();
        pOut.write(byteIn);
        pOut.flush();
        
        readToEnd(p.getInputStream(), outFile);

        return (p);
    }

    /**
    read from the input stream and writes to the output stream as long
    as something is available.
     */
    public static void readToEnd(InputStream in, OutputStream out)
    {
        try
        {
            int c = 0;
            while (c != -1)
            {
                c = in.read();
                System.out.print((char) c);
                out.write(c);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
