package stallone.api.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import stallone.io.RecursiveFileList;
import stallone.io.WildcardFilter;

/**
 * Class FileUtil.
 *
 * @author  Martin Senne, Frank Noe
 */
public class IOUtilities
{
    public static final Logger logger = Logger.getLogger(IOUtilities.class.getName());

    public void error(String msg)
    {
        Thread.dumpStack();
        System.out.println();
        System.out.println("ERROR: "+msg);
        System.exit(-1);
    }

    public List<File> listFilesRecursive(String startingDirectory, String filter)
            throws FileNotFoundException
    {
        return (new RecursiveFileList(startingDirectory, filter)).getFiles();
    }

    public List<File> listFilesRecursive(File startingDirectory, String filter)
            throws FileNotFoundException
    {
        return (new RecursiveFileList(startingDirectory, filter)).getFiles();
    }

    public List<File> listFilesRecursive(String startingDirectory)
            throws FileNotFoundException
    {
        return (new RecursiveFileList(startingDirectory)).getFiles();
    }

    /**
     * Recursively walk a directory tree and return a List of all
     * Files found; the List is sorted using File.compareTo().
     *
     * @param aStartingDir is a valid directory, which can be read.
     */
    public List<File> listFilesRecursive(File startingDirectory)
            throws FileNotFoundException
    {
        return (new RecursiveFileList(startingDirectory)).getFiles();
    }


    public List<File> listFiles(String[] wildcardFilenames)
    {
        List<File> foundFiles = new ArrayList<File>();

        for (String wildcardFilename : wildcardFilenames)
        {
            foundFiles.addAll(listFiles(wildcardFilename));
        }

        return foundFiles;
    }

    public List<File> listFiles(String wildcardFilename)
    {
        List<File> foundFiles = new ArrayList<File>();
        File f = new File(wildcardFilename);
        File parent = f.getParentFile();

        if (parent == null)
        {
            parent = new File(".");
        }

        // logger.info("Retrieving files matching '" + f.getName() + "' in '" + parent + "'");

        File[] filesInDir = parent.listFiles(new WildcardFilter(f.getName()));

        for (File foundFile : filesInDir)
        {
            foundFiles.add(foundFile);
        }

        return foundFiles;
    }

    public List<String> listFileNames(String wildcardFilename)
    {
        return toStrings(listFiles(wildcardFilename));
    }

    /**
     * Determine file extension.
     *
     * @param   filename  to analyse for file extension.
     *
     * @return  extension
     */
    public String getExtension(String filename)
    {
        int lastIndex = filename.lastIndexOf(".");

        return filename.substring(lastIndex + 1);
    }

    public String getExtension(File f)
    {
        return getExtension(f.getName());
    }

    public String getBasename(File f)
    {
        return getBasename(f.getName());
    }

    public String getBasename(String fullname)
    {
        String filename = (new File(fullname)).getName();
        int index = filename.lastIndexOf('.');
        if (index == -1)
        {
            return filename;
        }
        else
        {
            return filename.substring(0, index);
        }
    }

    public String getFilename(String fullname)
    {
        return (new File(fullname)).getName();
    }

    public String getDirectory(String fullname)
    {
        if (!fullname.contains("/"))
            return(".");
        else
            return fullname.substring(0,fullname.lastIndexOf("/"));
    }

    public boolean canCreateFile(String filename)
    {
        IO.util.error ("Implement me: IOUtilities.canCreateFile(String)");
        return true;
    }

    public List<String> toStrings(List<File> files)
    {
        ArrayList<String> res = new ArrayList<String>();
        for (File f : files)
            res.add(f.getAbsolutePath());
        return(res);
    }

    public List<File> toFiles(List<String> files)
    {
        ArrayList<File> res = new ArrayList<File>();
        for (String f : files)
            res.add(new File(f));
        return(res);
    }

    public void writeString(String fileName, String content)
    {
	try
	    {
		FileOutputStream out = new FileOutputStream(fileName);
		out.write(content.getBytes());
		out.close();
	    }
	catch(IOException e)
	    {
		e.printStackTrace();
		System.exit(-1);
	    }
    }

}
